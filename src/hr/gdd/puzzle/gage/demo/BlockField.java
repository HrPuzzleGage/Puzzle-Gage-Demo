package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;
import java.util.Observable;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.instant.CCCallFuncND;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import android.util.Log;

public class BlockField extends SpriteWrapper
{
	//-----------------Fields
	private Block[][] _blocks;
	private Block _firstSw;
	private CGPoint _dims;
	private Orientation _lastOrientation;
	private int _falling = 0;
	private int _switching = 0;
	private SwitchState _switchState = SwitchState.None;
	
	//-----------------Constructor
	public BlockField(CGPoint griddims)
	{
		//Set the texture to be used for the sprite
		this._sprite = CCSprite.sprite("Trans.png");
		
		//Last orientation
		this._lastOrientation = Orientation.Portrait;
		
		//Initialize the new blocks array based on the supplied dimensions
		this._dims = griddims;
		this._blocks = new Block[(int) griddims.x][(int) griddims.y];
	}
	
	//-----------------Getter Methods
	//Get the dimensions for the block field (how many by how many blocks there are)
	public CGPoint getGridDimensions()
	{
		return this._dims;
	}
	
	@Override
	public void cleanSprite()
	{
		super.cleanSprite();
		for(CCSprite spr : this.getBlockSprites()) spr.removeFromParentAndCleanup(true);
		
		this._blocks = new Block[(int) this._dims.x][(int) this._dims.y];
	}
	
	//Gets the sprites of all the blocks to return
	public ArrayList<CCSprite> getBlockSprites()
	{
		ArrayList<CCSprite> sprites = new ArrayList<CCSprite>();
		
		//Iterate over all blocks in the grid
		for(Block[] b : this._blocks) for(Block bb : b)
		{
			//If there is an existent block instance, add it to the list of sprites to return
			if(bb != null) sprites.add(bb.getSprite());
		}
		
		return sprites;
	}
	
	//-----------------Setter Methods
	public void changeFieldOrientation(Orientation orientation)
	{
		if(this._switchState != SwitchState.None) return;
		
		ArrayList<CCSprite> spr = this.getBlockSprites();
		if(this._lastOrientation != orientation)
		{
			this._lastOrientation = orientation;
			
			//Rotation of all sprites in the grid
			float newRotation = this.orientationToRotation(orientation);
			for(CCSprite s : spr) s.setRotation(newRotation);
		}
		
		if(spr.size() > 0) this.blocksToFall();
	}
	
	//Calls the parent's method to scale to the given size, using the grid as a default constraint
	public void scaleSizeToPixelsWithConstraint(CGSize newSize)
	{
		this.scaleSizeToPixelsWithConstraint(newSize, this._dims);
	}
		
	//Create the blocks according to a provided list of block configurators. Only works if the field has a size
	public void SetupBlocks(ArrayList<BlockConfig> blocks)
	{
		if(this._size == null) return;
		
		//Find the longest and shortest sides of both the grid dimensions and the field's size
		float longSideDims = Math.max(this._dims.x, this._dims.y);
		float shortSideDims = Math.min(this._dims.x, this._dims.y);
		float longSideSize = Math.max(this._size.width, this._size.height);
		float shortSideSize = Math.min(this._size.width, this._size.height);
		
		//Set the scale factor of the newly created blocks based on the shortest and longest sides
		float newBlockW = shortSideSize/shortSideDims;
		float newBlockH = longSideSize/longSideDims;
		CGSize newBlockSize = CGSize.make(newBlockW, newBlockH);
		
		//Iterate over the supplied block configurators
		for(BlockConfig bc : blocks)
		{
			//Convert the block configurator into a new Block instance and where it should be placed in the grid
			Block newBlock = bc.obtainBlock();
			CGPoint gridPos = bc.getPosition();
			
			//Exit the current iteration once it becomes clear the block is outside the virtual grid...
			if(gridPos.x < 0 || gridPos.x > this._dims.x-1 || gridPos.y < 0 || gridPos.y > this._dims.y-1) continue;
			
			//Resize and reposition the newly created block
			newBlock.scaleSizeToPixels(newBlockSize);
			
			//Add the block to the grid
			newBlock.setGridCoord(gridPos);
			this._blocks[(int)gridPos.x][(int)gridPos.y] = newBlock;
		}
		
		//Position the blocks
		this.positionBlocks();
	}
	
	//Positions the field sprite; if size of the field is known position the blocks as well!
	public void positionField(CGPoint pos)
	{
		this._sprite.setPosition(pos);
		if(this._size != null) this.positionBlocks();
	}
	
