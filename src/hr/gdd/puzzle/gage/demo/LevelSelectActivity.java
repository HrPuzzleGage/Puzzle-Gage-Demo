package hr.gdd.puzzle.gage.demo;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;


public class LevelSelectActivity extends Activity implements OnClickListener
{
	private int _currWorld = 0;
	
	//Default methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.level_selector);
        
        this._currWorld = this.getIntent().getIntExtra("worldToPlay", 0);
        
        LinearLayout buttonsParent = (LinearLayout)this.findViewById(R.id.ls_container);
        ArrayList<String> levelNames = new ArrayList<String>();
        
        try 
        {
        	XmlParser i = XmlParser.instance();
        	i.setDocumentFromAssets(this, "practiceXML");
			levelNames = i.getLevelNamesByWorld(this._currWorld);
		} 
        catch (IOException e) 
		{
			e.printStackTrace();
		} 
        catch (ParserConfigurationException e) 
        {
			e.printStackTrace();
		} 
        catch (SAXException e) 
        {
			e.printStackTrace();
		}
        
        for(int i = 0; i < levelNames.size(); i++)
        {
        	Button newButton = new Button(this);
        	newButton.setText("Level "+(i+1)+": "+levelNames.get(i));
        	newButton.setId(i);
        	
        	buttonsParent.addView(newButton);
        	newButton.setOnClickListener(this);
        }
    }

	@Override
	public void onClick(View v) {
		int newId = v.getId();
		
		Intent i = new Intent(this, Game.class);
		i.putExtra("worldToPlay", this._currWorld);
		i.putExtra("levelToPlay", newId);
		
		startActivity(i);
	}
}
