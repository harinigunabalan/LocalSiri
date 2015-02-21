package tud.kom.dss6.localsiri.localservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import tud.kom.dss6.localsiri.LocalSiriApplication;
import tud.kom.dss6.localsiri.R;
import tud.kom.dss6.localsiri.IBMDataObjects.CurrentPosition;
import tud.kom.dss6.localsiri.localservice.collection.CriteriaSelector;
import tud.kom.dss6.localsiri.localservice.collection.Optimizer;
import tud.kom.dss6.localsiri.monitor.MonitorAdaption;
import tud.kom.dss6.localsiri.monitor.PreferenceLocation;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocalSiriService extends Service implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener {

	private static final String TAG = LocalSiriService.class.getSimpleName();

	protected GoogleApiClient mGoogleApiClient;

	protected LocationManager locationManager;
	protected LocationRequest mLocationRequest;
	protected Location mCurrentLocation;

	protected Boolean mRequestingLocationUpdates;
	protected boolean mServiceStatus;

	LocalSiriApplication lsApplication;
	List<CurrentPosition> currentPositionList;

	private int uploadCounter = 0;
	IntentFilter ifilter;
	Intent batteryStatus;
	DBAdapter DB;

	@Override
	public void onCreate() {
		super.onCreate();

		DB = DBAdapter.getInstance(this);

		mRequestingLocationUpdates = false;
		mServiceStatus = false;

		ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryStatus = this.registerReceiver(null, ifilter);

		buildGoogleApiClient();

		/* Use application class to maintain global state. */
		lsApplication = (LocalSiriApplication) getApplication();
		currentPositionList = lsApplication.getCurrentPositionList();

	}

	protected synchronized void buildGoogleApiClient() {
		Log.i(TAG, "Building GoogleApiClient");
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		createLocationRequest();
	}

	protected void createLocationRequest() {
		Log.d(TAG, "Creating Location Request");
		mLocationRequest = new LocationRequest();

		mLocationRequest.setInterval(Constants.DEFAULT.UPDATE_INTERVAL);
		mLocationRequest
				.setFastestInterval(Constants.DEFAULT.FASTEST_UPDATE_INTERVAL);
		mLocationRequest.setPriority(Constants.DEFAULT.PRIORITY);
	}

	protected void updateLocationRequest(Optimizer optimizer) {

		Log.i(TAG, "Updating Listener: \n Initial Interval: "
				+ mLocationRequest.getInterval() + "\nDesired Interval: "
				+ optimizer.getFrequency());

		if (optimizer.getFrequency() == Constants.FREQUENCY_LEVEL.DEAD) {
			Log.i(TAG, "Deactivating Service. Reason: Battery about to Die");
			passivate();
			return;
		}

		mLocationRequest.setInterval(optimizer.getFrequency());
		mLocationRequest.setPriority(optimizer.getPriority());

		Log.i(TAG,
				"Updated Listener: \n Changed Interval: "
						+ mLocationRequest.getInterval());

		passivate();
		activate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
			Log.i(TAG, "Received Start Foreground Intent ");

			Intent notificationIntent = new Intent(this, LocationMain.class);
			notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);

			Intent passivateIntent = new Intent(this, LocalSiriService.class);
			passivateIntent.setAction(Constants.ACTION.PASSIVATE_ACTION);
			PendingIntent ppassivateIntent = PendingIntent.getService(this, 0,
					passivateIntent, 0);

			Intent activateIntent = new Intent(this, LocalSiriService.class);
			activateIntent.setAction(Constants.ACTION.ACTIVATE_ACTION);
			PendingIntent pactivateIntent = PendingIntent.getService(this, 0,
					activateIntent, 0);

			Bitmap icon = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_notification);

			Notification notification = new NotificationCompat.Builder(this)
					.setContentTitle("LocalSiri")
					.setTicker("LocalSiri(Service Started Running)")
					.setContentText("You are never alone")
					.setSmallIcon(R.drawable.ic_launcher)
					.setLargeIcon(
							Bitmap.createScaledBitmap(icon, 128, 128, false))
					.setContentIntent(pendingIntent)

					.setOngoing(true)
					.addAction(android.R.drawable.btn_star_big_off,
							"Passivate", ppassivateIntent)
					.addAction(android.R.drawable.ic_menu_help, "Activate",
							pactivateIntent).build();
			startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
					notification);

			activate();

		} else if (intent.getAction().equals(Constants.ACTION.PASSIVATE_ACTION)) {
			Log.i(TAG, "Deactivate Service");
			passivate();
		} else if (intent.getAction().equals(Constants.ACTION.ACTIVATE_ACTION)) {
			Log.i(TAG, "Re-activate Service");
			activate();
		} else if (intent.getAction().equals(
				Constants.ACTION.STOPFOREGROUND_ACTION)) {
			Log.i(TAG, "Received Stop Foreground Intent");
			if (mServiceStatus) {
				stopForeground(true);
				stopSelf();
			} else {
				Log.i(TAG, "Service is Not running Trying to stop");
			}
		}

		return START_STICKY;
	}

	protected void startLocationUpdates() {
		Log.i(TAG, "Registered with Location Listener");

		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	protected void addGeoLocation(Location location) {

		if (location != null) {

			HashMap<String, String> geoPointsSet = new HashMap<String, String>();

			geoPointsSet.put(LocationMain.KEY_DATE, getDateTime());
			geoPointsSet.put(LocationMain.KEY_LATITUDE,
					String.valueOf(location.getLatitude()));
			geoPointsSet.put(LocationMain.KEY_LONGITUDE,
					String.valueOf(location.getLongitude()));

			String message = "adding to DB. Latitude: "
					+ String.valueOf(location.getLatitude()) + "\n"
					+ "Longitude: " + String.valueOf(location.getLongitude());

			DB.insertGeoPoints(geoPointsSet);
			Log.d(TAG, message);
		} else {
			Log.d(TAG, "No Info");
		}
	}

	public String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	public void activate() {
		if (!mServiceStatus) {
			mGoogleApiClient.connect();
			mServiceStatus = true;
		} else {
			Log.i(TAG, "Service is Already Active");
		}
	}

	public void passivate() {

		if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {

			Log.i(TAG, "Stopping LocalSiriServices Listeners");
			stopLocationUpdates();

			Log.i(TAG, "Disconnecting Google API Client");
			mGoogleApiClient.disconnect();

			mServiceStatus = false;
		} else {
			Log.i(TAG, "Service is Already Passive");
		}
	}

	/**
	 * <p>
	 * <b>moitorContext() - </b> Method that verifies the current context
	 * details
	 * </p>
	 * We check for the following, <li>Battery Level</li><li>User Preferences</li>
	 */
	public void monitorContext() {

		MonitorAdaption monitorAdaption = new MonitorAdaption();
		PreferenceLocation preferenceLocation = new PreferenceLocation();
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		preferenceLocation.mBatteryLevel = (level * 100) / scale;

		Log.e("Test", "Battery Level: " + preferenceLocation.mBatteryLevel);

		preferenceLocation.isIntelligenceOverridden = (sharedPref.getBoolean(
				Constants.SETTINGS.pref_key_intelligent_optimizer, true) == true) ? false
				: true;

		Log.e("Criteria", "Shareed: "
				+ preferenceLocation.isIntelligenceOverridden);
		preferenceLocation.mUserScheme = Constants.SETTINGS.pref_key_intelligent_optimizer;

		if ((preferenceLocation.isIntelligenceOverridden)) {
			Log.i("Test", "Intelligent Optimizer is Overridden");
			preferenceLocation.mUserScheme = sharedPref.getString(
					Constants.SETTINGS.pref_key_location_scheme, getResources()
							.getStringArray(R.array.locationChoiceValues)[0]);
		}

		CriteriaSelector criteriaSelector = new CriteriaSelector(
				preferenceLocation);
		Optimizer optimizer = criteriaSelector.getOptimizedCriteria();

		boolean isAdapted = (optimizer.getFrequency() != mLocationRequest
				.getInterval()) ? true : false;
		
		if (isAdapted) {
			Log.i("Test", "Adapting to Context");
			
			monitorAdaption.updateMonitor(optimizer, mLocationRequest,
					preferenceLocation);
			updateLocationRequest(optimizer);
		} else {
			Log.i("Test", "No need adaption");
			monitorAdaption.updateMonitor(optimizer, mLocationRequest,
					preferenceLocation);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i(TAG, "Closing the DB Object");
		DB.close();
		passivate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Used only in case of bound services.
		return null;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(TAG, "Connected to GoogleApiClient");

		if (mCurrentLocation == null) {
			Log.i(TAG, "First Time Update");
			mCurrentLocation = LocationServices.FusedLocationApi
					.getLastLocation(mGoogleApiClient);
			addGeoLocation(mCurrentLocation);
			startLocationUpdates();
		} else {
			startLocationUpdates();
		}

	}

	@Override
	public void onLocationChanged(Location location) {

		Context context = getApplicationContext();
		SharedPreferences sharedpreferences = context.getSharedPreferences(
				LocalSiriApplication.PREFERENCES, MODE_PRIVATE);
		boolean success = false;
		int mode = 2; // set the mode as 0-WiFi only, 1-Optimized 3G and
						// 2-Normal 3G
		int RECORD_COUNTER;
		RECORD_COUNTER = (mode == 1) ? 12 : 6;
		uploadCounter = sharedpreferences.getInt(
				LocalSiriApplication.UPLOAD_COUNTER, 0);
		RECORD_COUNTER = (mode == 1) ? 12 : 6;
		uploadCounter = uploadCounter + 1;
		// edit shared Preference
		Editor editor = sharedpreferences.edit();
		editor.putInt(LocalSiriApplication.UPLOAD_COUNTER, uploadCounter);
		editor.commit();

		mCurrentLocation = location;
		addGeoLocation(mCurrentLocation);
		monitorContext();

		if (uploadCounter >= RECORD_COUNTER) {

			UploadService uploadService = new UploadService(this,
					currentPositionList);
			success = uploadService.uploadOptimizer(mode);

			if (success == true) {
				uploadCounter = 0;
				editor.putInt(LocalSiriApplication.UPLOAD_COUNTER,
						uploadCounter);
				editor.putString(LocalSiriApplication.LAST_UPLOAD, "");
				editor.commit();
				// TODO: clear local SQLite DB
			}

			if (success == true) {
				uploadCounter = 0;
				// TODO: clear local SQLite DB
			}
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "Connection suspended");
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());
	}

}
