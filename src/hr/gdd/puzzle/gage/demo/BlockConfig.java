package hr.gdd.puzzle.gage.demo;

import org.cocos2d.types.CGPoint;

import android.view.View;

public class BlockConfig
{
	//Fields
	private CGPoint _position;
	private AlienType _type;
	private IBlockFactory _factoryToUse;
	
	//Constructor
	public BlockConfig(CGPoint pos, AlienType type, IBlockFactory factory)
	{
		this._position = pos;
		this._type = type;
		this._factoryToUse = factory;
	}
	
	//Getter methods
	public CGPoint getPosition() 
	{
		return _position;
	}
	
	public AlienType getType() 
	{
		return _type;
	}
	
	public IBlockFactory getFactory() 
	{
		return _factoryToUse;
	}
	
	//Setter methods
	public void setPosition(CGPoint position) 
	{
		this._position = position;
	}

	public void setType(AlienType type) 
	{
		this._type = type;
	}

	public void setFactory(IBlockFactory factory) 
	{
		this._factoryToUse = factory;
	}
	
	//Create a new block using the supplied factory!
	public Block obtainBlock()
	{
		return this._factoryToUse.createBlock(this);
	}
}
