package hr.gdd.puzzle.gage.demo;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGRect;

public abstract class Block extends CCSprite {
	//Static fields
	private static CGRect _blDimensions = CGRect.make(0, 0, 32, 32);
	
	//Constants
	protected static final float FALLSPEED = CGRect.height(_blDimensions)/5.0f;
	
	//Static methods
	public static CGRect getBlockDimensions()
	{
		return _blDimensions;
	}
	
	//Move the block using its falling speed
	public void fall()
	{
		//This might be done using actions in Cocos2D
		this.setPosition(this.getPosition().x, this.getPosition().y + FALLSPEED);
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
