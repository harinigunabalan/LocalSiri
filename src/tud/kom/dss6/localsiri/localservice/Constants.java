package tud.kom.dss6.localsiri.localservice;

import com.google.android.gms.location.LocationRequest;

public class Constants {
	public interface ACTION {
		public static String MAIN_ACTION = "com.truiton.foregroundservice.action.main";
		public static String PREV_ACTION = "com.truiton.foregroundservice.action.prev";
		public static String PLAY_ACTION = "com.truiton.foregroundservice.action.play";
		public static String NEXT_ACTION = "com.truiton.foregroundservice.action.next";
		public static String STARTFOREGROUND_ACTION = "com.truiton.foregroundservice.action.startforeground";
		public static String STOPFOREGROUND_ACTION = "com.truiton.foregroundservice.action.stopforeground";
	}

	public interface NOTIFICATION_ID {
		public static int FOREGROUND_SERVICE = 101;
	}

	public interface DEFAULT {
		public static final int UPDATE_INTERVAL = 20000;
		public static final int FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
		public static final int PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;

	}

	public interface BATTERY_LEVEL {
		public static final int HIGH = 80;
		public static final int MEDIUM = 50;
		public static final int LOW = 30;
		public static final int CRITICAL = 15;
	}

	public interface PRIORITY_LEVEL {
		public static final int HIGH = LocationRequest.PRIORITY_HIGH_ACCURACY;
		public static final int MEDIUM = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
		public static final int LOW = LocationRequest.PRIORITY_LOW_POWER;
		public static final int CRITICAL = LocationRequest.PRIORITY_NO_POWER;
		public static final int DEAD = 0;
	}

	/**
	 * 
	 * FREQUENCY_LEVEL in milliseconds
	 * 
	 */
	public interface FREQUENCY_LEVEL {
		public static final int HIGH = 10000;
		public static final int MEDIUM = 15000;
		public static final int LOW = 20000;
		public static final int CRITICAL = 30000;
		public static final int DEAD = 0;
	}

	public interface SETTINGS {
		public static final String pref_key_do_not_disturb = "pref_key_do_not_disturb";
		public static final String pref_key_master_service = "pref_key_master_service";
		public static final String pref_key_temporary_service = "pref_key_temporary_service";
		public static final String pref_key_intelligent_optimizer = "pref_key_intelligent_optimizer";
		public static final String pref_key_location_scheme = "pref_key_location_scheme";
		public static final String pref_key_wifi_only = "pref_key_wifi_only";
		public static final String pref_key_upload_scheme = "pref_key_upload_scheme Service";
	}
}