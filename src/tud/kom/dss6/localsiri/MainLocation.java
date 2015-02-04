package tud.kom.dss6.localsiri;

/**
 * The opportunistic sensing of Location Data is performed. The 
 * location data from GPS/Network Provider is sensed and stored 
 * in SQLite Locally and to the Mobile Data Bluemix Cloud
 *
 * @author Harini Gunabalan	
 * @author Hariharan Gandhi
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tud.kom.dss6.localsiri.IBMDataObjects.CurrentPosition;
import tud.kom.dss6.localsiri.IBMDataObjects.GeoPointLocation;
import tud.kom.dss6.localsiri.IBMDataObjects.Position;
import tud.kom.dss6.localsiri.IBMDataObjects.Topic;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

public class MainLocation extends ListActivity{

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES 	= 1; 	// in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES 			= 1000; // in Milliseconds
    public final static String EXTRA_MESSAGE = "dss6.komlab.tu.localsiri.MainLocation.MSG";

    protected LocationManager locationManager;
    protected Button recordCurrentLocation;
    protected Button displaySavedLocations;
    LocalSiriApplication lsApp;
    DBAdapter DB;

    public static final String KEY_DATE 		= "date";
    public static final String KEY_LATITUDE 	= "latitude";
    public static final String KEY_LONGITUDE 	= "longitude";
    public static final String CLASS_NAME		= "MainLocation"; 
    
    public static final String PREFS_NAME = "MyPrefsFile";
    List<CurrentPosition> currentPositionList;
	CurrentPosition current = new CurrentPosition();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_location);

		/* Use application class to maintain global state. */
		lsApp = (LocalSiriApplication)getApplication();         
        
        recordCurrentLocation = (Button) findViewById(R.id.add_geoPoints);
        displaySavedLocations = (Button) findViewById(R.id.display_geoPint);
        DB = new DBAdapter(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener()
        );     
        
        recordCurrentLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               //addGeoLocation();                  
            }
        });

        displaySavedLocations.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //displayGeoLocation();
            	launchHomeScreen();
            	
            }
        });
              
    }    
    
    protected void addGeoLocation() {

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if(location == null){
        	location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        	Toast.makeText(MainLocation.this, "Using Network Provider to locate(Not GPS)",
                    Toast.LENGTH_SHORT).show();
        }
        
        if (location != null) {

            HashMap<String, String> geoPointsSet = new HashMap<String, String>();

            geoPointsSet.put(KEY_DATE,getDateTime());
            geoPointsSet.put(KEY_LATITUDE, String.valueOf(location.getLatitude()));
            geoPointsSet.put(KEY_LONGITUDE, String.valueOf(location.getLongitude()));

/*
 * Adding to the Local SQLite DB 
 */
            String message = "adding to DB. Latitude: " + String.valueOf(location.getLatitude())+ "\n" +
                    "Longitude: " + String.valueOf(location.getLongitude());

            DB.insertGeoPoints(geoPointsSet);
            Toast.makeText(MainLocation.this, message,
                    Toast.LENGTH_SHORT).show();
/*
 * Adding Position points to Mobile Cloud Data        
 */                   
            
            Position position = new Position();
            position.setDate(getDateTime());

        	/* Set the Device MAC address*/    	
        	WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        	WifiInfo wInfo = wifiManager.getConnectionInfo();
        	String macAddress = wInfo.getMacAddress();
        	position.setMac(macAddress);            
                       
            position.setLatitude(String.valueOf(location.getLatitude()));
            position.setLongitude(String.valueOf(location.getLongitude()));  
            position.save().continueWith(new Continuation<IBMDataObject, Void>() { 
// Probe the strength of the connectivity before cloud upload            		
            	@Override 
            	public Void then(Task<IBMDataObject> task) throws Exception { 
            		// Log if the save was cancelled. 
            		if (task.isCancelled()){ 
            			Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled."); 
            		} 
            		// Log error message, if the save task fails. 
            		else if (task.isFaulted()) { 
            			Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage()); 
            		} 
            		// If the result succeeds,  
            		else { 
            		 
            		} 
            		return null; 
            	} 
            });            		          
            
        }
        else{
            Toast.makeText(MainLocation.this, R.string.noInfo,
                    Toast.LENGTH_SHORT).show();
        }        
        
    }  
    
    protected void displayGeoLocation() {

        ArrayList<HashMap<String, String>> geoPointList = DB.getSavedGeoPoints();
        if(!geoPointList.isEmpty()){
        ListAdapter adapter = new SimpleAdapter(MainLocation.this,
        		geoPointList,
        		R.layout.geo_points_list,
                new String[] {KEY_DATE, KEY_LATITUDE, KEY_LONGITUDE},
                new int[]{R.id.date,R.id.latitude,R.id.longitude});
        
        setListAdapter(adapter);
        }else{
        	Toast.makeText(MainLocation.this, "No Data available to display",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            Toast.makeText(MainLocation.this, "New Location Updated",
                    Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(MainLocation.this, "Provider status changed",
                    Toast.LENGTH_SHORT).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(MainLocation.this,
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(MainLocation.this,
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    protected void launchHomeScreen() {
    	Intent intent = new Intent(this, AskSiri.class);
    	String msg = "Test";
    	intent.putExtra(EXTRA_MESSAGE, msg);
        startActivity(intent);
    }
    
    public void updateCurrentPosition(GeoPointLocation location){    	
    	
   		Log.e(CLASS_NAME, "Entered updateCurrentPosition");
		IBMQuery<CurrentPosition> query;
		String macAddress = new String();
		try {
			query = IBMQuery.queryForClass(CurrentPosition.class);		
		
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	WifiInfo wInfo = wifiManager.getConnectionInfo();
    	macAddress = wInfo.getMacAddress(); 
    	Log.e(CLASS_NAME, "Device MAC: " + macAddress);
    	
    	query.whereKeyEqualsTo("DeviceMAC", macAddress);            	
    	query.find().continueWith(new Continuation<List<CurrentPosition>, Void>() {

			@Override
			public Void then(Task<List<CurrentPosition>> task) throws Exception {
                final List<CurrentPosition> objects = task.getResult();
                 // Log if the find was cancelled.
                if (task.isCancelled()){
                    Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                }
				 // Log error message, if the find task fails.
				else if (task.isFaulted()) {
					Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
				}					
				 // If the result succeeds, load the list.
				else {
					Log.e(CLASS_NAME, "Data Fetched successfully");                                                                        
					currentPositionList.clear();
					for(IBMDataObject currentPosition:objects) {
						current = (CurrentPosition)currentPosition;
                        currentPositionList.add((CurrentPosition) currentPosition);
                        Log.e(CLASS_NAME, "Inside for loop");                     
                    }                      											
				}
				return null;
			}
		},Task.UI_THREAD_EXECUTOR);    	
    	
		} catch (IBMDataException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}    	
    	
    	 // Updating Current Position of the device to Mobile Cloud Data        
    	          if (current != null){ 
    	   
    	        	current.setLatitude(String.valueOf(location.getLatitude()));
      	            current.setLongitude(String.valueOf(location.getLongitude()));     
      	            current.save().continueWith(new Continuation<IBMDataObject, Void>() { 
      	        	// Probe the strength of the connectivity before cloud upload            		
      	        	            	@Override 
      	        	            	public Void then(Task<IBMDataObject> task) throws Exception { 
      	        	            		// Log if the save was cancelled. 
      	        	            		if (task.isCancelled()){ 
      	        	            			Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled."); 
      	        	            		} 
      	        	            		// Log error message, if the save task fails. 
      	        	            		else if (task.isFaulted()) { 
      	        	            			Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage()); 
      	        	            		} 
      	        	            		// If the result succeeds,  
      	        	            		else { 
      	        	            		 
      	        	            		} 
      	        	            		return null; 
      	        	            	} 
      	        	            });    
    	        	  
    	          }   
    	          else {    	        	      	          
// First time updating the location of the Device to the cloud!    	            
    	            CurrentPosition cposition = new CurrentPosition();
    	            cposition.setDate(getDateTime());
    	        	cposition.setMac(macAddress);            
    	                       
    	            cposition.setLatitude(String.valueOf(location.getLatitude()));
    	            cposition.setLongitude(String.valueOf(location.getLongitude()));  
    	            cposition.save().continueWith(new Continuation<IBMDataObject, Void>() { 
    	// Probe the strength of the connectivity before cloud upload            		
    	            	@Override 
    	            	public Void then(Task<IBMDataObject> task) throws Exception { 
    	            		// Log if the save was cancelled. 
    	            		if (task.isCancelled()){ 
    	            			Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled."); 
    	            		} 
    	            		// Log error message, if the save task fails. 
    	            		else if (task.isFaulted()) { 
    	            			Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage()); 
    	            		} 
    	            		// If the result succeeds,  
    	            		else { 
    	            		 
    	            		} 
    	            		return null; 
    	            	} 
    	            }); 
    	            
    	        }
    	
    }
    
}