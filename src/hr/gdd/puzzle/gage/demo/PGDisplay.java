package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class PGDisplay
{
	//Fields
	private ArrayList<PGButton> _buttons;
	private CCSprite _sprite;
	
	//Constructor
	public PGDisplay(ArrayList<PGButton> buttons)
	{
		//Set the texture
		this._sprite = CCSprite.sprite("interface.png");
		this._sprite.setAnchorPoint(0.0f, 1.0f);
		
		this._buttons = buttons;
	}
	
	//Set the size to a desired width
	public void resizeDisplay(CGSize theSize)
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
	
	//Add a button to the display
	public void AddButton(PGButton button)
	{
		this._buttons.add(button);
	}
	
	//Overload for adding multiple buttons to the display at once
	public void AddButton(ArrayList<PGButton> buttons) {
		this._buttons.addAll(buttons);
	}
	
	//Clear all buttons
	public void ClearButtons()
	{
		this._buttons.clear();
	}
	
	//Getter methods
	public CCSprite getSprite()
	{
		return this._sprite;
	}
}
