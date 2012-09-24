package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;

import org.cocos2d.actions.CCTimer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;

public class Level extends CCLayer 
{
	//Enumeration for level phases
	private enum LevelPhase 
	{
		Setup, 
		Playing, 
		Checking, 
		Paused
	}
	
	//Fields
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
	
	//Controller fields
	private Orientation _currOrientation;
	private CCTimer _orientationTimer;
	
	//Static scene returner
	public static CCScene scene(Background bg, BlockField bf, PGDisplay d, Context c)
	{
		CCScene scene = CCScene.node();
		Level newLevel = new Level(bg, bf, d, c);
		
		scene.addChild(newLevel);
		return scene;
	}
	
	//Level constructor
	public Level(Background bg, BlockField bf, PGDisplay d, Context c)
	{
		this._back = bg;
		this._field = bf;
		this._displ = d;
		this._context = c;
		this._config = new ArrayList<BlockConfig>();
		
		this._screenBounds = CCDirector.sharedDirector().displaySize();
		this.setIsTouchEnabled(true);
	}
	
	//Getter methods
	public PGDisplay getDisplay()
	{
		return this._displ;
	}
	
	public Background getBack()
	{
		return this._back;
	}
	
	public BlockField getField()
	{
		return this._field;
	}
	
	public ArrayList<BlockConfig> getConfig()
	{
		return this._config;
	}
	
	public int getScore()
	{
		return this._score;
	}
	
	public int getCombo()
	{
		return this._combo;
	}

	public CCTimer getTimer() 
	{
		return this._timer;
	}
	
	//Setter methods
	public void setDisplay(PGDisplay display, ArrayList<PGButton> buttons)
	{
		this._displ = display;
	}
	
	public void setBackground(Background back)
	{
		this._back = back;
	}
	
	public void setField(BlockField field)
	{
		this._field = field;
	}
	
	public void setScore(int score)
	{
		this._score = score;
	}
	
	public void setCombo(int combo)
	{
		this._combo = combo;
	}
	
	public void setTimer(CCTimer timer) 
	{
		this._timer = timer;
	}
	
	//Controller methods
	public boolean ccIsTouchedStart(MotionEvent event)
	{
		return true;
	}
	
	public boolean ccIsTouchedMove(MotionEvent event)
	{
		return true;
	}
	
	public boolean ccIsTouchedEnd(MotionEvent event)
	{
		return true;
	}
	
	public String getHorSwipe(MotionEvent event)
	{
		return "Nothing";
	}
	
	public String getScreenOrientation()
	{
		return "Portrait";
	}
	
	public boolean orientationChanged()
	{
		return false;
	}
	
	public void setOrientationTimer(CCTimer t)
	{
		this._orientationTimer = t;
	}

	//Methods to add to list
	public void addConfig(BlockConfig c)
	{
		this._config.add(c);
	}
	
	public void addConfig(ArrayList<BlockConfig> bc)
	{
		this._config.addAll(bc);
	}
	
	//Setup the level and all of its associated objects prior to playing it
	public void setup(Orientation orientation)
	{
		this._state = LevelPhase.Setup;
		
		//Add the display objects
		this.addChild(this._back.getSprite());
		this.addChild(this._field.getSprite());
		this.addChild(this._displ.getSprite());
		
		//Transform the grid
		this._field.TransformGrid(orientation);
		
		//Setup the blocks
		for(CCSprite spr : this._field.SetupBlocks(this._config))
		{
			this.addChild(spr);
		}
		
		//Rearrange the elements
		rearrangeElements(orientation);
	}
	
	public void rearrangeElements(Orientation orientation)
	{
		//Percentages
		float fieldPaddingPerc = 0.05f;
		float fieldPerc = 0.65f;
		float displayPerc = 0.25f;
		
		if(orientation == Orientation.Portrait || orientation == Orientation.IPortrait)
		{
			//Calculate some values based off of the screen size
			float maxFieldPadding = this._screenBounds.height*fieldPaddingPerc;
			float fieldStartX = this._screenBounds.width/2.0f;
			float fieldStartY = maxFieldPadding;
			float fieldSpace = this._screenBounds.height*fieldPerc;
			float displaySpace = this._screenBounds.height*displayPerc;
			
			//Resize the elements
			this._field.scaleField(fieldSpace, maxFieldPadding, "portrait");
			this._displ.resizeDisplay(CGSize.make(this._screenBounds.width, displaySpace));
			this._back.resizeBack(CGSize.make(this._screenBounds.width, this._screenBounds.height));
			
			//Position the elements
			this._field.getSprite().setPosition(CGPoint.ccp(fieldStartX-(this._field.getAbsoluteSize().x/2.0f), 
					fieldStartY+(this._field.getAbsoluteSize().y)));
			this._displ.getSprite().setPosition(CGPoint.ccp(0.0f, this._screenBounds.height));
			this._back.getSprite().setPosition(CGPoint.ccp(0.0f, this._screenBounds.height));
		} 
		else if(orientation == Orientation.Landscape || orientation == Orientation.ILandscape)
		{
			float maxFieldPadding = this._screenBounds.width*fieldPaddingPerc;
			float fieldStartX = maxFieldPadding;
			float fieldStartY = this._screenBounds.height/2.0f;
			float fieldSpace = this._screenBounds.width*fieldPerc;
			float displaySpace = this._screenBounds.width*displayPerc;
			
			//Resize the elements
			this._back.resizeBack(CGSize.make(this._screenBounds.height, this._screenBounds.width));
			this._displ.resizeDisplay(CGSize.make(this._screenBounds.height, displaySpace));
			this._field.scaleField(fieldSpace, maxFieldPadding, "landscape");
			
			//Position the elements
			this._field.getSprite().setPosition(CGPoint.ccp(fieldStartX, fieldStartY+(this._field.getAbsoluteSize().y/2.0f)));
			this._back.getSprite().setPosition(CGPoint.ccp(this._screenBounds.width, this._screenBounds.height));
			this._displ.getSprite().setPosition(CGPoint.ccp(this._screenBounds.width, this._screenBounds.height));
			
			//Rotate the elements
			this._back.getSprite().setRotation(90.0f);
			this._displ.getSprite().setRotation(90.0f);
		}
	}
	
	//Indicates that the player can start playing the level
	public void startLevel()
	{
		this._state = LevelPhase.Playing;
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
