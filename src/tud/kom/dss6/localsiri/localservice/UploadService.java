package tud.kom.dss6.localsiri.localservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tud.kom.dss6.localsiri.LocalSiriApplication;
import tud.kom.dss6.localsiri.IBMDataObjects.CurrentPosition;
import tud.kom.dss6.localsiri.IBMDataObjects.Position;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.data.IBMDataException;
import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

public class UploadService {

	boolean hit = true;

	public static final String CLASS_NAME = "tud.kom.dss6.localsiri.localservice.UploadService";
	private Context mContext;
	ConnectivityManager cm;
	NetworkInfo activeNetwork;
	List<CurrentPosition> mCPList;
	CurrentPosition current = null;

	UploadService(Context context, List<CurrentPosition> currentPositionList) {
		mContext = context;
		mCPList = currentPositionList;
		cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		activeNetwork = cm.getActiveNetworkInfo();
	}

	public boolean checkisConnected() {

		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}

	public boolean checkisConnectedToWiFi() {

		boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
		return isWiFi;
	}

	public boolean uploadOptimizer(int mode) {
		// mode: 0-WiFi only, 1-Optimized 3G and 2-Normal 3G
		boolean success = false;
		boolean isConnected = false, isWiFi = false;

		isConnected = checkisConnected();
		if (isConnected == true) {
			isWiFi = checkisConnectedToWiFi();
			if (isWiFi == true) {
				success = fromSQLiteToBluemix(false);
				updateCurrentPosition();
			} else if (isWiFi == false && mode == 2) {
				success = fromSQLiteToBluemix(false);
				updateCurrentPosition();
			} else if (isWiFi == false && mode == 1) {
				success = fromSQLiteToBluemix(true);
				updateCurrentPosition();
			}
		}
		return success;
	}

	private boolean fromSQLiteToBluemix(boolean optimized3G) {

		int j;
		j = (optimized3G == true) ? 2 : 1;

		DBAdapter DB = DBAdapter.getInstance(mContext);
		ArrayList<HashMap<String, String>> geoArrayList = DB
				.getSavedGeoPoints();
		if (!geoArrayList.isEmpty()) {
			for (int i = 0; i < geoArrayList.size(); i = i + j) {
				// TODO: check if the record is after the last_uploaded field in
				// the shared preference.
				HashMap<String, String> hashmap = new HashMap<String, String>();
				hashmap = geoArrayList.get(i);
				uploadPositionToMobileCloud(hashmap);
				// Set Success as true on Success and false on failure
			}
		}
		DB.close();
		return hit;
	}

	private void uploadPositionToMobileCloud(HashMap<String, String> hashmap) {
		/*
		 * Adding Position points to Mobile Cloud Data
		 */
		Position position = new Position();
		position.setDate(hashmap.get(LocationMain.KEY_DATE));

		String macAddress = returnMacAddress();
		
		position.setMac(macAddress);
		position.setLatitude(hashmap.get(LocationMain.KEY_LATITUDE));
		position.setLongitude(hashmap.get(LocationMain.KEY_LONGITUDE));
		position.save().continueWith(new Continuation<IBMDataObject, Void>() {			
			@Override
			public Void then(Task<IBMDataObject> task) throws Exception {
				// Log if the save was cancelled.
				if (task.isCancelled()) {
					Log.e(CLASS_NAME, "Exception : Task " + task.toString()
							+ " was cancelled.");
					hit = false;
				}
				// Log error message, if the save task fails.
				else if (task.isFaulted()) {
					Log.e(CLASS_NAME, "Exception : "
							+ task.getError().getMessage());
					hit = false;
				}
				// If the result succeeds,
				else {

				}
				return null;
			}
		});
	}

	private void uploadPositionToMobileCloud(CurrentPosition current) {
		current.save().continueWith(new Continuation<IBMDataObject, Void>() {			
			@Override
			public Void then(Task<IBMDataObject> task) throws Exception {
				// Log if the save was cancelled.
				if (task.isCancelled()) {
					Log.e(CLASS_NAME, "Exception : Task " + task.toString()
							+ " was cancelled.");
				}
				// Log error message, if the save task fails.
				else if (task.isFaulted()) {
					Log.e(CLASS_NAME, "Exception : "
							+ task.getError().getMessage());
				}
				// If the result succeeds,
				else {
					SharedPreferences sharedpreferences = mContext.getSharedPreferences(LocalSiriApplication.PREFERENCES, 0);    	
				    Editor editor = sharedpreferences.edit();
				    editor.putBoolean(LocalSiriApplication.CURRENT_LOCATION, true);
				    editor.commit(); 
				}
				return null;
			}
		});
	}

