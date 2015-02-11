package tud.kom.dss6.localsiri.monitor;

import java.util.ArrayList;
import java.util.HashMap;

import tud.kom.dss6.localsiri.R;
import tud.kom.dss6.localsiri.localservice.DBAdapter;
import tud.kom.dss6.localsiri.localservice.LocationMain;
import tud.kom.dss6.localsiri.localservice.Settings;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Monitor extends ListActivity {

	Button btnDisplayMonitor;
	EditText etxtListCount;
	EditText etxtGraphCount;

	DBAdapter DB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);

		DB = new DBAdapter(this);
		btnDisplayMonitor = (Button) findViewById(R.id.show_monitor_list);

		etxtListCount = (EditText) findViewById(R.id.etxtListCount);
		etxtGraphCount = (EditText) findViewById(R.id.etxtGraphCount);

		etxtListCount.setText("0");
		etxtGraphCount.setText("10");

		btnDisplayMonitor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				displayMonitorLog();
			}

		});
	}

	private void displayMonitorLog() {

		ArrayList<HashMap<String, String>> monitorValues = new ArrayList<HashMap<String, String>>();

		if (etxtListCount.getText().toString().equals("0")) {
			Toast.makeText(this, "Displaying all values from the DB",
					Toast.LENGTH_SHORT).show();
			monitorValues = DB.getMonitorValues();
		} else if (etxtListCount.getText().toString().equals("")) {
			monitorValues = DB.getMonitorValues();
			Toast.makeText(this, "Displaying all values from the DB",
					Toast.LENGTH_SHORT).show();
		} else {
			int range = Integer.parseInt(etxtListCount.getText().toString());
			Toast.makeText(this, "Displaying " + range + " values from the DB",
					Toast.LENGTH_SHORT).show();
			monitorValues = DB.getMonitorValues(range);
		}

		if (!monitorValues.isEmpty()) {
			ListAdapter adapter = new SimpleAdapter(Monitor.this,
					monitorValues, R.layout.row_monitor, new String[] { "Date",
							"BatteryLevel", "UserPreference_Location",
							"LocationFrequency", "LocationFrequency_Adapted",
							"LocationMode", "LocationMode_Adapted" },
					new int[] { R.id.date_field, R.id.batteryLevel_field,
							R.id.mUserPreference_field,
							R.id.mLocationFrequency_field,
							R.id.mLocationFrequency_Adapted_field,
							R.id.mLocationPriority_field,
							R.id.mLocationPriority_Adapted_field });

			setListAdapter(adapter);
		} else {
			Toast.makeText(Monitor.this, "No Data available to display",
					Toast.LENGTH_SHORT).show();
		}
		DB.close();
	}

	public void monitorGraph(View view) {
		Intent graph = new Intent(this, GraphHome.class);

		int range = 10;

		if (etxtGraphCount.getText().toString().equals("10")) {
			range = 10;
		} else if (etxtGraphCount.getText().toString().equals("")) {
			range = 10;
		} else {
			range = Integer.parseInt(etxtGraphCount.getText().toString());
		}
		graph.putExtra("Range", range);
		startActivity(graph);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.location_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Log.d("Menu", "Clicked Home");
			Intent intent = new Intent(this, LocationMain.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		case R.id.menu_settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		}
		return false;
	}
}
