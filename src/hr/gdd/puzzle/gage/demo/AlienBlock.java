package hr.gdd.puzzle.gage.demo;

import org.cocos2d.nodes.CCSprite;

public class AlienBlock extends Block 
{
	//Fields
	private BlockType _type;
	
	//Constructor
	public AlienBlock(BlockConfig config)
	{
		//this._sprite.setPosition(config.getPosition());
		this._type = config.getType();
		
		//Setup sprite based on the type
		switch(this._type)
		{
			case Alex:
				//this._sprite = CCSprite.sprite("Alex.png");
				this._sprite = CCSprite.sprite("Blauw_Volledig.png");
				break;
			case Daisy:
				//this._sprite = CCSprite.sprite("Daisy.png");
				this._sprite = CCSprite.sprite("Roze_Volledig.png");
				break;
			case Brain:
				//this._sprite = CCSprite.sprite("Brain.png");
				this._sprite = CCSprite.sprite("Groen_Volledig.png");
				break;
			case Crazy:
				//this._sprite = CCSprite.sprite("Crazy.png");
				this._sprite = CCSprite.sprite("Geel_Volledig.png");
				break;
			case Rocky:
				//this._sprite = CCSprite.sprite("Rocky.png");
				this._sprite = CCSprite.sprite("Rood_Volledig.png");
				break;
			default:
				this._sprite = CCSprite.sprite("Alex.png");
				break;
		}
	}
	
	//Implemented abstract methods
	@Override
	public BlockType getType() {
		return this._type;
	}
}
