package tud.kom.dss6.localsiri.localservice;

import tud.kom.dss6.localsiri.HomeScreen;
import tud.kom.dss6.localsiri.R;
import tud.kom.dss6.localsiri.knowuraddress.KnowYourAddress;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class LocationMain extends Activity {

	public static final String KEY_DATE = "date";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String CLASS_NAME = "MainLocation";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_main);
		
	    ActionBar actionBar = getActionBar();
	    actionBar.setHomeButtonEnabled(true);
	    

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				switch (position) {
				case 0:
					Intent mKnowYourAddress = new Intent(LocationMain.this,
							KnowYourAddress.class);
					startActivity(mKnowYourAddress);
					
					break;
				case 1:
					Intent settings = new Intent(LocationMain.this,
							Settings.class);
					startActivity(settings);
					break;
				case 2:
					Intent startIntent = new Intent(LocationMain.this,
							LocalSiriService.class);
					startIntent
							.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
					startService(startIntent);
					break;
				case 3:
					Intent stopIntent = new Intent(LocationMain.this,
							LocalSiriService.class);
					stopIntent
							.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
					startService(stopIntent);
					break;
				default:
					break;
				}
			}

		});
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
		case R.id.menu_save:
			startActivity(new Intent(this, HomeScreen.class));
			return true;
		}
		return false;
	}
}