	//Position the blocks within the field
	private void positionBlocks()
	{
		for(int i=0; i < this._dims.x; i++) for(int j = 0; j < this._dims.y; j++)
		{
			if(this._blocks[i][j] != null)
			{
				Block newBlock = this._blocks[i][j];
				float w = newBlock.getSizeIncludingScale().width;
				float h = newBlock.getSizeIncludingScale().height;
				
				float newPositionX = (w/2)+(newBlock.getGridCoord().x*w) + (this._sprite.getPosition().x-(this._size.width/2));
				float newPositionY = (-h/2)+(-newBlock.getGridCoord().y*h) + (this._sprite.getPosition().y+(this._size.height/2));
				
				newBlock.getSprite().setPosition(newPositionX, newPositionY);
			}
		}
	}

	//Callback method for handling results of move actions depending on the sender object's CCSprite tag
	public void moveHandler(Object sender, Object d)
	{
		CCSprite s = (CCSprite) sender;
		
		//Decide what to do based on sender's tag
		if(s.getTag() == 1)
		{
			//Tell the Block Field that the block has fallen the space it needed to fall
			this._falling--;
			
			//Move the block to its new position in the array and set that position's value in the array to the block
			Block block = (Block)d;
			CGPoint grTarget = block.getGridCoord(); 
			this._blocks[(int)grTarget.x][(int)grTarget.y] = block;
			
			//If this is zero, it means all blocks that were required to fall have fallen. Check again to see what blocks have to fall now
			if(this._falling == 0) this.blocksToFall();
		}
		else if(s.getTag() == 2)
		{
			this._switching--; 
			
			if(this._switching != 0) return;
			Block[] test = (Block[]) d;
			
			//Set the position in the grid, according to the switch array's stored coordinates
			this._blocks[(int) test[0].getGridCoord().x][(int) test[0].getGridCoord().y] = test[0];
			this._blocks[(int) test[1].getGridCoord().x][(int) test[1].getGridCoord().y] = test[1];
			this.setChanged();
			
			//If a reverse switch is not taking place, end the switch
			if(this._switchState != SwitchState.ISwitching)
			{
				//If a combination was found in response to the switch, clear the switch. Otherwise, reverse the switch...
				if(this.checkCombination()) 
				{
					this._switchState = SwitchState.None;
					this.clearSwitch();
					this.blocksToFall();
				}
				else this.StartSwitch(test[1], test[0]);
			} 
			else
			{
				this._switchState = SwitchState.None;
				this.clearSwitch();
				this.blocksToFall();
			}
			this.clearChanged();
		}
	}
	
	//Check if there are still any possible combinations on the grid
	public void CombinationPossible()
	{
		//Notify everything that is observing the block field atm, could be extended later on
		this.setChanged();
		this.notifyObservers(new EventData(EventType.DoneChecking));
		this.clearChanged();
	}
	
	//Check if there is a block next to the target block in the indicated swipe direction
	public boolean AttemptSwitch(SwipeDirection dir)
	{
		//Abort if there is no starting block, there is no direction to swipe into or if a switch is already happening atm
		if(this._firstSw == null || dir == SwipeDirection.None || this._switchState != SwitchState.None) return false;
		
		//Get the first block and of the switch as well as the coordinates that were stored into it
		CGPoint firstBlock = this._firstSw.getGridCoord();
		int posToCheckX = 0;
		int posToCheckY = 0;
		int tempVal = 0;
		
		//Initialize positions to Check based on swipe direction, transforming it based on screen orientation
		if(dir == SwipeDirection.Left) posToCheckX = -1;
		else posToCheckX = 1;
		
		switch(this._lastOrientation)
		{
			case Landscape:
				tempVal = posToCheckY;
				posToCheckY = posToCheckX;
				posToCheckX = tempVal;
				
				break;
			case ILandscape:
				tempVal = posToCheckY;
				posToCheckY = -posToCheckX;
				posToCheckX = -tempVal;
				
				break;
			case IPortrait:
				posToCheckY = -posToCheckY;
				posToCheckX = -posToCheckX;
				
				break;
			default: 
				break;
		}
		
		//Check the position
		if(firstBlock.x+posToCheckX >= 0 && firstBlock.y+posToCheckY >= 0 && firstBlock.x+posToCheckX < this._dims.x &&  firstBlock.y+posToCheckY < this._dims.y && this._blocks[(int)firstBlock.x+posToCheckX][(int)firstBlock.y+posToCheckY] != null)
		{
			Block target = this._blocks[(int)firstBlock.x+posToCheckX][(int)firstBlock.y+posToCheckY];
			target.setGridCoord(CGPoint.ccp(firstBlock.x+posToCheckX, firstBlock.y+posToCheckY));
			
			//Start a switch between to blocks now that two blocks were found; the method will return true indicating to the caller a switch is set up to start
			this.StartSwitch(this._firstSw, target);
			return true;
		}  
		
		//No switch takes place; notify the caller of that
		return false;
	}

