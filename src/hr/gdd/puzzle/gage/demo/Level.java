package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.util.Log;
import android.view.View;

public class Level extends CCLayer implements Observer
{
	public static final int LEVEL_TIME = 60;
	public static final float LEVEL_TIME_INTERVAL = 1.0f;
	public static final String LEVEL_TIME_SELECTOR = "secondPassed";
	
	//-----------------Fields
	private LevelPhase _state; 
	private LevelPhase _lastState;
	private int _score;
	private int _combo;
	private int _moves = 0;
	private int _allowedMoves = 0;
	private Background _back;
	private PGDisplay _displ;
	private BlockField _field;
	private ArrayList<BlockConfig> _config;
	private CGSize _screenBounds;
	private Orientation _currOrientation;
	private Control _control;
	private int _currTime = LEVEL_TIME;
	
	//-----------------Level constructor
	public Level(Background bg, BlockField bf, PGDisplay d, Control c)
	{
		this._back = bg;
		this._field = bf;
		this._displ = d;
		
		this._config = new ArrayList<BlockConfig>();
		this._screenBounds = CCDirector.sharedDirector().displaySize();
		
		this._control = c;
	}
	
	//-----------------Getter methods
	//Obtain the display interface instance
	public PGDisplay getDisplay()
	{
		return this._displ;
	}
	
	//Obtain the background instance
	public Background getBack()
	{
		return this._back;
	}
	
	//Obtain the blocks field instance
	public BlockField getField()
	{
		return this._field;
	}
	
	//Obtain the block configurator list
	public ArrayList<BlockConfig> getConfig()
	{
		return this._config;
	}
	
	//Obtain the current score for the level
	public int getScore()
	{
		return this._score;
	}
	
	//Get the current combo that is being made in the level
	public int getCombo()
	{
		return this._combo;
	}
	
	//-----------------Setter methods
	public void setAllowedMoves(int moves)
	{
		this._allowedMoves = moves;
	}
	
	//Overload for setting the display interface only
	public void setDisplay(PGDisplay display)
	{
		this._displ = display;
	}
	
	//Set the level's background instance
	public void setBackground(Background back)
	{
		this._back = back;
	}
	
	//Set the blockfield instance to be associated with the level
	public void setField(BlockField field)
	{
		this._field = field;
	}
	
	//Set the level's current score
	public void setScore(int score)
	{
		this._score = score;
		this._displ.modifyLabelsTextByType(DisplayLabel.Score, ""+this._score);
	}
	
	//Set the level's current combo
	public void setCombo(int combo)
	{
		this._combo = combo;
		this._displ.modifyLabelsTextByType(DisplayLabel.Combo, ""+this._combo);
	}
	
	//-----------------Other methods
	//Add a block configurator to the level 
	public void addConfig(BlockConfig c)
	{
		this._config.add(c);
	}
	
	//Overload for adding multiple block configurators
	public void addConfig(ArrayList<BlockConfig> bc)
	{
		this._config.addAll(bc);
	}
	
	//Delaying setup does work?
	public void delayedSetup()
	{
		//Set the level's phase to its setup phase
		this._state = LevelPhase.Setup;
		
		this.schedule("timercall", 1.0f);
	}
	
	public void moveMade()
	{
		this._moves++;
		this._displ.modifyLabelsTextByType(DisplayLabel.Moves, this._moves+"/"+this._allowedMoves);
	}
	
	//Call for handling the timer
	public void timercall(float dt)
	{
		this.unschedule("timercall");
		this.setup(Orientation.Portrait);
	}
	
	//Start the setup phase of the level and load its components
	public void setup(Orientation orientation)
	{	
		//Add the level as an observer to the instances it needs to receive updates from
		this._control.addObserver(this);
		this._field.addObserver(this);
		
		//Add background, blockfield and display interface
		this.addChild(this._back.getSprite());
		this.addChild(this._field.getSprite());
		this.addChild(this._displ.getSprite());
		
		//Rearrange the location of the elements according to the screen's orientation
		this.rearrangeElements(orientation);
		
		//Setup the blocks (which will position them as well)
		this._field.SetupBlocks(this._config);
		
		//Setup the blocks in the block field accordingly, then add the resulting blocks to the level as children
		for(CCSprite spr : this._field.getBlockSprites()) this.addChild(spr);
		//for(CCSprite b : this._displ.getButtonBox().getButtonSprites()) this.addChild(b);
		this.addChild(this._displ.getButtonBox().getButtonContainer());
		this.addChild(this._displ.getLabelBox());
		
		//Set the orientation of the block field to that of the game
		this._field.changeFieldOrientation(orientation);
	}
	
