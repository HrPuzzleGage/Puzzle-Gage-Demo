package hr.gdd.puzzle.gage.demo;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public abstract class Block extends SpriteWrapper
{
	public static float fallTime = 0.4f;
	public static float switchTime = 0.3f;
	
	//Fields
	protected CGPoint _gridCoord;
	
	//Get and set the block's grid coordinations
	public void setGridCoord(CGPoint point)
	{
		this._gridCoord = point;
	}
	
	public CGPoint getGridCoord()
	{
		return this._gridCoord;
	}
	
	//Check, based off of the supplied control, whether the block is touched
	//Select the block
	public void select()
	{
		this._sprite.setOpacity(65);
	}
	
	//Deselect the block
	public void unselect()
	{
		this._sprite.setOpacity(255);
	}
	
	//Abstract methods to be implemented by subclasses
	public abstract BlockType getType();
}
