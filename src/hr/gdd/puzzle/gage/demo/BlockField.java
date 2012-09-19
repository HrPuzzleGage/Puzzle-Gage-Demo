package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;

import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

public class BlockField extends CCSprite 
{
	//Fields
	private Block[][] _blocks;
	private CGRect _fieldBounds;
	private Block[] _switching;
	
	//Constructor
	public BlockField(CGPoint griddims)
	{
		//Initialize the new blocks array
		this._blocks = new Block[(int) griddims.x][(int) griddims.y];
	}
	
	//Getter methods
	public CGRect getFieldBounds() 
	{
		return _fieldBounds;
	}
	
	//Setter methods
	public void setFieldBounds(CGRect fieldBounds) 
	{
		this._fieldBounds = fieldBounds;
	}
	
	//Setup all the blocks in the field using the current grid
	public void SetupBlocks(ArrayList<Block> blocks)
	{
		
	}
	
	//Based off of the orientation, transform the grid along with the blocks within
	public void TransformGrid(String orientation)
	{
		
	}
	
	//Remove a block from the grid
	public void removeBlock(Block bl)
	{
		
	}
	
	//Check if all blocks are idle and not falling
	public boolean allBlocksIdle()
	{
		return true;
	}
	
	//Check if there is a combination within the grid
	public boolean checkCombination()
	{
		return false;
	}
	
	//Check if there are still any possible combinations on the grid
	public boolean CombinationPossible()
	{
		return true;
	}
	
	//Check if there is a block next to the target block in the indicated space (left or right)
	public boolean BlockNext(Block bl, String space)
	{
		return false;
	}
	
	//Start the switching of two blocks
	public void StartSwitch(Block bl1, Block bl2)
	{
		
	}
	
	//Start a reversed switch
	public void ReverseSwitch()
	{
		
	}
	
	//Check if the field is currently switching
	public boolean isSwitching()
	{
		return false;
	}
	
	//End the switching of two blocks if a switch is taking place
	public void EndSwitch()
	{
		
	}
	
	//Game loop
	public void GameLoop(float dt)
	{
	
	}
}