	private void updateCurrentPosition() {		
		Log.e(CLASS_NAME, "Entered updateCurrentPosition");
		
		boolean notFirstUpdate = isCurrentLocationUpdated();
		
		if(!notFirstUpdate){
			updateOrAddCurrentPosition();
			return;
		}

		IBMQuery<CurrentPosition> query;
		String macAddress = returnMacAddress();
		try {
			query = IBMQuery.queryForClass(CurrentPosition.class);			
			Log.e(CLASS_NAME, "Device MAC: " + macAddress);		
			query.whereKeyEqualsTo("DeviceMAC", macAddress);			
			query.find().continueWith(
					new Continuation<List<CurrentPosition>, Void>() {

						@Override
						public Void then(Task<List<CurrentPosition>> task)
								throws Exception {
							final List<CurrentPosition> objects = task.getResult();
							// Log if the find was cancelled.
							if (task.isCancelled()) {
								Log.e(CLASS_NAME,
										"Exception : Task " + task.toString()
												+ " was cancelled.");
							}
							// Log error message, if the find task fails.
							else if (task.isFaulted()) {
								Log.e(CLASS_NAME, "Exception : "
										+ task.getError().getMessage());
							}
							// If the result succeeds, load the list.
							else {
								Log.e(CLASS_NAME, "Data Fetched successfully");
								mCPList.clear();
								Log.e(CLASS_NAME, "1");
								for (IBMDataObject cp:objects) {	
									Log.e(CLASS_NAME, "Inside for loop 1");	
									try{
										mCPList.add((CurrentPosition) cp);
									}catch(Exception e){
										Log.e(CLASS_NAME, "Exception!!");	
									}
									Log.e(CLASS_NAME, "Inside for loop 2");
								}
								int i = mCPList.size();
								System.out.println(i);	
								
								if(!mCPList.isEmpty()){
									current = new CurrentPosition();
									current = mCPList.get(0);
								}
								
								Log.e(CLASS_NAME, "2");
								updateOrAddCurrentPosition();
								Log.e(CLASS_NAME, "3");																
							}
							return null;
						}
					}, Task.UI_THREAD_EXECUTOR);
		} catch (IBMDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private void updateOrAddCurrentPosition(){
		DBAdapter DB = DBAdapter.getInstance(mContext);
		HashMap<String, String> hashmap = DB.getLatestSavedGeoPoint();
		String macAddress = returnMacAddress();
		// Updating Current Position of the device to Mobile Cloud Data
		if (current != null) {
			Log.e(CLASS_NAME, "Updating Current Position record");
			current.setDate(hashmap.get(LocationMain.KEY_DATE));
			Log.e(CLASS_NAME, "Device MAC before current location update: " + macAddress);
			//current.setMac(macAddress);
			current.setLatitude(hashmap.get(LocationMain.KEY_LATITUDE));
			current.setLongitude(hashmap.get(LocationMain.KEY_LONGITUDE));
			uploadPositionToMobileCloud(current);
		} else {
			// First time updating the location of the Device to the cloud!
			Log.e(CLASS_NAME, "Adding Current Position record for the first time");
			CurrentPosition cposition = new CurrentPosition();
			cposition.setDate(hashmap.get(LocationMain.KEY_DATE));	
			Log.e(CLASS_NAME, "Device MAC before current location adding: " + macAddress);			
			cposition.setMac(macAddress);
			cposition.setLatitude(hashmap.get(LocationMain.KEY_LATITUDE));
			cposition.setLongitude(hashmap.get(LocationMain.KEY_LONGITUDE));
			uploadPositionToMobileCloud(cposition);
		}
	}
	
	private String returnMacAddress(){
		/* Set the Device MAC address */
		Context context = LocalSiriApplication.getContext();
		SharedPreferences sharedpreferences = context.getSharedPreferences(LocalSiriApplication.PREFERENCES, 0);			
		String macAddress = sharedpreferences.getString(LocalSiriApplication.MAC, "");
		return macAddress;
	}
	
	private boolean isCurrentLocationUpdated(){
		/* Set the Device MAC address */
		Context context = LocalSiriApplication.getContext();
		SharedPreferences sharedpreferences = context.getSharedPreferences(LocalSiriApplication.PREFERENCES, 0);			
		boolean notFirstUpdate = sharedpreferences.getBoolean(LocalSiriApplication.CURRENT_LOCATION, true);
		return notFirstUpdate;
	}	
	
}