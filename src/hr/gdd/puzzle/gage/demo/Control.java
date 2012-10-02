package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;
import java.util.Observable;
import org.cocos2d.types.CGPoint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Control extends Observable implements SensorEventListener, OnTouchListener
{
	private static final int swipeMargin = 1;
	
	private SensorManager _sm;
	private ArrayList<Sensor> _sensors;
	private ArrayList<EventType> _subscribedTypes;
	private Orientation _lastOrientation = Orientation.Portrait;
	private CGPoint _prevTouch = null;
	private boolean _eventsActive = true;
	
	//-----------------Level constructor
	public Control(View v)
	{
		//Initialize the lists
		this._sensors = new ArrayList<Sensor>();
		this._subscribedTypes = new ArrayList<EventType>();
		
		//Add a touch listener to the provided view
		Context c = v.getContext();  
		v.setOnTouchListener(this);
		
		//Obtain the Sensor Manager through the provided view's Context
		this._sm = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
		this.addSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
	//-----------------Other methods
	//Add an event type to the subscription list
	public void addEvent(EventType evType)
	{
		this._subscribedTypes.add(evType);
	}
	
	//Remove an event type from the subscription list
	public void removeEvent(EventType evType)
	{
		this._subscribedTypes.remove(evType);
	}
	
	//Add a Sensor to the instance's internal list of sensors by giving a sensor type
	public void addSensor(int type)
	{
		this._sensors.add(this._sm.getDefaultSensor(type));
	}
	
	//Add a Sensor to the instance's internal list of sensors
	public void addSensor(Sensor sensor)
	{
		this._sensors.add(sensor);
	}
	
	//Remove every sensor in the instance's internal list of sensors of the given type
	public void removeSensor(int type)
	{
		ArrayList<Sensor> sensorsToRemove = new ArrayList<Sensor>();
		
		for(Sensor s : this._sensors)
		{
			if(s.getType() == type) sensorsToRemove.add(s);
		}
		
		this._sensors.removeAll(sensorsToRemove);
	}
	
	//Remove a sensor instance from the Control instance's internal list of sensors
	public void removeSensor(Sensor sensor)
	{
		this._sensors.remove(sensor);
	}
	
	//Activate all sensors associated with the instance, registering them for events
	public void activateSensors()
	{
		for(Sensor s : this._sensors)
		{
			_sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	//Unregister all sensors associated with the instance
	public void deactivateSensors()
	{
		_sm.unregisterListener(this);
	}
	
	//Toggle on or off whether event updates should be received from this instance
	public void toggleEvents(boolean e)
	{
		this._eventsActive = e;
	}
	
	//Obtain the last changed orientation
	public Orientation getLastChangedOrientation()
	{
		return this._lastOrientation;
	}
	
    //Implemented onAccuracyChanged method for sensor manager
	public void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
		//In case there is a chance of one of the sensors changing in accuracy; change this
	}
	
	//Implemented onSensorChanged method for sensor manager.
	public void onSensorChanged(SensorEvent event) 
	{
		if(!this._eventsActive) return;
		
		//Accelerometer
		if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
		{
			//Obtain the value for the X- and Y-axis of the accelerometer
			float accX = event.values[0];
			float accY = event.values[1];
			
			Orientation or = Orientation.Portrait;
			boolean isDifferent = true;
			
			//Based on the sensor values, decide the orientation the device is held in
			if(accX > 5.0f && accY > -5.0f && accY < 5.0f)
				or = Orientation.Landscape;
			else if(accX < -5.0f && accY > -5.0f && accY < 5.0f)
				or = Orientation.ILandscape;
			else if(accY > 5.0f && accX > -5.0f && accX < 5.0f)
				or = Orientation.Portrait;
			else if(accY < -5.0f && accX > -5.0f && accX < 5.0f)
				or = Orientation.IPortrait;
			else isDifferent = false;
			
			//Check if the current orientation is different from the last measured orientation
			if(this._lastOrientation == or) isDifferent = false;
			else if(isDifferent) this._lastOrientation = or;
			
			//Notify the observers depending on what events should be received by this instance
			this.setChanged();
			if(isDifferent && this._subscribedTypes.contains(EventType.OrientationChanged)) 
				this.notifyObservers(new EventData(EventType.OrientationChanged, "neworientation", this._lastOrientation));
			if(this._subscribedTypes.contains(EventType.OrientationFound))
				this.notifyObservers(new EventData(EventType.OrientationFound, "orientation", or));
			this.clearChanged();
        }
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
		if(!this._eventsActive) return false;
		
		//Check for the touch event's type and decide from there
		this.setChanged();
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if(this._subscribedTypes.contains(EventType.TouchDown))
				{
					//If the screen is touched, return the point of the screen that was touched
					CGPoint touchPos = CGPoint.make(event.getX(), event.getY()); 
					this.notifyObservers(new EventData(EventType.TouchDown, "touchedlocation", touchPos));
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if(this._subscribedTypes.contains(EventType.TouchSwiped))
				{
					//By default, start the swipe direction as no direction
					SwipeDirection dir = SwipeDirection.None;
					
					//Only proceed with this block (direction calculation) if there is a second point to refer to
					if(this._prevTouch != null)
					{
						//Calculate the differences between the current and last position
						float swipeDiffX = event.getX()-this._prevTouch.x;
						float swipeDiffY = event.getY()-this._prevTouch.y;
						float swipeDist = FloatMath.sqrt((swipeDiffX*swipeDiffX)*(swipeDiffY*swipeDiffY));
						
						//If the swiped distance from the last position is long enough, proceed
						if(swipeDist > swipeMargin)
						{
							//Calculate the direction using the tangent of X and Y
							double swipeAngle = Math.atan2(-swipeDiffY, swipeDiffX)*(180/Math.PI);
							if(swipeAngle < 0) swipeAngle = 360+swipeAngle;
							
							//Convert the direction based on the current screen orientation
							if(this._lastOrientation == Orientation.IPortrait) swipeAngle += 180;
							if(this._lastOrientation == Orientation.Landscape) swipeAngle += 90;
							if(this._lastOrientation == Orientation.ILandscape) swipeAngle += 270;
							if(swipeAngle > 360) swipeAngle -= 360;
							
							//Based on the final angle, decide what direction the user has swiped in
							if(swipeAngle > 270 || swipeAngle < 90)
								dir = SwipeDirection.Right;
							else dir = SwipeDirection.Left;
							
						} else this._prevTouch = null;
					}  else this._prevTouch = CGPoint.ccp(event.getX(), event.getY());
					
					//Notify all observers of the new direction
					this.notifyObservers(new EventData(EventType.TouchSwiped, "swipedirection", dir));
				}
				break;
			case MotionEvent.ACTION_UP:
				//Erase any values involved in the touch move and notify any observers that the user no longer touches the screen
				this._prevTouch = null;
				this.notifyObservers(new EventData(EventType.TouchUp));
				
				break;
			default:
				break;
		}
		this.clearChanged();
		
		return true;
	}
}
