package tud.kom.dss6.localsiri.localservice;

import tud.kom.dss6.localsiri.R;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Settings extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		// //enable the home button
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		String key = "asdasd";

	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.d("settings", "Changed Key:" + key);
		Preference connectionPref;
		switch (key) {
		case Constants.SETTINGS.pref_key_do_not_disturb:
			connectionPref = findPreference(key);
			if (sharedPreferences.getBoolean(key, false)) {
				connectionPref.setSummary("Working on");
			} else {
				connectionPref.setSummary("Working for off");
			}
			break;
		
		case Constants.SETTINGS.pref_key_master_service:
			if (sharedPreferences.getBoolean(key, true)) {
				Intent startIntent = new Intent(this, LocalSiriService.class);
				startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
				startService(startIntent);
			} else {
				Intent stopIntent = new Intent(this, LocalSiriService.class);
				stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
				startService(stopIntent);
			}
			break;
		
		case Constants.SETTINGS.pref_key_temporary_service:
			if (sharedPreferences.getBoolean(key, true)) {
				Intent startIntent = new Intent(this, LocalSiriService.class);
				startIntent.setAction(Constants.ACTION.PLAY_ACTION);
				startService(startIntent);
			} else {
				Intent stopIntent = new Intent(this, LocalSiriService.class);
				stopIntent.setAction(Constants.ACTION.PREV_ACTION);
				startService(stopIntent);
			}
			break;
		}
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
			startActivity(new Intent(this, LocationMain.class));
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}
}
