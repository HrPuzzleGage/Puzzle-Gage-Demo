package hr.gdd.puzzle.gage.demo;


import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;


public class GameControls implements OnKeyListener, OnGestureListener, OnDoubleTapListener 
{   
    /**
     * The usable screen width
     */
    private int width;
    private int moveWidth;
    private int height;
    private int moveHeight;
    
    
	public GameControls(int width, int height)
	{
		
	}
	
	@Override
    public boolean onDown(MotionEvent e) 
	{
        return false;
    }
	
	@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
	{
		return false;
    }
	
	@Override
    public void onLongPress(MotionEvent e) 
	{
		//Event
    }
	
	@Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
	{
		
        return false;
    }
	
	@Override
	public void onShowPress(MotionEvent e)
	{
		//Event
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		return false;
	}
	
}
