package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity {
	//Constants
	private static final int DEF_LTIME = 60;
	
	//Fields
	private CCGLSurfaceView _glSurfaceView;
	private ArrayList<Level> _levels;
	private int _currLevel = 0;
	
	//Constructor
	public Game()
	{
		this._levels = new ArrayList<Level>();
	}
	
	//Default methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Configure the window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        //Create a new CCGLSurfaceView instance and make it the content window for the game
        _glSurfaceView = new CCGLSurfaceView(this);
        setContentView(_glSurfaceView);
        
        Log.d("Dave", "HASHSHASKD");
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        //Make sure the Cocos2D framework renders into the same Surface View that has been set as the Activity's content view.
        CCDirector.sharedDirector().attachInView(_glSurfaceView);
        
        //Display FPS and set the animation interval for the game
        CCDirector.sharedDirector().setDisplayFPS(true);
        CCDirector.sharedDirector().setAnimationInterval(1.0f/60.0f);
        
        //Factories used to create the blocks
        AlienBlockFactory afac = new AlienBlockFactory();
        
        //Blocks list
        ArrayList<BlockConfig> blocksL1 = new ArrayList<BlockConfig>();
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 0), "Crazy", afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 1), "Crazy", afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 2), "Nerdy", afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 3), "Nerdy", afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(1, 4), "Nerdy", afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 5), "Nerdy", afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 6), "Nerdy", afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 7), "Nerdy", afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 8), "Nerdy", afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 9), "Nerdy", afac));
        
        //Display elements
        ArrayList<PGButton> normalButtons = new ArrayList<PGButton>();
        normalButtons.add(new PGButton());
        
        PGDisplay normalDisplay = new PGDisplay(normalButtons);
        Background normalBack = new Background();
        BlockField normalField = new BlockField(CGPoint.ccp(6, 10));
        
        //Create the levels
        Level newLevel1 = new Level(normalBack, normalField, normalDisplay, this);
        newLevel1.addConfig(blocksL1);
        
        //Add the levels
        this.AddLevel(newLevel1);
        this.StartCurrentLevel();
    }

    @Override
    public void onPause() {
        super.onPause();
        
        CCDirector.sharedDirector().pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        
        CCDirector.sharedDirector().resume();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	
    	CCDirector.sharedDirector().end();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    //Add a level
    public void AddLevel(Level l)
    {
    	this._levels.add(l);
    }
    
    //Clear all levels
    public void ClearLevels()
    {
    	this._levels.clear();
    }
    
    //Start the current level
    public void StartCurrentLevel()
    {
    	//Abort the function if there are no levels
    	if(this._levels.size() == 0) return;
    	
    	//Make sure a level is targetted
    	Level currLevel = this._levels.get(this._currLevel);
    	
    	if(currLevel != null)
    	{
    		CCScene levelScene = CCScene.node();
    		levelScene.addChild(currLevel);
    		
    		currLevel.setup(Orientation.IPortrait);
    		CCDirector.sharedDirector().runWithScene(levelScene);
    	}
    }
    
    //Go to the next level
    public void NextLevel()
    {
    	//
    }
}
