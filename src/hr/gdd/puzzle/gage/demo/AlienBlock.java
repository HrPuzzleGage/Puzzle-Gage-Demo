package hr.gdd.puzzle.gage.demo;

import org.cocos2d.nodes.CCSprite;

public class AlienBlock extends Block 
{
	//Fields
	private String _type;
	
	//Constructor
	public AlienBlock(BlockConfig config)
	{
		//this._sprite.setPosition(config.getPosition());
		this._type = config.getType();
		
		//Setup sprite based on the type
		this._sprite = CCSprite.sprite("Nerdy.png");
		this._sprite.setAnchorPoint(0.0f, 1.0f);
	}
	
	//Implemented abstract methods
	@Override
	public String getType() {
		return this._type;
	}
}
