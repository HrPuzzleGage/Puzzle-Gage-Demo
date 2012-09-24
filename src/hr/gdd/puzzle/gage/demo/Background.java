package hr.gdd.puzzle.gage.demo;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class Background 
{
	//Fields
	private CCSprite _sprite;
	
	//Constructor
	public Background()
	{
		this._sprite = CCSprite.sprite("backdrop.png");
		this._sprite.setAnchorPoint(0.0f, 1.0f);
	}
	
	//Set the size to a desired width
	public void resizeBack(CGSize theSize)
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
	
	public CGPoint getAbsoluteSize()
	{
		return CGPoint.ccp(this._sprite.getScaleX()*this._sprite.getContentSize().width, this._sprite.getScaleY()*this._sprite.getContentSize().height);
	}
	
	public CCSprite getSprite()
	{
		return this._sprite;
	}
}
