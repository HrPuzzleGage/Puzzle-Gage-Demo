package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity
{
	//Constants
	private static final int DEF_LTIME = 60;
	
	//Fields
	private CCGLSurfaceView _glSurfaceView;
	private ArrayList<Level> _levels;
	private int _currLevel = 0;
	
	//Constructor
	public Game()
	{
		//Initialize the level list
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
        
        //Block configurators list
        ArrayList<BlockConfig> blocksL1 = new ArrayList<BlockConfig>();
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 0), AlienType.Crazy, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 9), AlienType.Crazy, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(5, 0), AlienType.Daisy, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(5, 9), AlienType.Daisy, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(1, 1), AlienType.Alex, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 2), AlienType.Alex, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(3, 2), AlienType.Alex, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(1, 3), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 4), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 6), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 7), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 8), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(0, 9), AlienType.Rocky, afac));
        /*blocksL1.add(new BlockConfig(CGPoint.ccp(2, 0), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 1), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 2), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 3), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 4), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 5), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 6), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 7), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 8), AlienType.Brain, afac));
        blocksL1.add(new BlockConfig(CGPoint.ccp(2, 9), AlienType.Brain, afac));*/
        
        //Create the display elements to be used in the level
        ArrayList<PGButton> normalButtons = new ArrayList<PGButton>();
        normalButtons.add(new PGButton());
        
        PGDisplay normalDisplay = new PGDisplay(normalButtons);
        Background normalBack = new Background();
        BlockField normalField = new BlockField(CGPoint.ccp(6, 10));
        
        //Create the levels
        Level newLevel1 = new Level(normalBack, normalField, normalDisplay, this._glSurfaceView);
        
        //Add the configurators to the level
        newLevel1.addConfig(blocksL1);
        
        //Add the levels to the game
        this.AddLevel(newLevel1);
        
        //Start the level that is currently chosen for the game
        this.StartCurrentLevel();
    }

    @Override
    public void onPause() {
        super.onPause();
        
        //Pause the Cocos2D director when the activity pauses
        CCDirector.sharedDirector().pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        
        //Resume the Cocos2D director when the activity resumes
        CCDirector.sharedDirector().resume();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	
    	//Stop the Cocos2D director when the activity stops
    	CCDirector.sharedDirector().end();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    //Add a level to the game's list of levels
    public void AddLevel(Level l)
    {
    	this._levels.add(l);
    }
    
    //Start the currently selected level of the game
    public void StartCurrentLevel()
    {
    	//Abort the method if the current level index exceeds the number of levels
    	if(this._currLevel+1 > this._levels.size()) return;
    	
    	//Obtain the currently selected level
    	Level currLevel = this._levels.get(this._currLevel);
    	
    	//Create a new scene and put the level in it
		CCScene levelScene = CCScene.node();
		levelScene.addChild(currLevel);
		
		//Put the newly added level in its setup phase, passing along a screen orientation
		//currLevel.setup(Orientation.IPortrait);
		currLevel.delayedSetup();
		
		//Run the scene containing the newly added level
		CCDirector.sharedDirector().runWithScene(levelScene);
    }
}
