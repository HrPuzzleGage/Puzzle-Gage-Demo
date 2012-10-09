package hr.gdd.puzzle.gage.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity implements OnClickListener
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button about = (Button) this.findViewById(R.id.about_button);
        about.setOnClickListener(this);
        Button start = (Button) this.findViewById(R.id.start_button);
        start.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) 
    {
        switch (v.getId()) 
        {
        case R.id.about_button:
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
            break;
        case R.id.start_button:
        	Intent b = new Intent(this, WorldSelectActivity.class);
        	startActivity(b);
        	break;
        }
    }
    
    
}