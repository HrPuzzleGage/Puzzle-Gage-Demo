package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;

import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class ButtonBox 
{
	private ArrayList<PGButton> _buttons;
	private float _buttonPaddingSides = 0.05f;
	private CGSize _bounds;
	private CCNode _buttonContainer;
	private float _lastPadding = 0.0f;
	
	public ButtonBox(ArrayList<PGButton> buttons)
	{
		this._buttons = buttons;
		this._buttonContainer = CCNode.node();
	}
	
	/*
	 * Get the sprites of all buttons associated with the display interface
	 */
	public ArrayList<CCSprite> getButtonSprites()
	{
		ArrayList<CCSprite> spr = new ArrayList<CCSprite>();
		for(PGButton b : this._buttons) spr.add(b.getSprite());
		
		return spr;
	}
	
	/*
	 * Add a button instance to the display interface
	 */
	public void AddButton(PGButton button)
	{
		this._buttons.add(button);
	}
	
	/*
	 * Overload for adding a range of button instances to the display interface
	 */
	public void AddButton(ArrayList<PGButton> buttons) 
	{
		this._buttons.addAll(buttons);
	}
	
	/*
	 * Clear all buttons associated with the display interface, making sure to clean their sprites up first
	 */
	public void ClearButtons()
	{
		for(PGButton b : this._buttons) b.cleanSprite();
		this._buttons.clear();
	}
	
	/*
	 * Provided with a location, this method will find if any button sprites intersect the position and return the corresponding button instance
	 */
	public PGButton buttonClickedAt(CGPoint pos)
	{
		for(PGButton b : this._buttons)
		{
			if(b.hasPoint(this._buttonContainer.convertToNodeSpace(pos))) return b;
		}
		
		//Return null if nothing has been found
		return null;
	}
	
	/*
	 * Obtain the buttons container node
	 */
	public CCNode getButtonContainer()
	{
		return this._buttonContainer;
	}
	
	/*
	 * Obtain the total size of the button box.
	 */
	public CGSize getTotalSize()
	{
		return CGSize.make(this._buttonContainer.getBoundingBox().size.width, 
			this._buttonContainer.getBoundingBox().size.height);
	}
	
	/*
	 * Obtain the total size of the button box including the last padding that it was calculated with.
	 */
	public CGSize getSizeWithLastPadding()
	{
		return CGSize.make(this._buttonContainer.getBoundingBox().size.width+(this._buttonContainer.getScale()*this._lastPadding), 
			this._buttonContainer.getBoundingBox().size.height+(this._buttonContainer.getScale()*this._lastPadding));
	}
	
	/*
	 * Position and scale the sprites associated with the buttons. 
	 */
	public void positionScaleButtonSprites(CGSize bounds, float dir)
	{
		this.positionScaleButtonSpritesPadding(bounds, dir, 0.0f);
	}
	
	/*
	 * Position and scale the sprites associated with the buttons, passing the default padding.
	 */
	public void positionScaleButtonSpritesPadding(CGSize bounds, float dir)
	{
		this.positionScaleButtonSpritesPadding(bounds, dir, this._buttonPaddingSides);
	}
	
	/*
	 * Position and scale the sprites associated with the buttons, passing it a custom padding.
	 */
	public void positionScaleButtonSpritesPadding(CGSize bounds, float dir, float paddingToUse)
	{	
		//Calculate padding between the buttons as well as the multiplication factor based on the provided direction
		float padding = bounds.width*paddingToUse*2;
		float currY = 0;
		
		float x1 = 0;
		float y1 = 0;
		float x2 = 0;
		float y2 = 0;
		boolean started = false;
		
		this._buttonContainer.removeAllChildren(true);
		this._buttonContainer.setContentSize(CGSize.zero());
		this._buttonContainer.setScale(1.0f);
		this._buttonContainer.setRotation(0.0f);
		
		//Iterate over all buttons associated with the display
		for(PGButton b : this._buttons)
		{
			if(!started) currY -= b.getSizeIncludingScale().height/2;
			
			b.getSprite().setPosition(0, currY);
			this._buttonContainer.addChild(b.getSprite());
			
			//Do not forget to add to the positions for the next iteration
			currY -= b.getSizeIncludingScale().height+(padding/2);
			CGPoint or = b.getSprite().getBoundingBox().origin;
			CGSize si = b.getSprite().getBoundingBox().size;
			
			if(!started)
			{
				x1 = or.x;
				x2 = x1+si.width;
				y1 = or.y;
				y2 = y1+si.height;
				
				started = true;
				continue;
			}
			
			if(or.x < x1) x1 = or.x;
			if(or.y < y1) y1 = or.y;
			if(or.x+si.width > x2) x2 = or.x+si.width;
			if(or.y+si.height > y2) y2 = or.y+si.height;
		}
		
		this._bounds = CGSize.make(x2-x1, y2-y1);
		this._buttonContainer.setContentSize(this._bounds); //<AARGH!
		this._buttonContainer.setRotation(dir);
		this._buttonContainer.setAnchorPoint(0.0f, -0.5f );
		this._lastPadding = padding;
		
		float scaleFactor = 1.0f;
		boolean currV = this.getTotalSize().height > this.getTotalSize().width;
		boolean newV = bounds.height-padding > bounds.width-padding;
		
		if(currV == newV) scaleFactor = (bounds.width-padding)/this.getTotalSize().width;
		else scaleFactor = (bounds.width-padding)/this.getTotalSize().height;
		
		this._buttonContainer.setScale(scaleFactor);
	}
}