	@Override
	public void update(Observable observable, Object data) {
		//Turn the provided data object into an EventData object to obtain the type and data from
		EventData ed = (EventData) data;
		
		//Tell the level what to do based on the event type
		switch(ed.getType())
		{
			case DoneChecking:    
				//The level is done checking from the setup phase (the 'initial' check)
				if(this._state == LevelPhase.Setup) this.startLevel();
				else this.moveMade();
				
				//Change the screen orientation if it changed in the meantime according to the controls
				if(this._currOrientation != this._control.getLastChangedOrientation())
				{
					//Change the field's orientation, making blocks fall if needed
					this._state = LevelPhase.Checking;
					this._currOrientation = this._control.getLastChangedOrientation();
					this.rearrangeElements(this._currOrientation);
					this._field.changeFieldOrientation(this._currOrientation);
				}
				else this._state = LevelPhase.Playing;
				
				break;
			case OrientationChanged:
				//Change the screen orientation according to the event data, but only if the level is currently playing
				if(this._state == LevelPhase.Playing)
				{
					//Change the field's orientation, making blocks fall if needed
					this._state = LevelPhase.Checking;
					this._currOrientation = (Orientation)ed.getDataByKey("neworientation");
					this.rearrangeElements(this._currOrientation);
					this._field.changeFieldOrientation(this._currOrientation);
				}
				
				break;
			case TouchDown:
				if(this._state == LevelPhase.Playing)
				{
					//Use the point provided by the event to set a selected block in the block field
					CGPoint p = (CGPoint)ed.getDataByKey("touchedlocation");
					p.y = this._screenBounds.height-p.y;
					
					this._field.selectBlockAt(p);
					String clicked =  this._displ.getButtonBox().buttonClickedAt(p) != null ? this._displ.getButtonBox().buttonClickedAt(p).getIdentifier() : "";
					
					if(clicked == "reset")
						this.resetLevel();
					else if(clicked == "resign")
						this.endLevel();
					else
					{
						//Unknown identifier
					}
				}
				
				break;
			case TouchSwiped:
				if(this._state == LevelPhase.Playing)
				{
					//Use the swipe direction provided by the event to attempt a switch in the block field. If true, change the level state
					SwipeDirection dir = (SwipeDirection)ed.getDataByKey("swipedirection");
					if(this._field.AttemptSwitch(dir)) this._state = LevelPhase.Checking;
				}
				
				break;
			case TouchUp:
				//Attempt to clear any data that was used for the other touch events (or in general what happens if touch is released)
				this._field.clearSwitch();
				
				break;
			case ScoreUpdate:
				int score = (Integer)ed.getDataByKey("score");
				int combo = (Integer)ed.getDataByKey("combo");
				
				this.setScore(_score+score);
				this.setCombo(combo);
				
				break;
			default:
				//When the level receives an event that it has no handler for (but is somehow still subscribed)
				break;
		}
	}
	
