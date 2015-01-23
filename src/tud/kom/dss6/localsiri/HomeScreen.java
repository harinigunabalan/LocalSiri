package tud.kom.dss6.localsiri;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;


public class HomeScreen extends Activity {

	public final static String MY_TOPICS 	= "dss6.komlab.tu.localsiri.MainLocation.MSG1";
	public final static String REL_TOPICS 	= "dss6.komlab.tu.localsiri.MainLocation.MSG2";
	public final static String QUERY 		= "dss6.komlab.tu.localsiri.MainLocation.MSG3";
	public final static String CITY 		= "dss6.komlab.tu.localsiri.MainLocation.MSG4";
	
    protected Button myTopics;
    protected Button relTopics;
    protected Button query;
    protected Button city;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);  
        
        myTopics 	= (Button) findViewById(R.id.MyTopics);
        relTopics 	= (Button) findViewById(R.id.RelTopics);
        query 		= (Button) findViewById(R.id.Query);
        city 		= (Button) findViewById(R.id.City);
        
        myTopics.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {            
            	launchMyTopics();
            }
        });
        
        relTopics.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {            
            	launchRelTopics();
            }
        });        
    
        query.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {            
            	launchQuery();
            }
        });
        
        city.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {            
            	launchCity();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    protected void launchMyTopics() {
    	Intent intent = new Intent(this, MyTopics.class);
    	String msg = "Test";
    	intent.putExtra(MY_TOPICS, msg);
        startActivity(intent);
    }
    
    protected void launchRelTopics() {
    	Intent intent = new Intent(this, RelTopics.class);
    	String msg = "Test";
    	intent.putExtra(REL_TOPICS, msg);
        startActivity(intent);
    }
    
    protected void launchQuery() {
    	Intent intent = new Intent(this, askQuery.class);
    	String msg = "Test";
    	intent.putExtra(QUERY, msg);
        startActivity(intent);
    }
    
    protected void launchCity() {
    	Intent intent = new Intent(this, viewCity.class);
    	String msg = "Test";
    	intent.putExtra(CITY, msg);
        startActivity(intent);
    }    
}
