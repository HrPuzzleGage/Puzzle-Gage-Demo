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

public class BlockField extends Observable
{
	//-----------------Fields
	private Block[][] _blocks;
	private Block[] _switching;
	private CCSprite _sprite;
	private CGSize _size;
	private CGPoint _dims;
	private Orientation _lastOrientation;
	private int _falling = 0;
	private SwitchState _switchState = SwitchState.None;
	
	//-----------------Constructor
	public BlockField(CGPoint griddims)
	{
		//Set the texture to be used for the sprite
		this._sprite = CCSprite.sprite("blockfield.png");
		
		//Last orientation
		this._lastOrientation = Orientation.Portrait;
		
		//Initialize the new blocks array based on the supplied dimensions
		this._dims = griddims;
		this._blocks = new Block[(int) griddims.x][(int) griddims.y];
		
		//Blocks to switch
		this._switching = new Block[2];
	}
	
	//-----------------Getter Methods
	//Get the dimensions for the block field (how many by how many blocks there are)
	public CGPoint getDimensions()
	{
		return this._dims;
	}
	
	//Obtain the scaled size of the associated sprite (this excludes any rotations)
	public CGSize getAbsoluteSpriteSize()
	{
		return this._size;
	}
	
	//Obtain the sprite used as a background for the block field
	public CCSprite getSprite()
	{
		return this._sprite;
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
	public void setOrientation(Orientation orientation)
	{
		if(this._lastOrientation == orientation || this._switchState != SwitchState.None) return;
		this._lastOrientation = orientation;
		
		//Rotation of all sprites in the grid
		float newRotation = this.orientationToRotation(orientation);
		for(CCSprite s : this.getBlockSprites()) s.setRotation(newRotation);
	}
	
	//-----------------Methods
	//Scale the sprite to a fixed width and height, keeping an eye on the dimensions of the field
	//Remember that if the aspect ratio is the opposite of that of the desired size, the sprite is rotated!
	public void resizeIndividualAxes(CGSize newSize, CGSize currSize, CGPoint constraint)
	{
		//Decide longest and shortest axis of both passed size and constraint
		float longestPassed = Math.max(newSize.width, newSize.height);
		float shortestPassed = Math.min(newSize.width, newSize.height);
		float longestDims = Math.max(constraint.x, constraint.y);
		float shortestDims = Math.min(constraint.x, constraint.y);
		
		//Set the shortest side taking the proportions into account 
		float longSteps = longestPassed/longestDims;
		float finalLong = longestPassed;
		float finalShort = longSteps * shortestDims;
		
		//Scale the whole result if the shortest side exceeds the newly set size
		if(finalShort > shortestPassed)
		{
			float scaleFactor = shortestPassed/finalShort;
			finalLong *= scaleFactor;
			finalShort *= scaleFactor;
		}
		
		//Scale the sprite
		float newX = currSize.width <= currSize.height ? finalShort : finalLong;
		float newY = currSize.height < currSize.width ? finalShort : finalLong;
		float spriteScaleX = newX/currSize.width;
		float spriteScaleY = newY/currSize.height;
		
		this._sprite.setScaleX(spriteScaleX);
	    this._sprite.setScaleY(spriteScaleY);
	    
		//Calculate aspect ratios and rotate the sprite based on a comparison between them
		boolean currAR = currSize.height >= currSize.width;
		boolean newAR = newSize.height >= newSize.width;
		this._size = CGSize.make(spriteScaleX*currSize.width, spriteScaleY*currSize.height);
		
		if(currAR != newAR)
		{
			this._sprite.setRotation(90.0f);
			this._size.width = spriteScaleY*currSize.height;
			this._size.height = spriteScaleX*currSize.width;
		}
	}
		
	//Return a list of sprites based on the current 
	public void SetupBlocks(ArrayList<BlockConfig> blocks)
	{
		float longSideDims = Math.max(this._dims.x, this._dims.y);
		float shortSideDims = Math.min(this._dims.x, this._dims.y);
		float longSideSize = Math.max(this._size.width, this._size.height);
		float shortSideSize = Math.min(this._size.width, this._size.height);
		
		//Set the scale factor of the newly created blocks
		float newBlockW = shortSideSize/shortSideDims;
		float newBlockH = longSideSize/longSideDims;
		CGSize newBlockSize = CGSize.make(newBlockW, newBlockH);
		
		//Set the rotation to use for the blocks
		float newRotation = this.orientationToRotation(this._lastOrientation);
		
		//Iterate over the supplied block configurators
		for(BlockConfig bc : blocks)
		{
			//Convert the block configurator into a new Block instancend where it should be placed in the grid
			Block newBlock = bc.obtainBlock();
			CGPoint gridPos = bc.getPosition();
			
			//Exit the current iteration once it becomes clear the block is outside the virtual grid...
			if(gridPos.x < 0 || gridPos.x > this._dims.x-1 || gridPos.y < 0 || gridPos.y > this._dims.y-1) continue;
			
			//Very very complex calculation for setting the blocks' position (Portrait). 
			float newPositionX = (newBlockW/2)+(gridPos.x*newBlockW) + (this._sprite.getPosition().x-(this._size.width/2));
			float newPositionY = (-newBlockH/2)+(-gridPos.y*newBlockH) + (this._sprite.getPosition().y+(this._size.height/2));
			
			//Resize and reposition the newly created block
			newBlock.resize(newBlockSize);
			newBlock.getSprite().setPosition(newPositionX, newPositionY);
			newBlock.getSprite().setRotation(newRotation);
			
			//Add the block to the grid
			this._blocks[(int)gridPos.x][(int)gridPos.y] = newBlock;
		}
	}
	
	//Calculate a rotation according to an orientation enumeration value
	public float orientationToRotation(Orientation orientation)
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
	
	//Decide what blocks need to fall
	public void blocksToFall()
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
				CCMoveTo actionMove = CCMoveTo.action(0.4f, CGPoint.ccp(
					blockToMove.getSprite().getPosition().x+(blockToMove.getActualSize().width*checkAddI), 
					blockToMove.getSprite().getPosition().y-(blockToMove.getActualSize().height*checkAddJ)));
			    CCCallFuncN actionMoveDone = CCCallFuncND.action(this, "blockFallen", blockToMove);
			    CCSequence actions = CCSequence.actions(actionMove, actionMoveDone);
			    
			    //Store coordinates in the block to move, to be read later on
			    blockToMove.setStoredCoord(CGPoint.ccp(i+checkAddI, j+checkAddJ));
			    
			    //Remove the block from the blocks grid
			    this._blocks[i][j] = null;
			    
			    //Execute the action and make sure the Block field knows another block is falling
			    blockToMove.getSprite().runAction(actions);
			    this._falling++;
			}
		}
		
		//If it is concluded after the calculation that no blocks are falling, then check for a combination
		//Otherwise, ensure that any potential switches (blocks selected for it) are cleaned up
		if(this._falling == 0) 
			this.checkCombination();
		else this.clearSwitch();
	}
	
	//Callback method after a block has moved to a new position
	public void blockFallen(Object sender, Object d)
	{
		//Tell the Block Field that the block has fallen the space it needed to fall
		this._falling--;
		
		//Move the block to its new position in the array and set that position's value in the array to the block
		Block block = (Block)d;
		CGPoint grTarget = block.getStoredCoord(); 
		this._blocks[(int)grTarget.x][(int)grTarget.y] = block;
		
		//If this is zero, it means all blocks that were required to fall have fallen. Check again to see what blocks have to fall now
		if(this._falling == 0) this.blocksToFall();
	}
	
	//Remove blocks from the blocks grid using a boolean map
	public void removeBlocks(boolean[][] removeMap)
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
		
		//Now that all blocks have been removed that should have been removed, check if more need to fall
		this.blocksToFall();
	}
	
	//Check if there is a combination within the grid
	public boolean checkCombination()
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
			AlienType lastType = AlienType.Alex;
			
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
			AlienType lastType = AlienType.Alex;
			
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
		
		//Score
		Log.d("TOTAL COMBO", ""+totalCombo);
		Log.d("TOTAL SCORE", ""+totalScore);
		
		//If no matches were found, check the block field to see if combinations are possible to begin with
		if(totalCombo == 0) 
			this.CombinationPossible();
		else
		{
			//Remove the found blocks and return that a combination was found by the method
			this.removeBlocks(matchMap);
			return true;
		}
		
		return false;
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
		if(this._switching[0] == null || dir == SwipeDirection.None || this._switchState != SwitchState.None) return false;
		
		//Get the first block and of the switch as well as the coordinates that were stored into it
		CGPoint firstBlock = this._switching[0].getStoredCoord();
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
			Block secondBlock = this._blocks[(int)firstBlock.x+posToCheckX][(int)firstBlock.y+posToCheckY];
			secondBlock.setStoredCoord(CGPoint.ccp(firstBlock.x+posToCheckX, firstBlock.y+posToCheckY));
			
			//Start a switch between to blocks now that two blocks were found; the method will return true indicating to the caller a switch is set up to start
			this.StartSwitch(this._switching[0], secondBlock);
			return true;
		}  
		
		//No switch takes place; notify the caller of that
		return false;
	}
	
	//Start the switching of two blocks
	public void StartSwitch(Block bl1, Block bl2)
	{
		//HUGE CHUNK OF CODE FOR EXTREMELY COMPLICATED SWITCH!!
		this._switching[0] = bl1;
		this._switching[1] = bl2;
		
		//Get the position of the two block sprites
		float firstX = bl1.getSprite().getPosition().x;
		float secondX = bl2.getSprite().getPosition().x;
		float firstY = bl1.getSprite().getPosition().y;
		float secondY = bl2.getSprite().getPosition().y;
		
		//Create move actions, supplying one with the callback method (only one is needed)
		CCMoveTo actionMove1 = CCMoveTo.action(0.4f, CGPoint.ccp(secondX, secondY));
		CCMoveTo actionMove2 = CCMoveTo.action(0.4f, CGPoint.ccp(firstX, firstY));
	    CCCallFuncN actionMoveDone = CCCallFuncN.action(this, "blocksSwitched");
	    CCSequence actions = CCSequence.actions(actionMove1, actionMoveDone);
	    
	    //Set the current positions of the blocks to null
	    this._blocks[(int) bl1.getStoredCoord().x][(int) bl1.getStoredCoord().y] = null;
	    this._blocks[(int) bl2.getStoredCoord().x][(int) bl2.getStoredCoord().y] = null;
	    
	    //Swap the two blocks' stored coordinates
	    CGPoint stored = bl1.getStoredCoord();
	    bl1.setStoredCoord(bl2.getStoredCoord());
	    bl2.setStoredCoord(stored);
	    
	    //Here states of the blockfield will be set
	    if(this._switchState == SwitchState.Switching) this._switchState = SwitchState.ISwitching; 
	    else this._switchState = SwitchState.Switching;
	    
	    //Run the move actions to switch the blocks
	    bl1.getSprite().runAction(actions);
	    bl2.getSprite().runAction(actionMove2);
	}
	
	//Callback method after a block has moved to a new position
	public void blocksSwitched(Object sender)
	{
		//Set the position in the grid, according to the switch array's stored coordinates
		this._blocks[(int) this._switching[0].getStoredCoord().x][(int) this._switching[0].getStoredCoord().y] = this._switching[0];
		this._blocks[(int) this._switching[1].getStoredCoord().x][(int) this._switching[1].getStoredCoord().y] = this._switching[1];
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
			else this.StartSwitch(this._switching[1], this._switching[0]);
		} 
		else
		{
			this._switchState = SwitchState.None;
			this.clearSwitch();
			this.blocksToFall();
		}
		this.clearChanged();
	}
	
	//Selects the grid position of first encountered block that intersects with the given location
	public void selectBlockAt(CGPoint pos)
	{
		pos.y = CCDirector.sharedDirector().displaySize().height-pos.y;
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
					//Select the block and save it to the switching array
					b.select();
					b.setStoredCoord(CGPoint.ccp(i, j));
					this._switching[0] = b;
					
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
		if(this._switchState != SwitchState.None) return;
		
		//Iterate over the switch array
		for(int s = 0; s < this._switching.length; s++)
		{
			//If the entry exists, deselect it and set its reference from the array to null
			if(this._switching[s] != null)
			{
				this._switching[s].unselect();
				this._switching[s] = null;
			}
		}
	}

	//Game loop
	public void GameLoop(float dt)
	{
		//May not be needed
	}
}
