/*
 * @author Harini Gunabalan
 * @author Hariharan Gandhi 
 */
package tud.kom.dss6.localsiri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import tud.kom.dss6.localsiri.IBMDataObjects.CurrentPosition;
import tud.kom.dss6.localsiri.IBMDataObjects.Position;
import tud.kom.dss6.localsiri.IBMDataObjects.Post;
import tud.kom.dss6.localsiri.IBMDataObjects.Topic;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.push.IBMPush;
import com.ibm.mobile.services.push.IBMPushNotificationListener;
import com.ibm.mobile.services.push.IBMSimplePushNotification;

public class LocalSiriApplication extends Application {

	private static final String APP_ID = "applicationID";
	private static final String APP_SECRET = "applicationSecret";
	private static final String APP_ROUTE = "applicationRoute";
	private static final String PROPS_FILE = "LocalSiri.properties";
	private static Context mContext;
	public static final int EDIT_ACTIVITY_RC = 1;
	private static final String CLASS_NAME = LocalSiriApplication.class
			.getSimpleName();

	// Shared Preference Variables
	public static final String PREFERENCES = "LocalSiriPrefs";
	public static final String MAC = "DeviceMAC";
	public static final String LAST_UPLOAD = "LastUpload";
	public static final String UPLOAD_COUNTER = "UploadCounter";
	public static final String CURRENT_LOCATION = "CurrentLocation";

	// Push Declarations begin
	public static IBMPush push = null;
	private Activity mActivity;
	private static final String deviceAlias = "TargetDevice";
	private static final String consumerID = "LocalSiriApp";
	private IBMPushNotificationListener notificationListener = null;
	// Push Declarations end

	List<Position> positionList;
	List<Topic> topicList;
	List<Post> postList;
	List<CurrentPosition> currentPositionList;

	// Tracking the activity status within four of the
	// ActivityLifecycleCallbacks
	// onActivityCreated, onActivityStarted, onActivityResumed, onActivityPaused
	public LocalSiriApplication() {
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity,
					Bundle savedInstanceState) {
				Log.d(CLASS_NAME,
						"Activity created: " + activity.getLocalClassName());
				// Track activity
				mActivity = activity;
			}

			@Override
			public void onActivityStarted(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity started: " + activity.getLocalClassName());
				// Track activity
				mActivity = activity;
				if (push != null) {
					push.listen(notificationListener);
				}
			}

			@Override
			public void onActivityResumed(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity resumed: " + activity.getLocalClassName());
				// Track activity
				mActivity = activity;

				if (push != null) {
					push.listen(notificationListener);
				}

			}

			@Override
			public void onActivitySaveInstanceState(Activity activity,
					Bundle outState) {
				Log.d(CLASS_NAME,
						"Activity saved instance state: "
								+ activity.getLocalClassName());
				if (push != null) {
					push.listen(notificationListener);
				}
			}

			@Override
			public void onActivityPaused(Activity activity) {
				/*
				 * if (push != null) { push.hold(); }
				 */

				if (push != null) {
					push.listen(notificationListener);
				}
				Log.d(CLASS_NAME,
						"Activity paused: " + activity.getLocalClassName());
				// Track activity
				if (activity != null && activity.equals(mActivity))
					mActivity = null;
			}

			@Override
			public void onActivityStopped(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity stopped: " + activity.getLocalClassName());
				if (push != null) {
					push.listen(notificationListener);
				}
			}

