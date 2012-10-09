package hr.gdd.puzzle.gage.demo;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

public class PGButton extends SpriteWrapper
{
	private String _identifier;
	
	//Constructor
	public PGButton(String sprResource, String identifier)
	{
		this._sprite = CCSprite.sprite(sprResource);
		this._sprite.setAnchorPoint(0.5f, 0.5f);
		
		this._identifier = identifier;
	}
	
	public void setIdentifier(String id)
	{
		this._identifier = id;
	}
	
	public String getIdentifier()
	{
		return this._identifier;
	}
}