	//Selects the grid position of first encountered block that intersects with the given location
	public void selectBlockAt(CGPoint pos)
	{
		this.clearSwitch();
		
		//Iterate over all blocks in the grid
		for(int i = 0; i < this._blocks.length; i++) for(int j = 0; j < this._blocks[i].length; j++)
		{
			//If a block is found
			if(this._blocks[i][j] != null)
			{ 
				Block b = this._blocks[i][j];
				
				//See if the sprite associated with the targeted block contains the position
				if(CGRect.containsPoint(b.getSprite().getBoundingBox(), pos))
				{
					//Select the block and save it
					b.select();
					this._firstSw = b;
					
					//Stop the function
					return;
				}
			}
		}
	}
	
	//Clear any data that is associated with a switch
	public void clearSwitch()
	{
		//A switch should not be cleared while it is already taking place
		if(this._switchState != SwitchState.None || this._firstSw == null) return;
		
		this._firstSw.unselect();
		this._firstSw = null;
	}
	
	//Calculate a rotation according to an orientation enumeration value
	private float orientationToRotation(Orientation orientation)
	{
		switch(orientation)
		{
			case IPortrait:
				return 180.0f;
			case Landscape:
				return 90.0f;
			case ILandscape:
				return 270.0f;
			default:
				return 0.0f;
		}
	}
	
	//Start the switching of two blocks
	private void StartSwitch(Block bl1, Block bl2)
	{
		//Test
		bl1.getSprite().setTag(2);
		bl2.getSprite().setTag(2);
		Block[] test = new Block[]{bl1, bl2};
		
		//Create move actions, supplying one with the callback method (only one is needed)
		CCMoveTo actionMove1 = CCMoveTo.action(Block.switchTime, bl2.getSprite().getPosition());
		CCMoveTo actionMove2 = CCMoveTo.action(Block.switchTime, bl1.getSprite().getPosition());
	    CCCallFuncN actionMoveDone = CCCallFuncND.action(this, "moveHandler", test);
	    CCSequence actions = CCSequence.actions(actionMove1, actionMoveDone);
	    CCSequence actions2 = CCSequence.actions(actionMove2, actionMoveDone);
	    
	    //Set the current positions of the blocks to null
	    this._blocks[(int) bl1.getGridCoord().x][(int) bl1.getGridCoord().y] = null;
	    this._blocks[(int) bl2.getGridCoord().x][(int) bl2.getGridCoord().y] = null;
	    
	    //Swap the two blocks' stored coordinates
	    CGPoint stored = bl1.getGridCoord();
	    bl1.setGridCoord(bl2.getGridCoord());
	    bl2.setGridCoord(stored);
	    
	    //Here states of the blockfield will be set
	    if(this._switchState == SwitchState.Switching) this._switchState = SwitchState.ISwitching; 
	    else this._switchState = SwitchState.Switching;
	    
	    this._switching+=2;
	    
	    //Run the move actions to switch the blocks
	    bl1.getSprite().runAction(actions);
	    bl2.getSprite().runAction(actions2);
	}
	
	//Check if there is a combination within the grid
	private boolean checkCombination()
	{
		//Construct new array containing the blocks that belong to a match
		boolean[][] matchMap = new boolean[(int)this._dims.x][(int)this._dims.y];
		
		//Score/combo values
		int totalScore = 0;
		int totalCombo = 0;
		
		int defScore = 100;
		int defScoreAdd = 50;
		
		//Search through columns for matches
		for(int i = 0; i < this._blocks.length; i++)
		{
			//Initialize number of found blocks in the current column
			int found = 0;
			BlockType lastType = BlockType.Alex;
			
			//Iterate over the current column
			for(int j = 0; j < this._blocks[i].length; j++)
			{
				//If a block instance has been found...
				if(this._blocks[i][j] != null)
				{
					Block currBlock = this._blocks[i][j];
					
					//Set the length of the match to zero if the type does not match
					if(found == 0) lastType = currBlock.getType();
					else if(lastType != currBlock.getType())
					{
						found = 0;
						lastType = currBlock.getType();
					}
					
					found++;
					
					//If a match of three more was found, increase the combo and the score accordingly
					if(found == 3)
					{
						matchMap[i][j] = matchMap[i][j-1] = matchMap[i][j-2] = true;
						totalScore += defScore*(totalCombo+1);
						totalCombo++;
					}
					else if(found > 3)
					{
						matchMap[i][j] = true;
						totalScore += defScoreAdd*totalCombo;
					}
				} else found = 0;
			}
		}
		
		//Search through rows for matches
		for(int k = 0; k < this._blocks[0].length; k++)
		{
			//Initialize number of found blocks in the current row
			int found = 0;
			BlockType lastType = BlockType.Alex;
			
			//Iterate over the current row
			for(int l = 0; l < this._blocks.length; l++)
			{
				//If a block instance has been found...
				if(this._blocks[l][k] != null)
				{
					Block currBlock = this._blocks[l][k];
					
					//Set the length of the match to zero if the type does not match
					if(found == 0) lastType = currBlock.getType();
					else if(lastType != currBlock.getType())
					{
						found = 0;
						lastType = currBlock.getType();
					}
					
					found++;
					
					//If a match of three more was found, increase the combo and the score accordingly
					if(found == 3)
					{
						matchMap[l][k] = matchMap[l-1][k] = matchMap[l-2][k] = true;
						totalScore += defScore*(totalCombo+1);
						totalCombo++;
					}
					else if(found > 3)
					{
						matchMap[l][k] = true;
						totalScore += defScoreAdd*totalCombo;
					}
				} else found = 0;
			}
		}
		
		//If no matches were found, check the block field to see if combinations are possible to begin with
		if(totalCombo > 0) 
		{
			//Score
			EventData data = new EventData(EventType.ScoreUpdate);
			data.addData("score", totalScore);
			data.addData("combo", totalCombo);
			
			this.setChanged();
			this.notifyObservers(data);
			this.clearChanged();
			
			this.removeBlocks(matchMap);
			return true;
		}
		
		return false;
	}
	
