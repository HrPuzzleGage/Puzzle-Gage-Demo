package hr.gdd.puzzle.gage.demo;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

public abstract class Block 
{
	//Static fields
	private static CGSize _blDimensions = CGSize.make(32, 32);
	
	//Constants
	protected static final float FALLSPEED = _blDimensions.height/5.0f;
	
	//Fields
	protected CCSprite _sprite;
	
	//Static methods
	public static CGSize getBlockDimensions()
	{
		return _blDimensions;
	}
	
	//Move the block using its falling speed
	public void fall()
	{
		//This might be done using actions in Cocos2D
		//this.setPosition(this.getPosition().x, this.getPosition().y + FALLSPEED);
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
	    this._sprite.setAnchorPoint(0.0f, 1.0f);
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
		
	}
	
	//Deselect the block
	public void unselect()
	{
		
	}
	
	//Abstract methods to be implemented by subclasses
	public abstract String getType();
}
