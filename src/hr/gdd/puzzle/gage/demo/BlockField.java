package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import android.util.Log;

public class BlockField 
{
	//Fields
	private Block[][] _blocks;
	private CGRect _fieldBounds;
	private Block[] _switching;
	private CCSprite _sprite;
	private CGPoint _dims;
	private Orientation _lastOrientation;
	
	//Constructor
	public BlockField(CGPoint griddims)
	{
		//Set the texture
		this._sprite = CCSprite.sprite("blockfield.png");
		this._sprite.setAnchorPoint(0.0f, 1.0f);
		
		//Last orientation
		_lastOrientation = Orientation.Portrait;
		
		//Initialize the new blocks array
		this._dims = griddims;
		this._blocks = new Block[(int) griddims.x][(int) griddims.y];
	}
	
	//Scale to a fixed width and height, keeping an eye on the dimensions of the field
	public void scaleField(float largestSize, float padding, String orientation)
	{
		float theX = 0.0f;
		float theY = 0.0f;
		
		if(this._dims.x > this._dims.y) 
		{
			theX = largestSize;
			theY = this._dims.y * (largestSize/this._dims.x);
		} 
		else 
		{
			theX = this._dims.x * (largestSize/this._dims.y);
			theY = largestSize;
		}
		
		if(orientation == "portrait")
		{
			float constraint = CCDirector.sharedDirector().displaySize().width - (padding*2);
			if(theX > constraint) theX = constraint;
		} 
		else 
		{
			float constraint = CCDirector.sharedDirector().displaySize().height - (padding*2);
			if(theY > constraint) theY = constraint;
		}
		
		CGSize theSize = CGSize.make(theX, theY);
		
		float newWidth = theSize.width;
	    float newHeight = theSize.height;
	    float startWidth = this._sprite.getContentSize().width;
	    float startHeight = this._sprite.getContentSize().height;
	    float newScaleX = newWidth/startWidth;
	    float newScaleY = newHeight/startHeight;

	    this._sprite.setScaleX(newScaleX);
	    this._sprite.setScaleY(newScaleY);
	    this._sprite.setAnchorPoint(0.0f, 1.0f);
	}
	
	//Obtaining the scaled size of the associated sprite
	public CGPoint getAbsoluteSize()
	{
		return CGPoint.ccp(this._sprite.getScaleX()*this._sprite.getContentSize().width, this._sprite.getScaleY()*this._sprite.getContentSize().height);
	}
	
	//Getter methods
	public CCSprite getSprite()
	{
		return this._sprite;
	}
	
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
	public ArrayList<CCSprite> SetupBlocks(ArrayList<BlockConfig> blocks)
	{
		ArrayList<CCSprite> sprites = new ArrayList<CCSprite>();
		
		for(BlockConfig bc : blocks)
		{
			Block newBlock = bc.obtainBlock();
			CGPoint gridPos = TransformBlockPosition(bc.getPosition());
			
			float newBlockW = (this.getAbsoluteSize().y/this._dims.x);
			float newBlockH = (this.getAbsoluteSize().x/this._dims.y);
			
			//this._sprite.addChild(newBlock.getSprite());
			newBlock.resize(CGSize.make(newBlockW, newBlockH));
			newBlock.getSprite().setPosition(gridPos.x*newBlockW, gridPos.y*newBlockH);
			
			sprites.add(newBlock.getSprite());
		} 
		
		return sprites;
	}
	
	private CGPoint TransformBlockPosition(CGPoint oldPosition)
	{
		if(this._lastOrientation == Orientation.Portrait) return oldPosition;
		
		CGPoint newPosition = CGPoint.zero();
		if(this._lastOrientation == Orientation.IPortrait)
		{
			newPosition = CGPoint.ccp(this._dims.x-oldPosition.x, this._dims.y-oldPosition.y);
		}
		else if(this._lastOrientation == Orientation.Landscape)
		{
			newPosition = CGPoint.ccp(oldPosition.y, this._dims.x-oldPosition.x);
		}
		else if(this._lastOrientation == Orientation.ILandscape)
		{
			newPosition = CGPoint.ccp(this._dims.y-oldPosition.y, oldPosition.x);
		}
		
		return newPosition;
	}
	
