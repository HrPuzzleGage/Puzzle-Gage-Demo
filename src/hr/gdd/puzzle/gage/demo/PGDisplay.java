package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;

public class PGDisplay extends SpriteWrapper
{
	private ButtonBox _buttonBox;
	private LinkedHashMap<CCLabel, DisplayLabel> _labels;
	private CCNode _labelContainer;
	
	/*
	 * Constructor: Will load the interface's default sprite and adds the provided buttons
	 */
	public PGDisplay(ArrayList<PGButton> buttons, LinkedHashMap<CCLabel, DisplayLabel> labels)
	{
		this._sprite = CCSprite.sprite("interface_back.bmp");
		this._sprite.setAnchorPoint(0.5f, 0.5f);
		this._buttonBox = new ButtonBox(buttons);
		this._labels = labels;
		this._labelContainer = CCNode.node();
	}
	
	/*
	 * Override the SpriteWrapper cleanSprite() method to remove all display objects involved in the display
	 */
	@Override
	public void cleanSprite()
	{
		super.cleanSprite();
		this._buttonBox.getButtonContainer().removeFromParentAndCleanup(true);
		for(CCLabel l : this._labels.keySet())
		{
			l.setString("0");
			l.removeFromParentAndCleanup(true);
		}
	}
	
	public ButtonBox getButtonBox()
	{
		return this._buttonBox;
	}
	
	public void addLabel(CCLabel label, DisplayLabel dl)
	{
		this._labels.put(label, dl);
	}
	
	public CCNode getLabelBox()
	{
		return this._labelContainer;
	}
	
	public void positionLabels(CGPoint pos, float dir)
	{
		this._labelContainer.removeAllChildren(true);
		this._labelContainer.setPosition(pos);
		this._labelContainer.setRotation(0.0f);
		
		float currX = 0;
		float currY = 0;
		//boolean started = false;
		
		for(CCLabel l : this._labels.keySet())
		{
			l.setAnchorPoint(0.0f, 1.0f);
			l.setPosition(currX, currY);
			this._labelContainer.addChild(l);
			
			currY -= l.getContentSize().height;
		}
		
		this._labelContainer.setRotation(dir);
	}
	
	public void modifyLabelsTextByType(DisplayLabel type, String newText)
	{
		for(CCLabel l : this._labels.keySet())
		{
			if(this._labels.get(l) == type) l.setString(newText); 
		}
	}
	
	public void configureAllLabels(ccColor3B col)
	{
		for(CCLabel l : this._labels.keySet()) l.setColor(col);
	}
}
