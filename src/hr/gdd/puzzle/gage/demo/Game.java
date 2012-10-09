package hr.gdd.puzzle.gage.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CGPoint;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity
{
	//Fields
	private CCGLSurfaceView _glSurfaceView;
	private ArrayList<Level> _levels;
	private int _currLevel = 0;
	private int _currWorld = 0;
	
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
        
        //Passed from level select 
        this._currLevel = this.getIntent().getIntExtra("levelToPlay", 0);
        this._currWorld = this.getIntent().getIntExtra("worldToPlay", 0);
        
        //Create a new CCGLSurfaceView instance and make it the content window for the game
        _glSurfaceView = new CCGLSurfaceView(this);
        
        setContentView(_glSurfaceView);
    }
    
    @Override
    public void onStart() 
    {
        super.onStart();
        
        //Make sure the Cocos2D framework renders into the same Surface View that has been set as the Activity's content view.
        CCDirector.sharedDirector().attachInView(_glSurfaceView);
        
        //Display FPS and set the animation interval for the game
        CCDirector.sharedDirector().setDisplayFPS(true);
        CCDirector.sharedDirector().setAnimationInterval(1.0f/60.0f);
        
        //Create the display elements to be used in the level
        ArrayList<PGButton> normalButtons = new ArrayList<PGButton>();
        LinkedHashMap<CCLabel, DisplayLabel> defLabels = new LinkedHashMap<CCLabel, DisplayLabel>();
        normalButtons.add(new PGButton("Item_Reset.png", "reset"));
        normalButtons.add(new PGButton("Item_Exit.png", "resign"));
        defLabels.put(CCLabel.makeLabel("0", "DroidSans", 16), DisplayLabel.Score);
        defLabels.put(CCLabel.makeLabel("0", "DroidSans", 16), DisplayLabel.Moves);
        defLabels.put(CCLabel.makeLabel("0", "DroidSans", 16), DisplayLabel.Combo);
        defLabels.put(CCLabel.makeLabel("0", "DroidSans", 16), DisplayLabel.Time);
        
        PGDisplay normalDisplay = new PGDisplay(normalButtons, defLabels);
        Background normalBack = new Background();
        BlockField normalField = new BlockField(CGPoint.ccp(6, 10));

		Control control = new Control(this._glSurfaceView);
        control.activateSensors();
		control.toggleEvents(true);
        
        //Create the levels
        Level newLevel = new Level(normalBack, normalField, normalDisplay, control);
        
        //XmlDataReader instance = XmlDataReader.getInstance();
        ArrayList<BlockConfig> blocks = new ArrayList<BlockConfig>();
        
        try 
        {
			XmlParser i = XmlParser.instance();
			i.setDocumentFromAssets(this, "practiceXML");
			
			blocks = i.getLevelBlockDataAt(this._currWorld, this._currLevel);
			
			newLevel.setAllowedMoves(i.getLastLevelPar());
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		} 
        catch (ParserConfigurationException e) 
        {
			e.printStackTrace();
		} 
        catch (SAXException e) 
        {
			e.printStackTrace();
		}
        
        //Add the configurators to the level
        newLevel.addConfig(blocks);
        
        //Add the levels to the game
        this.AddLevel(newLevel);
        
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
    	//if(this._currLevel+1 > this._levels.size()) return;
    	if(this._levels.size() == 0) return;
    	
    	//Obtain the currently selected level
    	Level currLevel = this._levels.get(/*this._currLevel*/0);
    	
    	//Create a new scene and put the level in it
		CCScene levelScene = CCScene.node();
		levelScene.addChild(currLevel);
		
		//Put the newly added level in its setup phase, passing along a screen orientation
		currLevel.delayedSetup();
		
		//Run the scene containing the newly added level
		CCDirector.sharedDirector().runWithScene(levelScene);
    }
}