	//Based off of the orientation, transform the grid along with the blocks within
	public void TransformGrid(Orientation orientation)
	{
		//Abort if the last orientation equals the current
		if(orientation == this._lastOrientation) return;
		
		int maxhor = (int) _dims.x;
		int maxvert = (int) _dims.y;
		boolean wasFlat = this._lastOrientation == Orientation.Landscape || this._lastOrientation == Orientation.ILandscape;
		
		//Create a temporary array to store the blocks in
		Block[][] tempArray = new Block[wasFlat ? maxvert : maxhor][wasFlat ? maxhor : maxvert];
		
		//Map everything into a new array
		if(this._lastOrientation == Orientation.Portrait)
		{
			for(int i = 0; i <  maxhor; i++) for(int j = 0; j < maxvert; j++)
			{
				tempArray[i][j] = this._blocks[i][j];
			}
		}
		else if(this._lastOrientation == Orientation.IPortrait)
		{
			for(int i = maxhor-1; i >= 0; i--) for(int j = maxvert-1; j >= 0; j--)
			{
				tempArray[maxhor-1-i][maxvert-1-j] = this._blocks[i][j];
			}
		} 
		else if (this._lastOrientation == Orientation.Landscape)
		{
			for(int j = maxvert-1; j >= 0; j--) for(int i = 0; i < maxhor; i++)
			{
				tempArray[maxvert-1-j][i] = this._blocks[i][j];
			}
		}
		else if(this._lastOrientation == Orientation.ILandscape)
		{
			for(int j = 0; j < maxvert; j++) for(int i = maxhor-1; i >= 0; i--)
			{
				tempArray[j][maxhor-1-i] = this._blocks[i][j];
			}
		}
		
		//Reset the dimensions if needed
		if(((this._lastOrientation == Orientation.Portrait || this._lastOrientation == Orientation.IPortrait) && (orientation == Orientation.Landscape || orientation == Orientation.ILandscape)) ||
				((this._lastOrientation == Orientation.Landscape || this._lastOrientation == Orientation.ILandscape) && (orientation == Orientation.Portrait || orientation == Orientation.IPortrait)))
		{
			float newX = this._dims.y;
			float newY = this._dims.x;
			this._dims = CGPoint.ccp(newX, newY);
		}
		
		int newmaxhor = wasFlat ? maxvert : maxhor;
		int newmaxvert = wasFlat ? maxhor : maxvert;
		
		//Create a new blocks array
		Block[][] newBlocksArray = new Block[(int) this._dims.x][(int) this._dims.y];
		
		//Map everything into a new array
		if(orientation == Orientation.Portrait)
		{
			for(int i = 0; i < newmaxhor-1; i++) for(int j = 0; j < newmaxvert-1; j++)
			{
				newBlocksArray[i][j] = tempArray[i][j];
			}
		}
		else if(orientation == Orientation.IPortrait)
		{
			for(int i = newmaxhor-1; i >= 0; i--) for(int j = newmaxvert-1; j >= 0; j--)
			{
				newBlocksArray[newmaxhor-1-i][newmaxvert-1-j] = tempArray[i][j];
			}
		} 
		else if (orientation == Orientation.Landscape)
		{
			for(int j = newmaxvert-1; j >= 0; j--) for(int i = 0; i < newmaxhor; i++)
			{
				newBlocksArray[newmaxvert-1-j][i] = tempArray[i][j];
			}
		}
		else if(orientation == Orientation.ILandscape)
		{
			for(int j = 0; j < newmaxvert; j++) for(int i = newmaxhor-1; i >= 0; i--)
			{
				newBlocksArray[j][newmaxhor-1-i] = tempArray[i][j];
			}
		}
		
		this._blocks = newBlocksArray;
		this._lastOrientation = orientation;  
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
