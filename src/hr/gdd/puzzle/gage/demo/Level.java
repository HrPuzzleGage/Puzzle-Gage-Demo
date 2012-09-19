package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;

import org.cocos2d.actions.CCTimer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;

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
	private ArrayList<Block> _blocks;
	
	//Controller fields
	private String _currOrientation;
	private CCTimer _orientationTimer;
	
	//Obtain a new level instance
	public static CCScene scene(Background bg, BlockField bf, PGDisplay d, ArrayList<PGButton> buttons)
	{
		CCScene scene = CCScene.node();
		CCLayer layer = new Level(bg, bf, d, buttons);
		
		scene.addChild(layer);
		return scene;
	}
	
	//Level constructor
	public Level(Background bg, BlockField bf, PGDisplay d, ArrayList<PGButton> buttons)
	{
		this._back = bg;
		this._field = bf;
		this._displ = d;
		
		this.setIsTouchEnabled(true);
		
		//Add buttons to display
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
	
	//Setup the level and all of its associated objects prior to playing it
	public void setup(String orientation)
	{
		this._state = LevelPhase.Setup;
	}
	
	//Load the config data and turn those into block instances
	public void configToBlocks()
	{
		//Iterate over configuration and create new block instances
	}
	
	//Indicates that the player can start playing the level
	public void startLevel()
	{
		this._state = LevelPhase.Playing;
		//is the value 'canplayerinteract' needed if you have this phase?
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
	public void GameLoop(float dt){
		//Game loop here...
	}
}
