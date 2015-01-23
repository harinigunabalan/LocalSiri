package tud.kom.dss6.localsiri.localservice;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class UploadService {
	
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
			}
			else if (isWiFi == false && mode == 2 ){
				success = fromSQLiteToBluemix(false);
			}
			else if (isWiFi == false && mode == 1 ){
				success = fromSQLiteToBluemix(true);
			}
		}				
		return success;
	}		

	public boolean fromSQLiteToBluemix(boolean optimized3G){
		
		boolean success = false;
		int j;
		j = (optimized3G == true) ? 2:1;				
		
		DBAdapter DB = DBAdapter.getInstance(mContext);		
		ArrayList<HashMap<String, String>> geoArrayList = DB.getSavedGeoPoints();
		if(!geoArrayList.isEmpty()){
			for(int i = 0;i < geoArrayList.size(); i = i+j ){
				HashMap<String, String> hashmap = new HashMap<String, String>(); 
				hashmap = geoArrayList.get(i);
				hashmap.get(LocationMain.KEY_DATE);
				hashmap.get(LocationMain.KEY_LATITUDE);
				hashmap.get(LocationMain.KEY_LONGITUDE);
				// Assign to Position POJO and Add to Mobile Data - code in Local Siri MainLocation
				// Set Success as true on Success and false on failure
			}
		}
		DB.close();
		return success;
	}
}