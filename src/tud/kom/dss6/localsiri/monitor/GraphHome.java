package tud.kom.dss6.localsiri.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import tud.kom.dss6.localsiri.R;
import tud.kom.dss6.localsiri.localservice.DBAdapter;
import tud.kom.dss6.localsiri.localservice.LocationMain;
import tud.kom.dss6.localsiri.localservice.Settings;
import tud.kom.dss6.localsiri.monitor.Graph.Line;
import tud.kom.dss6.localsiri.monitor.Graph.LineGraph;
import tud.kom.dss6.localsiri.monitor.Graph.LineGraph.OnPointClickedListener;
import tud.kom.dss6.localsiri.monitor.Graph.LinePoint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GraphHome extends Activity {

	DBAdapter DB;
	
	int range = 10;
	Line batteryLine;
	Line frequencyLine;
	String[] date; 
	String[] optimization_mode;
	String[] priority;
	
	TextView txtDate;
	TextView txtBatteryLevel;
	TextView txtScheme;
	TextView txtFrequency;
	TextView txtPriority;
	ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);
		
		Intent intent = getIntent();

        range = intent.getIntExtra("Range", 10);
        
        date = new String[range];
        optimization_mode = new String[range];
        priority = new String[range];
        
		progressDialog = ProgressDialog.show(this, "Please Wait",
				"Generating Graph", true, true);
		Button reloadGraph = (Button) findViewById(R.id.reload_graph);
		
		DB = new DBAdapter(this);
		txtDate = (TextView) findViewById(R.id.g_date_field);
		txtBatteryLevel = (TextView) findViewById(R.id.g_batteryLevel_field);
		txtScheme = (TextView) findViewById(R.id.g_mUserPreference_field);
		txtFrequency = (TextView) findViewById(R.id.g_mLocationFrequency_field);
		txtPriority = (TextView) findViewById(R.id.g_mLocationPriority_field);
		
		reloadGraph.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = ProgressDialog.show(GraphHome.this, "Please Wait",
						"Reloading Graph", true, true);
				
				Toast.makeText(GraphHome.this, "Reloading Graph", Toast.LENGTH_SHORT).show();
				displayGraph();
			}

		});
		displayGraph();

	}

	public void displayGraph() {
		
		ArrayList<HashMap<String, String>> monitorArrayList = DB
				.getMonitorValues(range);

		Iterator<HashMap<String, String>> iterator = monitorArrayList
				.iterator();

		int index = 0;
		batteryLine = new Line();
		frequencyLine = new Line();

		while (iterator.hasNext()) {
			HashMap<String, String> monitorValue = iterator.next();

			LinePoint batteryLinePoints = new LinePoint();
			LinePoint frequencyLinePoints = new LinePoint();

			batteryLinePoints.setX(index);
			frequencyLinePoints.setX(index);

			batteryLinePoints.setY(Integer.parseInt(monitorValue
					.get("BatteryLevel")));
			frequencyLinePoints.setY((Integer.parseInt(monitorValue
					.get("LocationFrequency_Adapted")) / 1000));

			date[index] = monitorValue.get("Date");
			optimization_mode[index] = monitorValue.get("UserPreference_Location");
			priority[index] = monitorValue.get("LocationMode_Adapted");

			batteryLine.addPoint(batteryLinePoints);
			frequencyLine.addPoint(frequencyLinePoints);

			index++;
		}

		batteryLine.setColor(getResources().getColor(R.color.batteryline));
		frequencyLine.setColor(getResources().getColor(R.color.frequencyLine));

		LineGraph lg = (LineGraph) findViewById(R.id.graph);
		lg.addLine(batteryLine);
		lg.addLine(frequencyLine);
		lg.setRangeY(0, 120);
		
		if(progressDialog.isShowing()){
			progressDialog.dismiss();
		}

		lg.setOnPointClickedListener(new OnPointClickedListener() {

			@Override
			public void onClick(int lineIndex, int pointIndex) {
				clickResult(lineIndex, pointIndex);
			}
		});
	}

	public void clickResult(int lineIndex, int pointIndex) {
		
		txtDate.setText(date[pointIndex]);
		txtBatteryLevel.setText(String.valueOf(batteryLine.getPoint(pointIndex).getY()));
		txtScheme.setText(optimization_mode[pointIndex]);
		txtFrequency.setText(String.valueOf(frequencyLine.getPoint(pointIndex).getY()));
		txtPriority.setText(priority[pointIndex]);
		
		/*Toast.makeText(
				GraphHome.this,
				"Line " + lineIndex + " / Point " + pointIndex + " clicked"
						+ " battery: " + batteryLine.getPoint(pointIndex)
						+ " date: " + date[pointIndex] + " frequency: "
						+ frequencyLine.getPoint(pointIndex),

				Toast.LENGTH_SHORT).show();*/
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
