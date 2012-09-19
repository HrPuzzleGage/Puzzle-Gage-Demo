package hr.gdd.puzzle.gage.demo;

import java.util.ArrayList;
import org.cocos2d.nodes.CCSprite;

public class PGDisplay extends CCSprite 
{
	//Fields
	private ArrayList<PGButton> _buttons;
	
	//Constructor
	public PGDisplay(ArrayList<PGButton> buttons)
	{
		this._buttons = buttons;
	}
	
	//Add a button to the display
	public void AddButton(PGButton button)
	{
		this._buttons.add(button);
	}
	
	//Clear all buttons
	public void ClearButtons()
	{
		this._buttons.clear();
	}
}
