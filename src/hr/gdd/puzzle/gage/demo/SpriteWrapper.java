package hr.gdd.puzzle.gage.demo;

import java.util.Observable;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

public abstract class SpriteWrapper extends Observable
{
	protected CCSprite _sprite;
	protected CGSize _size;
	protected float _baseRot = 0;
	
	//Obtain the sprite associated with the wrapper
	public CCSprite getSprite()
	{
		return this._sprite;
	}
	
	//Remove the sprite as a child
	public void cleanSprite()
	{
		if(this._sprite.getParent() != null) this._sprite.removeFromParentAndCleanup(true);
	}
	
	//Move sprite
	public void addPosition(float x, float y)
	{
		this._sprite.setPosition(this._sprite.getPosition().x+x, this._sprite.getPosition().y+y);
	}
	
	//Return the sprite's content size + scale
	public CGSize getSizeIncludingScale()
	{
		if(this._size == null)
		{
			this._size = CGSize.make(this._sprite.getContentSize().width*this._sprite.getScaleX(),
				this._sprite.getContentSize().height*this._sprite.getScaleY());
		}
		
		return this._size;
		//return CGSize.make(this._sprite.getBoundingBox().size.width, this._sprite.getBoundingBox().size.height);
	}
	
	//Check if the sprite's bounding box contains the given point
	public boolean hasPoint(CGPoint pos)
	{
		return CGRect.containsPoint(this._sprite.getBoundingBox(), pos);
	}
	
	//Scales the sprite so that its longest side is equal to the provided sideSize
	public void squareScale(int sideSize)
	{
		float AR = this._sprite.getContentSize().height / this._sprite.getContentSize().width;
		float scaleFactor = 1;
		
		if(AR >= 1) scaleFactor = sideSize/this._sprite.getContentSize().height;
		else scaleFactor = sideSize/this._sprite.getContentSize().width;
		
		this._size = CGSize.make(this._sprite.getContentSize().width*scaleFactor, this._sprite.getContentSize().height*scaleFactor);
		this._sprite.setScale(scaleFactor);
		this._sprite.setAnchorPoint(0.5f, 0.5f);
	}
	
	//Set the angle of the sprite
	public void setAngle(float rotation)
	{
		this._sprite.setRotation(rotation+this._baseRot);
	}
	
	//Setting the angle without any rotation will revert the sprite to its base angle
	public void setAngle()
	{
		this._sprite.setRotation(this._baseRot);
	}
	
	//Scale X and Y of the sprite's content independently to fit within the provided rectangle
	public void scaleSizeToPixels(CGSize theSize)
	{
		boolean newSizeVert = (theSize.height/theSize.width) > 1;
		boolean currSizeVert = (this._sprite.getContentSize().height/this._sprite.getContentSize().width) > 1;
		
		float scaleX = newSizeVert == currSizeVert ? theSize.width/this._sprite.getContentSize().width : theSize.height/this._sprite.getContentSize().width;
		float scaleY = newSizeVert == currSizeVert ? theSize.height/this._sprite.getContentSize().height : theSize.width/this._sprite.getContentSize().height;

	    this._sprite.setScaleX(scaleX);
	    this._sprite.setScaleY(scaleY);
	    
	    float finalSizeX = this._sprite.getScaleX()*this._sprite.getContentSize().width;
	    float finalSizeY = this._sprite.getScaleY()*this._sprite.getContentSize().height;
	    
	    if(newSizeVert != currSizeVert)
		{
			this._baseRot = 90.0f;
			this.setAngle();
			
			this._size = CGSize.make(finalSizeY, finalSizeX);
		} else this._size = CGSize.make(finalSizeX, finalSizeY);
	    
	    this._sprite.setAnchorPoint(0.5f, 0.5f);
	}
	
	//Scale X and Y of the sprite's content independently to fit within the provided rectangle, keeping a dimensions constraint and rotating it if needed...
	public void scaleSizeToPixelsWithConstraint(CGSize newSize, CGPoint con)
	{
		CGSize currSize = this._sprite.getContentSize();
		CGSize desSize = CGSize.getZero();
		
		float widthStep = newSize.width/con.x;
		desSize.width = widthStep * con.x;
		desSize.height = widthStep * con.y;
		
		float scaleFactor = newSize.width/desSize.width;
		desSize.width = scaleFactor*desSize.width;
		desSize.height = scaleFactor * desSize.height;
		
		if(desSize.height > newSize.height)
		{
			scaleFactor = newSize.height/desSize.height;
			desSize.width = scaleFactor*desSize.width;
			desSize.height = scaleFactor * desSize.height;
		}
		
		boolean newVertical = (desSize.height/desSize.width) > 1;
		boolean currVertical = (currSize.height/currSize.width) > 1;
		
		float scaleX = currVertical == newVertical ? desSize.width/currSize.width : desSize.height/currSize.width;
		float scaleY = currVertical == newVertical ? desSize.height/currSize.height : desSize.width/currSize.height;
		
		this._sprite.setScaleX(scaleX);
		this._sprite.setScaleY(scaleY);
		
		if(currVertical != newVertical)
		{
			this._baseRot = 90.0f;
			this.setAngle();
			this._size = CGSize.make(scaleY*currSize.height, scaleX*currSize.width);
		}
		else this._size = CGSize.make(scaleX*currSize.width, scaleY*currSize.height);
		
		this._sprite.setAnchorPoint(0.5f, 0.5f);
	}
}