	//Change the layout of the field based on the provided orientation
	public void rearrangeElements(Orientation orientation)
	{
		//Percentages that the elements should take up, assuming the longest side of the screen.
		//Keep in mind: Game is in portrait mode at all times; the orientations are merely based on sensor data.
		float fieldPaddingPerc = 0.05f;
		float fieldPerc = 0.7f;
		float displayPerc = 0.2f;
		
		//Padding between the elements
		float maxFieldPadding = this._screenBounds.height*fieldPaddingPerc;
		
		//Size of the block field
		float fieldSizeX = this._screenBounds.width-(maxFieldPadding*2);
		float fieldSizeY = this._screenBounds.height*fieldPerc;
		CGSize fieldSize = CGSize.make(fieldSizeX, fieldSizeY);
		
		//Size of the display field
		float displaySizeX = this._screenBounds.width;
		float displaySizeY = this._screenBounds.height*displayPerc;
		CGSize displaySize = CGSize.make(displaySizeX, displaySizeY);
		
		float backSizeX = this._screenBounds.width;
		float backSizeY = this._screenBounds.height-displaySizeY;
		CGSize backSize = CGSize.make(backSizeX, backSizeY);
		
		//Starting location of the block field
		float fieldStartX = this._screenBounds.width/2.0f; 
		float fieldStartY = 0;
		float displayStartX = this._screenBounds.width/2.0f;
		float displayStartY = 0;
		float displayRot = 180;
		float backStartX = this._screenBounds.width/2.0f;
		float backStartY = 0;
		float buttonsStartX = 0;
		float buttonsStartY = 0;
		float buttonsRot = 0;
		CGSize buttonSize = CGSize.make(displaySizeY, displaySizeY);
		
		//Change some specific positions and rotations according to the current screen orientation
		if(orientation == Orientation.Portrait || orientation == Orientation.ILandscape)
		{
			fieldStartY = fieldSizeY/2+maxFieldPadding;
			displayStartY = this._screenBounds.height-(displaySizeY/2);
			backStartY = backSizeY/2;
			
			if(orientation == Orientation.Portrait) buttonsRot = 0.0f;
			else buttonsRot = 270.0f;
		} 
		else
		{
			fieldStartY = this._screenBounds.height-(fieldSizeY/2+maxFieldPadding);
			displayStartY = displaySizeY/2;
			displayRot = 0;
			backStartY = this._screenBounds.height-(backSizeY/2);
			
			if(orientation == Orientation.IPortrait) buttonsRot = 180.0f;
			else buttonsRot = 90.0f;
		}
		
		//Set the size and location of the axes		
		this._field.scaleSizeToPixelsWithConstraint(fieldSize);
		this._displ.scaleSizeToPixels(displaySize);
		this._displ.setAngle(displayRot);
		this._back.scaleSizeToPixels(backSize);
		this._field.positionField(CGPoint.ccp(fieldStartX, fieldStartY));
		this._displ.getSprite().setPosition(CGPoint.ccp(displayStartX, displayStartY));
		this._back.getSprite().setPosition(CGPoint.ccp(backStartX, backStartY));
		this._displ.getButtonBox().positionScaleButtonSpritesPadding(buttonSize, buttonsRot);
		
		//Position elements after initial ones have scaled
		buttonsStartY = this._displ.getSprite().getPosition().y;
		this._displ.positionLabels(this._displ.getSprite().getPosition(), buttonsRot);
		
		switch(orientation)
		{
			case Portrait: case ILandscape:
				buttonsStartX = this._screenBounds.width-(this._displ.getButtonBox().getSizeWithLastPadding().width/2);
				break;
			case IPortrait: case Landscape:
				buttonsStartX = this._displ.getButtonBox().getSizeWithLastPadding().width/2;
				break;
			default:
				break;
		}
		
		this._displ.getButtonBox().getButtonContainer().setPosition(CGPoint.ccp(buttonsStartX, buttonsStartY));
	}
	
	//Indicates that the player can start playing the level
	public void startLevel()
	{
		//Subscribe to events on the control instance
		this._control.addEvent(EventType.TouchDown);
		this._control.addEvent(EventType.TouchSwiped);
		this._control.addEvent(EventType.TouchUp);
		this._control.addEvent(EventType.OrientationChanged);
		
		this.schedule(LEVEL_TIME_SELECTOR, LEVEL_TIME_INTERVAL);
	}
	
	public void endLevel()
	{
		
	}
	
	//What to do with the level if the game is paused/stopped
	public void pause()
	{
		this._control.deactivateSensors();
		this._lastState = this._state;
		this._state = LevelPhase.Paused;
		
		this.unschedule(LEVEL_TIME_SELECTOR);
	}
	
	//What to do when the level is resumed
	public void resume()
	{
		if(this._lastState == LevelPhase.Playing)
		{
			this._control.activateSensors();
			this.schedule(LEVEL_TIME_SELECTOR, LEVEL_TIME_INTERVAL);
		}
		
		this._state = this._lastState;
	}
	
	//Reset the level's variables
	public void resetLevel()
	{
		//Empty level content and reset the timer
		this._control.removeAllEvents();
		this._control.deleteObservers();
		this._field.deleteObservers();
		this._displ.cleanSprite();
		this._field.cleanSprite();
		this._back.cleanSprite();
		this._score = 0;
		this._moves = 0;
		this._combo = 0;
		
		this.unschedule(LEVEL_TIME_SELECTOR);
		this._currTime = LEVEL_TIME;
		
		this.delayedSetup();
	}
	
	//Scheduled timer callback function
	public void secondPassed(float dt)
	{
		this._currTime--;
		this._displ.modifyLabelsTextByType(DisplayLabel.Time, ""+this._currTime);
	}
	
	//Game loop
	public void GameLoop(float dt)
	{
		//Game loop here...
	}
}
