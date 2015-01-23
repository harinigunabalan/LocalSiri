package tud.kom.dss6.localsiri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import tud.kom.dss6.localsiri.IBMDataObjects.GeoPointLocation;
import tud.kom.dss6.localsiri.IBMDataObjects.Post;
import tud.kom.dss6.localsiri.IBMDataObjects.Topic;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.cloudcode.IBMCloudCode;
import com.ibm.mobile.services.core.http.IBMHttpResponse;
import com.ibm.mobile.services.data.IBMDataObject;

public class askQuery  extends Activity {
	
	public final static String MY_TOPICS 	= "dss6.komlab.tu.localsiri.MainLocation.MSG1";
	private boolean useMyLocationFlag = false;
    protected LocationManager locationManager;
	protected Button ask;
	protected String TopicLat;
	protected String TopicLong;
	public static final String CLASS_NAME = "askQuery";
	public EditText TopicLocation;
	public EditText Topic;
	public EditText PostContent;

	Topic topic = new Topic();
	Post post = new Post();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_query);  
        ask = (Button) findViewById(R.id.ask);
        TopicLocation = (EditText) findViewById(R.id.TopicLocation);
        Topic = (EditText) findViewById(R.id.Topic);
        PostContent = (EditText) findViewById(R.id.TopicContent);
        
        ask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if(TopicLocation.getText().toString().equals("")) {               
                	alertDialog("Please enter Topic Location");
                	return; 
                }
                else if (Topic.getText().toString().equals("")){
                	alertDialog("Please enter Topic Name");
                	return;
                }
                else if (PostContent.getText().toString().equals("")){
                	alertDialog("Please enter the query!");
                	return;
            	}	
            	saveTopic();                  
            }
        });
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);                 
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
    
    protected void saveTopic(){    	

    	/* Set the Device MAC address*/    	
    	WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	WifiInfo wInfo = wifiManager.getConnectionInfo();
    	String macAddress = wInfo.getMacAddress();
    	topic.setMac(macAddress);
    	
    	/* Set the Topic TimeStamp */    	
    	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    	Date date = Calendar.getInstance().getTime();
    	String timeStamp = df.format(date);
    	topic.setTopicTS(timeStamp);
    	
    	/* Set the Latitude and Longitude of the Topic using GeoCoding/MyLocation */
    	if (useMyLocationFlag){     		
    		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);            
            if(location == null){
            	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);            	
            }
            if(location != null){
            	topic.setLatitude(String.valueOf(location.getLatitude()));
            	topic.setLongitude(String.valueOf(location.getLongitude()));
            }
    	}    	
    	else {
    		Log.e(CLASS_NAME, "Using GeoPoint Location!!"); 
    		GeoPointLocation gp = getLocationFromAddress(TopicLocation.getText().toString());
    		if (gp != null){
    			topic.setLatitude(String.valueOf(gp.getLatitude()));
                topic.setLongitude(String.valueOf(gp.getLongitude()));
                Log.e(CLASS_NAME, "From gp: Latitude: " + topic.getLatitude() + "-- longitude: " + topic.getLongitude());
    		}
    		else { 
    			alertDialog("Address coordinates cannot be determined. Please enter a valid Address!");
    			return;
    		}
    	}
    	
    	/* Set the Topic Heading */
    	topic.setTopic(Topic.getText().toString()); 
    	
    	/* Set the Post Content */
    	post.setMac(macAddress);
    	post.setPostContent(PostContent.getText().toString()); 
    	
    	/*
    	 * Adding Topics to Mobile Cloud Data of IBM Bluemix        
    	 */                       	                	                	                	                	            
            topic.save().continueWith(new Continuation<IBMDataObject, Void>() { 
            		
            	@Override 
            	public Void then(Task<IBMDataObject> task) throws Exception { 
            		// Log if the save was cancelled. 
            		if (task.isCancelled()){ 
            			Log.e(CLASS_NAME, "Topic Exception : Task " + task.toString() + " was cancelled."); 
            		} 
            		// Log error message, if the save task fails. 
            		else if (task.isFaulted()) { 
            			Log.e(CLASS_NAME, "Topic Exception : " + task.getError().getMessage()); 
            		} 
            		// If the result succeeds,  
            		else { 
	            		Log.i(CLASS_NAME, "Successfully saved in Mobile Cloud");
	            		//TopicLocation.setText("");
	            		//PostContent.setText("");
	            		Log.i(CLASS_NAME, "Your Question is Posted");	            	
	            		Log.i(CLASS_NAME, topic.getObjectId());
	            		post.setTopic_ID(topic.getObjectId());			            		
	            		post.save().continueWith(new Continuation<IBMDataObject, Void>() { 
	                		
	                    	@Override 
	                    	public Void then(Task<IBMDataObject> task) throws Exception { 
	                    		// Log if the save was cancelled. 
	                    		if (task.isCancelled()){ 
	                    			Log.e(CLASS_NAME, "Post Exception : Task " + task.toString() + " was cancelled."); 
	                    		} 
	                    		// Log error message, if the save task fails. 
	                    		else if (task.isFaulted()) { 
	                    			Log.e(CLASS_NAME, "Post Exception : " + task.getError().getMessage()); 
	                    		} 
	                    		// If the result succeeds,  
	                    		else { 
	        	            		Log.i(CLASS_NAME, " Post Successfully saved in Mobile Cloud");	        	            			        	            		
	        	            		Toast.makeText(askQuery.this, "Your Question is Posted", Toast.LENGTH_SHORT).show();
	        	            		//Push Notification	        	            		
	                    		} 
	                    		return null; 
	                    	} 
	                    });   
	            		// Back into Topic Save
	            		launchMyTopics();	           
	            		updateOtherDevices();
            		} 
            		return null; 
            	} 
            });   
                       
    	
    }
    
    public void onCheckboxClicked(View view) {     	 
        boolean checked = ((CheckBox) view).isChecked();               
        switch(view.getId()) {
            case R.id.UseMyLocation:
                if (checked)
                	useMyLocationFlag = true;
                else
                	useMyLocationFlag = false;
                break;
        }    	
    }
    
    public GeoPointLocation getLocationFromAddress(String strAddress){

    	Geocoder coder = new Geocoder(this);
    	List<Address> address;   
    	Address location;
    	GeoPointLocation l = new GeoPointLocation();
    	
    	    try {
				address = coder.getFromLocationName(strAddress,5);
	    	    location = address.get(0);
	    	    double lat = location.getLatitude();
	    	    double lon = location.getLongitude();
	    	    Log.e(CLASS_NAME, "Latitude: " + lat + "-- longitude: " + lon );
	    	    l.setLatitude(lat);
	    	    l.setLongitude(lon);
	    	    Log.e(CLASS_NAME, "From GeoPoint Location: Latitude: " + l.getLatitude() + "-- longitude: " + l.getLongitude() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    	    	     	        	        	    
    	    return l;
    	          	
    }
    protected void launchMyTopics() {
    	Intent intent = new Intent(this, MyTopics.class);
    	String msg = "Test";
    	intent.putExtra(MY_TOPICS, msg);
        startActivity(intent);
    }
    
    protected void alertDialog(String msg){
    	 AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

         dlgAlert.setMessage(msg);
         dlgAlert.setTitle("Error Message...");
         dlgAlert.setPositiveButton("OK", null);
         dlgAlert.setCancelable(true);
         dlgAlert.create().show();

         dlgAlert.setPositiveButton("Ok",
                 new DialogInterface.OnClickListener() {
             	public void onClick(DialogInterface dialog, int which) {

             	} 
             	} );         
    }  
    
    private void updateOtherDevices() {

		// initialize and retrieve an instance of the IBM CloudCode service
    	Log.e(CLASS_NAME, "Inside updateOtherDevices 1");
		IBMCloudCode.initializeService();
		IBMCloudCode myCloudCodeService = IBMCloudCode.getService();
		JSONObject jsonObj = new JSONObject();
		Log.e(CLASS_NAME, "Inside updateOtherDevices 2");
		try {
			jsonObj.put("key1", "value1");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.e(CLASS_NAME, "Inside updateOtherDevices 3");
		// Call the node.js application hosted in the IBM Cloud Code service
		// with a POST call, passing in a non-essential JSONObject
		// The URI is relative to, appended to, the BlueMix context root
		
		myCloudCodeService.post("notifyOtherDevices", jsonObj).continueWith(new Continuation<IBMHttpResponse, Void>() {
			
            @Override
            public Void then(Task<IBMHttpResponse> task) throws Exception {
                if (task.isCancelled()) {
                    Log.e(CLASS_NAME, "Exception : Task" + task.isCancelled() + "was cancelled.");
                } else if (task.isFaulted()) {
                    Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
                } else {
                    InputStream is = task.getResult().getInputStream();
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(is));
                        String responseString = "";
                        String myString = "";
                        while ((myString = in.readLine()) != null)
                            responseString += myString;

                        in.close();
                        Log.i(CLASS_NAME, "Response Body: " + responseString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.i(CLASS_NAME, "Response Status from notifyOtherDevices: " + task.getResult().getHttpResponseCode());
                }
                return null;
            }
        });
	}	    
}