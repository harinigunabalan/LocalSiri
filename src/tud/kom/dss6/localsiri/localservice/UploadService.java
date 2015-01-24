package tud.kom.dss6.localsiri.localservice;

import java.util.ArrayList;
import java.util.HashMap;

import tud.kom.dss6.localsiri.IBMDataObjects.Position;
import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.data.IBMDataObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class UploadService {
	
	boolean hit = true;
	
	public static final String CLASS_NAME = "tud.kom.dss6.localsiri.localservice.UploadService";
	private Context mContext;
	ConnectivityManager cm;
	NetworkInfo activeNetwork;
	
	UploadService(Context context){
		mContext = context;
		cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);		 
		activeNetwork = cm.getActiveNetworkInfo();
	}
	
	public boolean checkisConnected(){
		
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}
	
	public boolean checkisConnectedToWiFi(){
		
		boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
		return isWiFi;
	}
	
	public boolean uploadOptimizer(int mode){
		// mode: 0-WiFi only, 1-Optimized 3G and 2-Normal 3G				
		boolean success = false;
		boolean isConnected = false, isWiFi = false;
		
		isConnected = checkisConnected();
		if (isConnected == true){
			isWiFi = checkisConnectedToWiFi();
			if(isWiFi == true){
				success = fromSQLiteToBluemix(false);
				updateCurrentPosition();
			}
			else if (isWiFi == false && mode == 2 ){
				success = fromSQLiteToBluemix(false);
				updateCurrentPosition();
			}
			else if (isWiFi == false && mode == 1 ){
				success = fromSQLiteToBluemix(true);
				updateCurrentPosition();
			}
		}				
		return success;
	}		

	private boolean fromSQLiteToBluemix(boolean optimized3G){
				
		int j;
		j = (optimized3G == true) ? 2:1;				
		
		DBAdapter DB = DBAdapter.getInstance(mContext);		
		ArrayList<HashMap<String, String>> geoArrayList = DB.getSavedGeoPoints();
		if(!geoArrayList.isEmpty()){
			for(int i = 0;i < geoArrayList.size(); i = i+j ){
				// TODO: check if the record is after the last_uploaded field in the shared preference.
				HashMap<String, String> hashmap = new HashMap<String, String>(); 
				hashmap = geoArrayList.get(i);	
	/*
	 * Adding Position points to Mobile Cloud Data        
	 */                   	            
	            Position position = new Position();
	            position.setDate(hashmap.get(LocationMain.KEY_DATE));

	        	/* Set the Device MAC address*/    	
	        	WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	        	WifiInfo wInfo = wifiManager.getConnectionInfo();
	        	String macAddress = wInfo.getMacAddress();
	        	position.setMac(macAddress);            
	                       
	            position.setLatitude(hashmap.get(LocationMain.KEY_LATITUDE));
	            position.setLongitude(hashmap.get(LocationMain.KEY_LONGITUDE));  
	            position.save().continueWith(new Continuation<IBMDataObject, Void>() { 
	            // Probe the strength of the connectivity before cloud upload            		
	            	@Override 
	            	public Void then(Task<IBMDataObject> task) throws Exception { 
	            		// Log if the save was cancelled. 
	            		if (task.isCancelled()){ 
	            			Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
	            			hit = false;
	            		} 
	            		// Log error message, if the save task fails. 
	            		else if (task.isFaulted()) { 
	            			Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
	            			hit = false;
	            		} 
	            		// If the result succeeds,  
	            		else { 
	            			
	            		} 
	            		return null; 
	            	} 
	            }); 
				// Set Success as true on Success and false on failure
			}
		}
		DB.close();
		return hit;
	}
}