	//Remove blocks from the blocks grid using a boolean map
	private void removeBlocks(boolean[][] removeMap)
	{
		//Iterate over the remove map and remove the matching portions from the blocks array
		for(int i = 0; i < removeMap.length; i++) for(int j = 0; j < removeMap[i].length; j++)
		{
			if(removeMap[i][j] && this._blocks[i][j] != null)
			{ 
				this._blocks[i][j].getSprite().removeFromParentAndCleanup(true);
				this._blocks[i][j] = null;
			}
		}
	}

	//Decide what blocks need to fall
	private void blocksToFall()
	{
		//Blocks may not fall if a switch is in progress, no matter how everything begs for it
		if(this._switchState != SwitchState.None) return; 
		
		//Decide on a number to use for checking the field's virtual grid 
		int checkAddI = 0;
		int checkAddJ = 0;
		
		switch(this._lastOrientation)
		{
			case IPortrait:
				checkAddJ--;
				break;
			case Landscape:
				checkAddI--;
				break;
			case ILandscape:
				checkAddI++;
				break;
			default:
				checkAddJ++;
				break;
		}
		
		//Iterate over the grid; when a block is detected, check a neighbouring square based on screen orientation
		for(int i = 0; i < this._blocks.length; i++) for(int j = 0; j < this._blocks[i].length; j++)
		{
			if(this._blocks[i][j] != null && checkAddI+i <= (this._dims.x-1) && checkAddI+i >= 0 && checkAddJ+j <= (this._dims.y-1) && checkAddJ+j >= 0 && this._blocks[i+checkAddI][j+checkAddJ] == null)
			{
				Block blockToMove = this._blocks[i][j];
				
				//Create a new Cocos2D move action for the targeted block to use; it moves one square depending on screen orientation
				CCMoveTo actionMove = CCMoveTo.action(Block.fallTime, CGPoint.ccp(
						blockToMove.getSprite().getPosition().x+(blockToMove.getSizeIncludingScale().width*checkAddI), 
						blockToMove.getSprite().getPosition().y-(blockToMove.getSizeIncludingScale().height*checkAddJ)));
			    CCCallFuncN actionMoveDone = CCCallFuncND.action(this, "moveHandler", blockToMove);
			    CCSequence actions = CCSequence.actions(actionMove, actionMoveDone);
			    
			    //Change the block's grid coordinate
			    blockToMove.setGridCoord(CGPoint.ccp(i+checkAddI, j+checkAddJ));
			    
			    //Remove the block from the blocks grid
			    this._blocks[i][j] = null;
			    
			    //Execute the action and make sure the Block field knows another block is falling
			    blockToMove.getSprite().setTag(1);
			    blockToMove.getSprite().runAction(actions);
			    this._falling++;
			}
		}
		
		//If it is concluded after the calculation that no blocks are falling, then check for a combination
		//Otherwise, ensure that any potential switches (blocks selected for it) are cleaned up
		if(this._falling == 0) 
		{
			//Move over to combinationPossible if checkCombination has returned false, otherwise repeat the method
			if(!this.checkCombination()) 
				this.CombinationPossible();
			else this.blocksToFall();
		}
		else this.clearSwitch();
	}
}
