package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.cocos2d.actions.CCTimer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGSize;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

public class Level extends CCLayer implements Observer
{
	//-----------------Fields
	private int _startTime;
	private int _currTime;
	private CCTimer _timer;
	private LevelPhase _state; 
	private LevelPhase _lastState;
	private int _score;
	private int _combo;
	private Background _back;
	private PGDisplay _displ;
	private BlockField _field;
	private ArrayList<BlockConfig> _config;
	private CGSize _screenBounds;
	private Context _context;
	private Orientation _currOrientation;
	private CCTimer _orientationTimer;
	
	//-----------------Level constructor
	public Level(Background bg, BlockField bf, PGDisplay d, Context c)
	{
		this._back = bg;
		this._field = bf;
		this._displ = d;
		this._context = c;
		
		this._config = new ArrayList<BlockConfig>();
		this._screenBounds = CCDirector.sharedDirector().displaySize();
		this.setIsTouchEnabled(true);
		
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

	//Obtain the timer used for the level
	public CCTimer getTimer() 
	{
		return this._timer;
	}
	
	//Obtain the current application orientation
	public Orientation getScreenOrientation()
	{
		return Orientation.Portrait;
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
	
	//Set the level's current timer to be used
	public void setTimer(CCTimer timer) 
	{
		this._timer = timer;
		//Reset the timer
	}
	
	//-----------------Other methods
	//Check if a touch start event has been invoked on the level layer
	public boolean ccIsTouchedStart(MotionEvent event)
	{
		return true;
	}
	
	//Check if a touch move event has been invoked on the level layer
	public boolean ccIsTouchedMove(MotionEvent event)
	{
		return true;
	}
	
	//Check if a touch end event has been invoked on the level layer
	public boolean ccIsTouchedEnd(MotionEvent event)
	{
		return true;
	}
	
	//Check the general direction of a swipe and return one of the possible swipe orientations
	public SwipeDirection getHorSwipe(MotionEvent event)
	{
		return SwipeDirection.Left;
	}
	
	//Detect whether the current game orientation matches the level orientation 
	public boolean orientationChanged()
	{
		return false;
	}
	
	//Set the level orientation timer in order to change the level orientation if it is still different at the end of the timer
	public void setOrientationTimer(CCTimer t)
	{
		this._orientationTimer = t;
	}

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
		String message = data.toString();
		
		if(message == "donechecking") 
		{
			if(this._state == LevelPhase.Setup) 
				this.startLevel();
			else 
				this._state = LevelPhase.Playing;
		}
	}
	
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
		this._state = LevelPhase.Playing;
		
		//Test with new orientation
		this._field.setOrientation(Orientation.Landscape);
		this._field.blocksToFall();
	}
	
	//What to do with the level if the game is paused/stopped
	public void pause()
	{
		this._lastState = this._state;
		this._state = LevelPhase.Paused;
	}
	
	//What to do when the level is resumed
	public void resume()
	{
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
