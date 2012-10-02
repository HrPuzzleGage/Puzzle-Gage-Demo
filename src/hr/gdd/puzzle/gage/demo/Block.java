package hr.gdd.puzzle.gage.demo;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public abstract class Block 
{
	//Fields
	protected CCSprite _sprite;
	protected CGPoint _storedCoord;
	
	//Move the block using its falling speed
	public void setStoredCoord(CGPoint point)
	{
		this._storedCoord = point;
	}
	
	public CGPoint getStoredCoord()
	{
		return this._storedCoord;
	}
	
	//Set the size to a desired width
	public void resize(CGSize theSize)
	{
		float newWidth = theSize.width;
	    float newHeight = theSize.height;
	    float startWidth = this._sprite.getContentSize().width;
	    float startHeight = this._sprite.getContentSize().height;
	    float newScaleX = newWidth/startWidth;
	    float newScaleY = newHeight/startHeight;

	    this._sprite.setScaleX(newScaleX);
	    this._sprite.setScaleY(newScaleY);
	    this._sprite.setAnchorPoint(0.5f, 0.5f);
	}
	
	public CGSize getActualSize()
	{
		return CGSize.make(
			this._sprite.getContentSize().width*this._sprite.getScaleX(),
			this._sprite.getContentSize().height*this._sprite.getScaleY());
	}
	
	public CCSprite getSprite()
	{
		return this._sprite;
	}
	
	//Check, based off of the supplied control, whether the block is touched
	public boolean isSelected()
	{
		return true;
	}
	
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
	public abstract AlienType getType();
}
