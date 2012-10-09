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


public class WorldSelectActivity extends Activity implements OnClickListener
{
	//Default methods
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.level_selector);
        
        LinearLayout buttonsParent = (LinearLayout)this.findViewById(R.id.ls_container);
        ArrayList<String> worldNames = new ArrayList<String>();
        
        try 
        {
        	XmlParser i = XmlParser.instance();
        	i.setDocumentFromAssets(this, "practiceXML");
        	worldNames = i.getWorldNames();
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
        
        for(int i = 0; i < worldNames.size(); i++)
        {
        	Button newButton = new Button(this);
        	newButton.setText(worldNames.get(i));
        	newButton.setId(i);
        	
        	buttonsParent.addView(newButton);
        	newButton.setOnClickListener(this);
        }
    }

	@Override
	public void onClick(View v) {
		int newId = v.getId();
		
		Intent i = new Intent(this, LevelSelectActivity.class);
		i.putExtra("worldToPlay", newId);
		
		startActivity(i);
	}
}
