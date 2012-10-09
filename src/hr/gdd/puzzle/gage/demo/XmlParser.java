package hr.gdd.puzzle.gage.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cocos2d.types.CGPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

public class XmlParser 
{
	private static XmlParser _parser;
	
	private Document _doc = null;
	private String _lastName = "";
	private int _lastPar = 0;
	
	
	/**
	 * Obtain the instance of the XmlParser class, creating one if it does not yet exist.
	 * 
	 * @return the XmlParser instance
	 */
	public static XmlParser instance()
	{
		if(_parser == null) _parser = new XmlParser();
		
		return _parser;
	}
	
	
	/**
	 * 
	 * XmlParser cannot be instantiated through the new keyword.
	 */
	private XmlParser() { }
	
	
	/**
	 * 
	 * Set the (new) Document instance to be used by the XmlParser instance. It uses the Android assets folder.
	 * 
	 * @param c the context used to resolve the assets folder.
	 * @param fileName the name of the XML file to resolve from the Assets folder, excluding its extension
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void setDocumentFromAssets(Context c, String fileName) throws IOException, ParserConfigurationException, SAXException
	{
		InputStream is = c.getAssets().open(fileName+".xml");
		
		//Set the current Document instance for the XmlParser through the DocumentBuilderFactory instance
		DocumentBuilderFactory docBuilderFactory=DocumentBuilderFactory.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		this._doc = docBuilder.parse(is, null);
	}
	
	
	/**
	 * Get a list of all the World names present in the current XML file.
	 * 
	 * Note: A document needs to have been set for the XmlParser instance in order to be used. It will just return an empty list otherwise.
	 * 
	 * @return A list containing all the world names that have been found.
	 */
	public ArrayList<String> getWorldNames()
	{
		//Return an empty list if no document has been set (the code should not perform operations on the document otherwise)
		ArrayList<String> worldNames = new ArrayList<String>();
		
		if(this._doc == null) 
		{
			return worldNames;
		}
		
		//Get the root node of the document instance and ensure it has the required name
		Node root = this._doc.getFirstChild();
		
		if(root.getNodeName().equals(XmlNode.ROOTNODE.toString()))
		{
			//Iterate over all child nodes contained within the root element 
			NodeList worlds = root.getChildNodes();
			
			for(int i = 0; i < worlds.getLength(); i++)
			{
				//The loop should jump to the next iteration if the current node is a blank text node or the name does not match that of a world node.
				Node worldNode = worlds.item(i);
				if(worldNode.getNodeType() == Node.TEXT_NODE || !worldNode.getNodeName().equals(XmlNode.WORLDNODE.toString())) continue;
				
				//Find the first occurrence of a world name node, appending it to the world names list if it was found
				Node nameNode = this.findFirstElement(worldNode.getChildNodes(), XmlNode.WORLDNAME);
				if(nameNode != null) worldNames.add(nameNode.getTextContent());
			}
		}
		
		//Return the world names list
		return worldNames;
	}
	
	
	/**
	 * Get a list of all level names present in the designated world index.
	 * Note: A document needs to have been set for the XmlParser instance in order to be used. It will just return an empty list otherwise.
	 * 
	 * @param world the index that the world node needs to be in relation to its parent node in order to be targeted.
	 * @return A list containing all the level names that have been found.
	 */
	public ArrayList<String> getLevelNamesByWorld(int world)
	{
		ArrayList<String> names = new ArrayList<String>();
		
		if(this._doc == null) 
		{
			return names;
		}
		
		//Get the root node of the document instance and ensure it has the required name
		Node root = this._doc.getFirstChild();
		
		if(root.getNodeName().equals(XmlNode.ROOTNODE.toString()))
		{
			//Find the occurrence of a node corresponding the name of a world node within the root node's children.
			Node worldNode = this.findElemByNamePosition(world, XmlNode.WORLDNODE, root.getChildNodes());
			
			if(worldNode != null)
			{
				//Iterate over all child nodes contained within the targeted world node.
				NodeList levels = worldNode.getChildNodes();
				
				for(int i = 0; i < levels.getLength(); i++)
				{
					//The loop should jump to the next item if a blank text node is encountered or a node that is not a level node.
					Node levelNode = levels.item(i);
					if(levelNode.getNodeType() == Node.TEXT_NODE || !levelNode.getNodeName().equals(XmlNode.LEVELNODE.toString())) continue;
					
					//Find the first occurrence of a level name node, appending it to the level names list if it has been found.
					Node levelNameNode = this.findFirstElement(levelNode.getChildNodes(), XmlNode.LEVELNAMENODE);
					if(levelNameNode != null)names.add(levelNameNode.getTextContent());
				}
			}
		}
		
		//Return the level names list
		return names;
	}
	
	
	/**
	 * Obtain the last level name that was found, assuming a function was used before to find a single level.
	 * 
	 * @return a String containing the last level name.
	 */
	public String getLastLevelName()
	{
		return this._lastName;
	}
	
	
	/**
	 * Obtain the last level par (number of allowed moves) that was found, assuming a function was used before to find a single level.
	 * 
	 * @return an integer containing the last level par.
	 */
	public int getLastLevelPar()
	{
		return this._lastPar;
	}
	
	
	/**
	 * Find a level by index within the given world index, parsing its data into a list of BlockConfig instances if it was found. This will also obtain that level's name and par.
	 * Note: A document needs to have been set for the XmlParser instance in order to be used. It will just return an empty list otherwise.
	 * 
	 * @param world the index that a world node needs to be in relation to its parent node in order to be targeted.
	 * @param level the index that a level node needs to be in relation to the world node if that one has been found.
	 * @return An ArrayList filled with all BlockConfig instances found in the designated level node.
	 */
	public ArrayList<BlockConfig> getLevelBlockDataAt(int world, int level)
	{
		ArrayList<BlockConfig> blocks = new ArrayList<BlockConfig>();
		
		if(this._doc == null)
		{
			return blocks;
		}
		
		//Get the root node of the document instance and ensure it has the required name
		Node root = this._doc.getFirstChild();
		
		if(root.getNodeName().equals(XmlNode.ROOTNODE.toString()))
		{
			//Find the occurrence of a node (matching the name of a world node) within the root node's children.
			Node worldNode = this.findElemByNamePosition(world, XmlNode.WORLDNODE, root.getChildNodes());
			
			if(worldNode != null)
			{
				//If the world node exists, find the occurrence of a node (matching the name of a level node) within its child nodes.
				Node levelNode = this.findElemByNamePosition(level, XmlNode.LEVELNODE, worldNode.getChildNodes());
				
				if(levelNode != null)
				{
					//When found, handle the level node using the getDataFromLevelNode function. 
					blocks = this.getDataFromLevelNode(levelNode);
				}
			}
		}
		
		//Return the block configurators list.
		return blocks;
	}
	
	
	/**
	 * Find the first occurrence of a node with the provided node that is inside the provided context.
	 * 
	 * @param nl A Node list containing the nodes to be searched.
	 * @param node A node name as it occurs in the XmlNode enumeration.
	 * @return The node that was found. Returns null if it does not exist in the current context.
	 */
	private Node findFirstElement(NodeList nl, XmlNode node)
	{
		//Iterate over the provided node list
		for(int i = 0; i < nl.getLength(); i++)
		{
			Node currNode = nl.item(i);
			
			//Continue on to the next iteration if the node in question is a blank text node
			if(currNode.getNodeType() == Node.TEXT_NODE) continue;
			
			//If the name of the currently targeted node equals that of the name to search for, return it (aborting the for loop in the process)
			if(currNode.getNodeName().equals(node.toString()))
			{
				return nl.item(i);
			}
		}
		
		//Return null if the for loop ended and, therefore, no instance was returned.
		return null;
	}
	
	
	/**
	 * Find an element of a given name that occurs at a specific index in relation to its parent node.
	 * 
	 * @param pos The position the node should be in relation to its parent node.
	 * @param node A node name as it occurs in the XmlNode enumeration.
	 * @param nl A Node list containing the nodes to be searched.
	 * @return The node that was found. Returns null if it does not exist in the current context.
	 */
	private Node findElemByNamePosition(int pos, XmlNode node, NodeList nl)
	{
		int currPos = 0;
		
		//Iterate over the provided node list
		for(int i = 0; i < nl.getLength(); i++)
		{
			Node currNode = nl.item(i);
			
			//Continue on to the next iteration if the node in question is a blank text node
			if(currNode.getNodeType() == Node.TEXT_NODE) continue;
			
			//If the current node's name matches that of the name that should be searched on, check the current index. If it matches the expected index, return the node and otherwise increase index by one.
			if(currNode.getNodeName().equals(node.toString()))
			{
				if(currPos == pos) return nl.item(i);
				
				currPos++;
			}
		}
		
		//Return null if the for loop ended and, therefore, no instance was returned.
		return null;
	}
	
	
	/**
	 * Parse an ArrayList from a level that contains block configurator instances.
	 * 
	 * @param the level node instance to scan.
	 * @return an Arraylist instance containing the Level's block configurators.
	 */
	private ArrayList<BlockConfig> getDataFromLevelNode(Node levelNode)
	{
		ArrayList<BlockConfig> blocks = new ArrayList<BlockConfig>();
		
		if(!levelNode.getNodeName().equals(XmlNode.LEVELNODE.toString())) 
		{
			return blocks;
		}
		
		//Instantiate a new block factory in order to create the block instances, then obtain the level Node's child nodes.
		IBlockFactory fac = new AlienBlockFactory();
		NodeList ll = levelNode.getChildNodes();
		
		//Iterate over the level's child nodes
		for(int i = 0; i < ll.getLength(); i++)
		{
			Node blockNode = ll.item(i);
			
			//If the node's name matches that of a level name or level par node, set these as the last obtained level/ par of the instance to be obtained later
			if(blockNode.getNodeName().equals(XmlNode.LEVELNAMENODE.toString())) 
			{
				this._lastName = blockNode.getTextContent();
			}
			else if(blockNode.getNodeName().equals(XmlNode.LEVELPARNODE.toString())) 
			{
				this._lastPar = Integer.parseInt(blockNode.getTextContent());
			}
			
			//Skip to the next iteration if the node in question is a blank text node or has a name that is not the name of a block node
			if(blockNode.getNodeType() == Node.TEXT_NODE || !blockNode.getNodeName().equals(XmlNode.BLOCKNODE.toString())) continue;
			
			//Values to put into a newly created Block configurator later on
			Integer x = null;
			Integer y = null;
			BlockType type = null;
			
			//Iterate over the block node's child nodes
			NodeList blockParams = blockNode.getChildNodes();
			
			for(int j=0; j < blockParams.getLength(); j++)
			{
				Node paramNode = blockParams.item(j);
				
				//Skip to the next iteration if the current node is a blank text node
				if(blockNode.getNodeType() == Node.TEXT_NODE) continue;
				
				String nodeName = paramNode.getNodeName();
				
				//Set the earlier declared values to a value depending on what nodes have been encountered
				try
				{
					if(nodeName.equals(XmlNode.BLOCKHNODE.toString()))
					{
						x = Integer.parseInt(paramNode.getTextContent());
					}
					else if(nodeName.equals(XmlNode.BLOCKVNODE.toString()))
					{
						y = Integer.parseInt(paramNode.getTextContent());
					}
					else if(nodeName.equals(XmlNode.BLOCKTYPENODE.toString()) && BlockType.blockInEnum(paramNode.getTextContent()))
					{
						type = BlockType.valueOf(paramNode.getTextContent());
					}
				}
				catch(NumberFormatException e)
				{
					Log.d("EXCEPTION!", "NUMBER FORMAT EXCEPTION!!");
				}
			}
			
			//Check if none of the values is null. If not, continue on to the next iteration
			if(x == null || y == null || type == null) continue;
			
			//Add a new block configurator to the list
			blocks.add(new BlockConfig(CGPoint.ccp(x, y), type, fac));
		}
		
		//Return the resulting list of blocks
		return blocks;
	}
}
