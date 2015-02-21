package tud.kom.dss6.localsiri;

import java.util.List;

import tud.kom.dss6.localsiri.IBMDataObjects.Topic;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

public class MyTopics extends Activity {

	List<Topic> topicList;
	protected ListView listview;
	Topic topic;
	LocalSiriApplication lsApplication;
	public final String CLASS_NAME = "MyTopics";
	public final static String TOPIC_CHAT = "dss6.komlab.tu.localsiri.MyTopics.TOPIC_ID";

	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_topics);

		listview = (ListView) findViewById(R.id.listTopics);

		/* Use application class to maintain global state. */
		lsApplication = (LocalSiriApplication) getApplication();
		topicList = lsApplication.getTopicList();
		Log.e(CLASS_NAME, "Entered My Topics");

		progressDialog = ProgressDialog.show(this, "Please Wait",
				"Loading your Topics", true, true);

		listMyTopics();

		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(getApplicationContext(),
						ChatForum.class);
				Topic chosen = topicList.get(position);
				String msg = chosen.getObjectId();
				System.out.println("Topic ID is:" + msg);
				intent.putExtra(TOPIC_CHAT, msg);
				intent.putExtra("Topic_name", chosen.getTopic());
				startActivity(intent);
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

	public void listMyTopics() {
		try {

	    	// Reference: http://www.ibm.com/developerworks/library/mo-android-mobiledata-app/
	    	// IBM Bluemix Tutorial for retrieving Data from mobile cloud 			
			
			Log.e(CLASS_NAME, "Entered listMyTopics");
			IBMQuery<Topic> query = IBMQuery.queryForClass(Topic.class);

			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo wInfo = wifiManager.getConnectionInfo();
			String macAddress = wInfo.getMacAddress();
			Log.e(CLASS_NAME, "Device MAC: " + macAddress);

			query.whereKeyEqualsTo("DeviceMAC", macAddress);
			query.find().continueWith(new Continuation<List<Topic>, Void>() {

				@Override
				public Void then(Task<List<Topic>> task) throws Exception {
					final List<Topic> objects = task.getResult();
					// Log if the find was cancelled.
					if (task.isCancelled()) {
						Log.e(CLASS_NAME, "Exception : Task " + task.toString()
								+ " was cancelled.");
					}
					// Log error message, if the find task fails.
					else if (task.isFaulted()) {
						Log.e(CLASS_NAME, "Exception : "
								+ task.getError().getMessage());
						progressDialog.dismiss();
						Toast.makeText(MyTopics.this,
								task.getError().getMessage(),
								Toast.LENGTH_SHORT).show();
					}
					// If the result succeeds, load the list.

					else {
						Log.e(CLASS_NAME, "Data Fetched successfully");

						topicList.clear();
						for (IBMDataObject topic : objects) {
							topicList.add((Topic) topic);
							Log.e(CLASS_NAME, "Inside for loop");
						}

						int i = topicList.size();
						System.out.println(i);

						if (!topicList.isEmpty()) {
							progressDialog.dismiss();
							Log.e(CLASS_NAME, "Inside List");
							ArrayAdapter<Topic> adapter = new ArrayAdapter<Topic>(
									MyTopics.this,
									android.R.layout.simple_list_item_1,
									topicList);
							Log.e(CLASS_NAME, "After Array Adapter");
							listview.setAdapter(adapter);
							Log.e(CLASS_NAME, "It shud work!");

						} else {
							progressDialog.dismiss();
							Toast.makeText(MyTopics.this,
									"No Data available to display",
									Toast.LENGTH_SHORT).show();
						}

					}
					return null;
				}
			}, Task.UI_THREAD_EXECUTOR);

		} catch (Exception e) {
			Log.e(CLASS_NAME, "Query Exception");
		}

	}
}