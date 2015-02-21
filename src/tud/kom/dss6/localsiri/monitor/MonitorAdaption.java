package tud.kom.dss6.localsiri.monitor;

import tud.kom.dss6.localsiri.LocalSiriApplication;
import tud.kom.dss6.localsiri.localservice.Constants;
import tud.kom.dss6.localsiri.localservice.DBAdapter;
import tud.kom.dss6.localsiri.localservice.LocalSiriService;
import tud.kom.dss6.localsiri.localservice.collection.Optimizer;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

public class MonitorAdaption {

	MonitorPojo monitorPojo = new MonitorPojo();
	Context mContext = LocalSiriApplication.getContext();

	public void updateMonitor(Optimizer optimizer,
			LocationRequest mLocationRequest,
			PreferenceLocation preferenceLocation) {

		LocalSiriService locationSiriService = new LocalSiriService();

		monitorPojo.setmDate(locationSiriService.getCurrentDateTime());
		monitorPojo.setmBatteryLevel(preferenceLocation.mBatteryLevel);
		monitorPojo.setmLocationFrequency((int) mLocationRequest.getInterval());
		monitorPojo.setmLocationFrequency_Adapted(optimizer.getFrequency());
		monitorPojo.setmUserPreference_Location(preferenceLocation.mUserScheme);

		switch (mLocationRequest.getPriority()) {
		case Constants.PRIORITY_LEVEL.HIGH:
			monitorPojo.setmLocationMode("PRIORITY_HIGH_ACCURACY");break;
		case Constants.PRIORITY_LEVEL.MEDIUM:
			monitorPojo.setmLocationMode("PRIORITY_BALANCED_POWER_ACCURACY");break;
		case Constants.PRIORITY_LEVEL.LOW:
			monitorPojo.setmLocationMode("PRIORITY_LOW_POWER");break;
		case Constants.PRIORITY_LEVEL.CRITICAL:
			monitorPojo.setmLocationMode("PRIORITY_NO_POWER");break;
		case Constants.PRIORITY_LEVEL.DEAD:
			monitorPojo.setmLocationMode("KILL THE SERVICE");break;
		}

		switch (optimizer.getPriority()) {
		case Constants.PRIORITY_LEVEL.HIGH:
			monitorPojo.setmLocationMode_Adapted("PRIORITY_HIGH_ACCURACY");break;
		case Constants.PRIORITY_LEVEL.MEDIUM:
			monitorPojo
					.setmLocationMode_Adapted("PRIORITY_BALANCED_POWER_ACCURACY");break;
		case Constants.PRIORITY_LEVEL.LOW:
			monitorPojo.setmLocationMode_Adapted("PRIORITY_LOW_POWER");break;
		case Constants.PRIORITY_LEVEL.CRITICAL:
			monitorPojo.setmLocationMode_Adapted("PRIORITY_NO_POWER");break;
		case Constants.PRIORITY_LEVEL.DEAD:
			monitorPojo.setmLocationMode_Adapted("KILL THE SERVICE");break;
		}

		Log.i("MONITOR:", monitorPojo.toString());

		DBAdapter DB = DBAdapter.getInstance(mContext);
		DB.updateMonitorLog(monitorPojo);
		DB.close();

	}
}