			@Override
			public void onActivityDestroyed(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity destroyed: " + activity.getLocalClassName());
				if (push != null) {
					push.listen(notificationListener);
				}
			}
		});
	}

	@Override
	public void onCreate() {

		super.onCreate();
		positionList = new ArrayList<Position>();
		topicList = new ArrayList<Topic>();
		postList = new ArrayList<Post>();
		currentPositionList = new ArrayList<CurrentPosition>();
		// Read from properties file.
		Properties props = new java.util.Properties();
		mContext = getApplicationContext();
		try {
			AssetManager assetManager = mContext.getAssets();
			props.load(assetManager.open(PROPS_FILE));
			Log.i(CLASS_NAME, "Found configuration file: " + PROPS_FILE);
		} catch (FileNotFoundException e) {
			Log.e(CLASS_NAME, "The LocalSiri.properties file was not found.", e);
		} catch (IOException e) {
			Log.e(CLASS_NAME,
					"The LocalSiri.properties file could not be read properly.",
					e);
		}
		// Initialize the IBM core back end-as-a-service.
		IBMBluemix.initialize(this, props.getProperty(APP_ID),
				props.getProperty(APP_SECRET), props.getProperty(APP_ROUTE));
		// Initialize the IBM Data Service.
		IBMData.initializeService();
		// Register the Item Specialization.
		Position.registerSpecialization(Position.class);
		Topic.registerSpecialization(Topic.class);
		Post.registerSpecialization(Post.class);
		CurrentPosition.registerSpecialization(CurrentPosition.class);

		// Initialize IBM Push service.
		IBMPush.initializeService();
		// Retrieve instance of the IBM Push service.
		push = IBMPush.getService();
		// Register the device with the IBM Push service.
		push.register(deviceAlias, consumerID).continueWith(
				new Continuation<String, Void>() {

					@Override
					public Void then(Task<String> task) throws Exception {
						if (task.isCancelled()) {
							Log.e(CLASS_NAME,
									"Exception : Task " + task.toString()
											+ " was cancelled.");
						} else if (task.isFaulted()) {
							Log.e(CLASS_NAME, "Exception : "
									+ task.getError().getMessage());
						} else {
							Log.d(CLASS_NAME, "Device Successfully Registered");
						}
						return null;
					}
				});

		// Create an object of IBMPushNotificationListener and implement its
		// onReceive method
		notificationListener = new IBMPushNotificationListener() {
			@Override
			public void onReceive(IBMSimplePushNotification message) {
				Log.e(CLASS_NAME, "on Receive of the Notification Listener!");
				processPushMessage(message);
			}

		};

		/* Set the Device MAC address */
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		String macAddress = wInfo.getMacAddress();

		// Creating Shared Preference for the Application
		SharedPreferences sharedpreferences = mContext.getSharedPreferences(
				LocalSiriApplication.PREFERENCES, MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.putString(MAC, macAddress);
		editor.putString(LAST_UPLOAD, "");
		editor.putInt(UPLOAD_COUNTER, 0);
		editor.putBoolean(CURRENT_LOCATION, false);
		editor.commit();
	}

	public static Context getContext() {
		return mContext;
	}

	public void processPushMessage(final IBMSimplePushNotification message) {
		Log.e(CLASS_NAME, "inside Notification Builder!");
		Context context = LocalSiriApplication.getContext();
		String payload = message.getPayload();
		System.out.println("Payload: " + payload);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.ic_launcher,
				"Message received", System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// adding LED lights to notification
		notification.defaults |= Notification.DEFAULT_LIGHTS;

		Intent intent = new Intent(context, MyTopics.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, "Do you know...", 
				"You have a new Query", pendingIntent);
		notificationManager.notify(0, notification);

		/*
		 * Intent intent = new Intent(this, AskSiri.class);
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		 * Intent.FLAG_ACTIVITY_CLEAR_TASK); intent.putExtra("Test", payload);
		 * startActivity(intent);
		 */
	}

	/**
	 * returns the itemList, an array of Position objects.
	 * 
	 * @return LocationList
	 */
	public List<Position> getPositionList() {
		return positionList;
	}

	/**
	 * returns the itemList, an array of Topic objects.
	 * 
	 * @return TopicList
	 */
	public List<Topic> getTopicList() {
		return topicList;
	}

	/**
	 * returns the itemList, an array of Post objects.
	 * 
	 * @return PostList
	 */
	public List<Post> getPostList() {
		return postList;
	}

	/**
	 * returns the currentPositionList, an array of Position objects.
	 * 
	 * @return CurrentPositionList
	 */
	public List<CurrentPosition> getCurrentPositionList() {
		return currentPositionList;
	}

}
