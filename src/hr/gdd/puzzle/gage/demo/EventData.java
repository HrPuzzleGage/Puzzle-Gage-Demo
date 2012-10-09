package hr.gdd.puzzle.gage.demo;

import java.util.HashMap;

public class EventData 
{
	private EventType _evType;
	private HashMap<String, Object> _additionalData;
	
	public EventData(EventType type)
	{
		this._evType = type;
		this._additionalData = new HashMap<String, Object>();
	}
	
	public EventData(EventType type, String defDataKey, Object defData)
	{
		this._evType = type;
		
		this._additionalData = new HashMap<String, Object>();
		if(defData != null) this._additionalData.put(defDataKey, defData);
	}
	
	public EventData(EventType type, HashMap<String, Object> defData)
	{
		this._evType = type;
		this._additionalData = defData;
	}
	
	public boolean addData(String key, Object data)
	{
		if(this._additionalData.containsKey(key)) return false;
		
		this._additionalData.put(key, data);
		return true;
	}
	
	public HashMap<String, Object> getAllData()
	{
		return this._additionalData;
	}
	
	public Object getDataByKey(String key)
	{
		if(!this._additionalData.containsKey(key)) return null;
		
		return this._additionalData.get(key);
	}
	
	public EventType getType()
	{
		return this._evType;
	}
}
