package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.cocos2d.actions.CCTimer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.content.Context;
import android.view.View;

public class Level extends CCLayer implements Observer
{
	//-----------------Fields
	private LevelPhase _state; 
	private LevelPhase _lastState;
	private int _score;
	private int _combo;
	private Background _back;
	private PGDisplay _displ;
	private BlockField _field;
	private ArrayList<BlockConfig> _config;
	private CGSize _screenBounds;
	private Orientation _currOrientation;
	private Control _control;
	
	//-----------------Level constructor
	public Level(Background bg, BlockField bf, PGDisplay d, View v)
	{
		this._back = bg;
		this._field = bf;
		this._displ = d;
		
		this._config = new ArrayList<BlockConfig>();
		this._screenBounds = CCDirector.sharedDirector().displaySize();
		
		//Create the control instance (needs to be activated before sensors can be used)
		this._control = new Control(v);
		this._control.addObserver(this);
		this._control.activateSensors();
		this._control.toggleEvents(true);
		
		//Add the level as an observer to the block field
		this._field.addObserver(this);
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
	//Overload for setting the display interface only
	public void setDisplay(PGDisplay display)
	{
		this.setDisplay(display, null);
	}
	
	//Set the display interface along with all of its buttons
	public void setDisplay(PGDisplay display, ArrayList<PGButton> buttons)
	{
		this._displ = display;
		if(buttons != null) this._displ.AddButton(buttons);
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
	}
	
	//Set the level's current combo
	public void setCombo(int combo)
	{
		this._combo = combo;
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
		this.schedule("timercall", 1.0f);
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
		//Set the level's phase to its setup phase
		this._state = LevelPhase.Setup;
		
		//Add background, blockfield and display interface
		//this.addChild(this._back.getSprite());
		this.addChild(this._field.getSprite());
		//this.addChild(this._displ.getSprite());
		
		//Rearrange the location of the elements according to the screen's orientation
		rearrangeElements(orientation);
		
		//Set the orientation of the block field to that of the game
		this._field.setOrientation(orientation);
		
		//Setup the blocks
		this._field.SetupBlocks(this._config);
		
		//Setup the blocks in the block field accordingly, then add the resulting blocks to the level as children
		for(CCSprite spr : this._field.getBlockSprites())
		{
			this.addChild(spr);
		}
		
		//Make the blocks fall; this will trigger an event when finished
		this._field.blocksToFall();
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
				
				//Change the screen orientation if it changed in the meantime according to the controls
				if(this._currOrientation != this._control.getLastChangedOrientation())
				{
					//Change the field's orientation, making blocks fall if needed
					this._state = LevelPhase.Checking;
					this._currOrientation = this._control.getLastChangedOrientation();
					
					this._field.setOrientation(this._currOrientation);
					this._field.blocksToFall();
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
					
					this._field.setOrientation(this._currOrientation);
					this._field.blocksToFall();
				}
				
				break;
			case TouchDown:
				if(this._state == LevelPhase.Playing)
				{
					//Use the point provided by the event to set a selected block in the block field
					CGPoint p = (CGPoint)ed.getDataByKey("touchedlocation");
					this._field.selectBlockAt(p);
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
		float fieldPerc = 0.65f;
		float displayPerc = 0.25f;
		
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
		
		//Starting location of the block field
		float fieldStartX = this._screenBounds.width/2.0f;
		float fieldStartY = this._screenBounds.height/2.0f;
		
		//Set the size and location of the axes
		this._field.getSprite().setPosition(fieldStartX, fieldStartY);
		this._field.resizeIndividualAxes(fieldSize, this._field.getSprite().getContentSize(), this._field.getDimensions());
	}
	
	//Indicates that the player can start playing the level
	public void startLevel()
	{
		//Subscribe to events on the control instance
		this._control.addEvent(EventType.TouchDown);
		this._control.addEvent(EventType.TouchSwiped);
		this._control.addEvent(EventType.TouchUp);
		this._control.addEvent(EventType.OrientationChanged);
	}
	
	//What to do with the level if the game is paused/stopped
	public void pause()
	{
		this._control.deactivateSensors();
		this._lastState = this._state;
		this._state = LevelPhase.Paused;
	}
	
	//What to do when the level is resumed
	public void resume()
	{
		if(this._lastState == LevelPhase.Playing) this._control.activateSensors();
		
		this._state = this._lastState;
	}
	
	//Reset the level's variables
	public void resetLevel()
	{
		//Empty level content and reset the timer
	}
	
	//Game loop
	public void GameLoop(float dt)
	{
		//Game loop here...
	}
}
