package hr.gdd.puzzle.gage.demo;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity {
	protected CCGLSurfaceView _glSurfaceView;
	
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
}
