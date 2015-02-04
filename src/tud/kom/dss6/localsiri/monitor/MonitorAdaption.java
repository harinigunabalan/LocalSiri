package tud.kom.dss6.localsiri.monitor;

import tud.kom.dss6.localsiri.localservice.collection.Optimizer;

import com.google.android.gms.location.LocationRequest;

public class MonitorAdaption {

	MonitorPojo monitorPojo = new MonitorPojo();
	
	public void updateMonitor(Optimizer optimizer,
			LocationRequest mLocationRequest, PreferenceLocation preferenceLocation) {
	
		monitorPojo.setmBatteryLevel(preferenceLocation.mBatteryLevel);
		monitorPojo.setmLocationFrequency((int)mLocationRequest.getInterval());
		monitorPojo.setmLocationFrequency_Adapted(optimizer.getFrequency());
		monitorPojo.setmLocationMode(mLocationRequest.getPriority());
		monitorPojo.setmLocationMode_Adapted(optimizer.getPriority());
		monitorPojo.setmUserPreference_Location(preferenceLocation.mUserScheme);
		
		

	}

}
