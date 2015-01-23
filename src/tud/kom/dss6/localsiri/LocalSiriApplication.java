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

import tud.kom.dss6.localsiri.IBMDataObjects.Position;
import tud.kom.dss6.localsiri.IBMDataObjects.Post;
import tud.kom.dss6.localsiri.IBMDataObjects.Topic;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.core.IBMBluemix;
import com.ibm.mobile.services.data.IBMData;
import com.ibm.mobile.services.push.IBMPush;
import com.ibm.mobile.services.push.IBMPushNotificationListener;
import com.ibm.mobile.services.push.IBMSimplePushNotification;

public class LocalSiriApplication extends Application{

	private static final String APP_ID = "applicationID";
	private static final String APP_SECRET = "applicationSecret";
	private static final String APP_ROUTE = "applicationRoute";
	private static final String PROPS_FILE = "LocalSiri.properties";	
	public static final int EDIT_ACTIVITY_RC = 1;
	private static final String CLASS_NAME = LocalSiriApplication.class
			.getSimpleName();
	
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
	
// Tracking the activity status within four of the ActivityLifecycleCallbacks
// onActivityCreated, onActivityStarted, onActivityResumed, onActivityPaused 	
	public LocalSiriApplication() {
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity,
					Bundle savedInstanceState) {
				Log.d(CLASS_NAME,
						"Activity created: " + activity.getLocalClassName());
				//Track activity
				mActivity = activity;
			}

			@Override
			public void onActivityStarted(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity started: " + activity.getLocalClassName());
				//Track activity
				mActivity = activity;
			}

			@Override
			public void onActivityResumed(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity resumed: " + activity.getLocalClassName());
				//Track activity
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
			}

			@Override
			public void onActivityPaused(Activity activity) {
				if (push != null) {
					push.hold();
				}
				Log.d(CLASS_NAME,
						"Activity paused: " + activity.getLocalClassName());
				//Track activity
				if (activity != null && activity.equals(mActivity))
					mActivity = null;				
			}

			@Override
			public void onActivityStopped(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity stopped: " + activity.getLocalClassName());
			}

			@Override
			public void onActivityDestroyed(Activity activity) {
				Log.d(CLASS_NAME,
						"Activity destroyed: " + activity.getLocalClassName());
			}
		});
	}	

	@Override
	public void onCreate() {
        // in most cases the following initialising code using defaults is probably sufficient:
        //
        // LocationLibrary.initialiseLibrary(getBaseContext(), "com.your.package.name");
        //
        // however for the purposes of the test app, we will request unrealistically frequent location broadcasts
        // every 1 minute, and force a location update if there hasn't been one for 2 minutes.		
		//LocationLibrary.initialiseLibrary(getBaseContext(), 60 * 1000, 2 * 60 * 1000, "dss6.komlab.tu.localsiri");
		super.onCreate();
		positionList = new ArrayList<Position>();
		topicList = new ArrayList<Topic>();
		postList = new ArrayList<Post>();
		// Read from properties file.
		Properties props = new java.util.Properties();
		Context context = getApplicationContext();
		try {
			AssetManager assetManager = context.getAssets();
			props.load(assetManager.open(PROPS_FILE));
			Log.i(CLASS_NAME, "Found configuration file: " + PROPS_FILE);
		} catch (FileNotFoundException e) {
			Log.e(CLASS_NAME, "The LocalSiri.properties file was not found.", e);
		} catch (IOException e) {
			Log.e(CLASS_NAME,
					"The LocalSiri.properties file could not be read properly.", e);
		}
		// Initialize the IBM core back end-as-a-service.
		IBMBluemix.initialize(this, props.getProperty(APP_ID), props.getProperty(APP_SECRET), props.getProperty(APP_ROUTE));
		// Initialize the IBM Data Service.
		IBMData.initializeService();
		// Register the Item Specialization.
		Position.registerSpecialization(Position.class);
		Topic.registerSpecialization(Topic.class);
		Post.registerSpecialization(Post.class);
		
		// Initialize IBM Push service.
		IBMPush.initializeService();
		// Retrieve instance of the IBM Push service.
		push = IBMPush.getService();
		// Register the device with the IBM Push service.
		push.register(deviceAlias, consumerID).continueWith(new Continuation<String, Void>() {

			@Override
			public Void then(Task<String> task) throws Exception {
				if (task.isCancelled()) {
					Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
				} else if (task.isFaulted()) {
					Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
				} else {
					Log.d(CLASS_NAME, "Device Successfully Registered");
				}
				return null;
			}
		});		
		
	    // Create an object of IBMPushNotificationListener and implement its onReceive method
	    notificationListener = new IBMPushNotificationListener() {
	      @Override
	      public void onReceive(IBMSimplePushNotification message) {	
	    	Log.d(CLASS_NAME, "on Receive of the Notification Listener!");	    	  
	    	processPushMessage(message);
	      }
	    };		
		
	}
	
	public void processPushMessage(final IBMSimplePushNotification message){
		String Payload = message.getPayload();
		System.out.println("Payload");
		Intent intent = new Intent(this, HomeScreen.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
    	intent.putExtra("Test", Payload);
        //startActivity(intent);
    	PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
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
	
}
