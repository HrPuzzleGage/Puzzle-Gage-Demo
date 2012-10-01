package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;
import java.util.Observable;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.instant.CCCallFuncND;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
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
		if(this._lastOrientation == orientation) return;
		
		this._lastOrientation = orientation;
		
		//Rotation
		float newRotation = this.orientationToRotation(orientation);
		
		//Iterate over all blocks in the grid, setting new values for them
		for(Block[] b : this._blocks) for(Block bb : b)
		{
			if(bb != null) bb.getSprite().setRotation(newRotation);
		}
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
			if(this._blocks[i][j] != null && checkAddI+i <= (this._dims.x-1) && checkAddI+i >= 0 && checkAddJ+j <= (this._dims.y-1) && checkAddJ+j >= 0)
			{
				if(this._blocks[i+checkAddI][j+checkAddJ] == null)
				{
					Block blockToMove = this._blocks[i][j];
					
					CCMoveTo actionMove = CCMoveTo.action(0.4f, CGPoint.ccp(
						blockToMove.getSprite().getPosition().x+(blockToMove.getActualSize().width*checkAddI), 
						blockToMove.getSprite().getPosition().y-(blockToMove.getActualSize().height*checkAddJ)));
				    CCCallFuncN actionMoveDone = CCCallFuncND.action(this, "blockFallen", blockToMove);
				    CCSequence actions = CCSequence.actions(actionMove, actionMoveDone);
				    
				    blockToMove.setStoredCoord(CGPoint.ccp(i+checkAddI, j+checkAddJ));
				    this._blocks[i][j] = null;
				    
				    blockToMove.getSprite().runAction(actions);
				    this._falling++;
				}
			}
		}
		
		if(this._falling == 0) this.checkCombination();
	}
	
	//Callback method after a block has moved to a new position
	public void blockFallen(Object sender, Object d)
	{
		this._falling--;
		
		//Move the block to its new position in the array
		Block block = (Block)d;
		CGPoint grTarget = block.getStoredCoord(); 
		this._blocks[(int)grTarget.x][(int)grTarget.y] = block;
		
		if(this._falling == 0) this.blocksToFall();
	}
	
	//Remove blocks from the grid
	public void removeBlocks(boolean[][] removeMap)
	{
		for(int i = 0; i < removeMap.length; i++) for(int j = 0; j < removeMap[i].length; j++)
		{
			if(removeMap[i][j] && this._blocks[i][j] != null)
			{ 
				this._blocks[i][j].getSprite().removeFromParentAndCleanup(true);
				this._blocks[i][j] = null;
			}
		}
		
		//Now that all blocks have been removed that should be removed, check if more needs to fall...
		this.blocksToFall();
	}
	
	//Check if there is a combination within the grid
	public void checkCombination()
	{
		//Construct new array containing the blocks that belong to a match
		boolean[][] matchMap = new boolean[(int)this._dims.x][(int)this._dims.y];
		int totalScore = 0;
		int totalCombo = 1;
		
		//MAKE THIS A CONSTANT LATER...
		int defScore = 100;
		int defScoreAdd = 50;
		
		//Search through columns for matches
		for(int i = 0; i < this._blocks.length; i++)
		{
			int found = 0;
			AlienType lastType = AlienType.Alex;
			
			for(int j = 0; j < this._blocks[i].length; j++)
			{
				if(this._blocks[i][j] != null)
				{
					Block currBlock = this._blocks[i][j];
					
					if(found == 0) lastType = currBlock.getType();
					else if(lastType != currBlock.getType())
					{
						found = 0;
						lastType = currBlock.getType();
					}
					
					found++;
					
					if(found == 3)
					{
						matchMap[i][j] = matchMap[i][j-1] = matchMap[i][j-2] = true;
						totalScore += defScore*totalCombo;
						totalCombo++;
					}
					else if(found > 3)
					{
						matchMap[i][j] = true;
						totalScore += defScoreAdd*(totalCombo-1);
					}
				} else found = 0;
			}
		}
		
		//Search through rows for matches
		for(int k = 0; k < this._blocks[0].length; k++)
		{
			int found = 0;
			AlienType lastType = AlienType.Alex;
			
			for(int l = 0; l < this._blocks.length; l++)
			{
				if(this._blocks[l][k] != null)
				{
					Block currBlock = this._blocks[l][k];
					
					if(found == 0) lastType = currBlock.getType();
					else if(lastType != currBlock.getType())
					{
						found = 0;
						lastType = currBlock.getType();
					}
					
					found++;
					
					if(found == 3)
					{
						matchMap[l][k] = matchMap[l-1][k] = matchMap[l-2][k] = true;
						totalScore += defScore*totalCombo;
						totalCombo++;
					}
					else if(found > 3)
					{
						matchMap[l][k] = true;
						totalScore += defScoreAdd*(totalCombo-1);
					}
				} else found = 0;
			}
		}
		
		//Score
		Log.d("TOTAL COMBO", ""+totalCombo);
		Log.d("TOTAL SCORE", ""+totalScore);
		
		if(totalScore == 0)
			this.CombinationPossible();
		else
			this.removeBlocks(matchMap);
	}
	
	//Check if there are still any possible combinations on the grid
	public void CombinationPossible()
	{
		//Notify everything that is observing the block field atm
		this.setChanged();
		this.notifyObservers(new EventData(EventType.DoneChecking));
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
