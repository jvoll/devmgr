package com.mozilla.android.devmgr.tools;

public interface Constants {

	// Keys for Shared Preferences
	public static final String KEY_PREFS_FILE = "com.mozilla.android.devmgr";
	public static final String KEY_DEVICE_ID = "com.mozilla.android.devmgr.id";
	public static final String KEY_ALLOW_TRACK = "com.mozilla.android.devmgr.track";
	public static final String KEY_DEVICE_NAME = "com.mozilla.android.devmgr.name";
	public static final String KEY_LOC_UPDATE_FREQUENCY = "com.mozilla.android.devmgr.loc_update_frequency";
	public static final String KEY_SERVER_ADDRESS = "com.mozilla.android.devmgr.server_address";
	
	// Constants for service to activity messages
	public static final int MESSAGE_LOCATION_UPDATE = 100;
	
	// Server URL
	public static final String DEFAULT_API_URL = "http://10.0.2.2:8000";

	// Intent name for location update service
	public static final String LOCATION_SERVICE_INTENT = "com.mozilla.android.devmgr.intent.START_LOCATION_SERVICE";

	// C2DM Related Constants
	public static final String C2DM_EMAIL = "jvoll@mozilla.com";
	public static final String TAG = "c2dm";

	public static final String REGISTRATION_INTENT = "com.mozilla.android.devmgr.c2dm.intent.REGISTER";
	public static final String SEND_REGISTRATION_TO_GOOGLE = "com.google.android.c2dm.intent.REGISTER";
	public static final String SEND_UNREGISTRATION_TO_GOOGLE = "com.google.android.c2dm.intent.UNREGISTER";
	public static final String RECEIVED_REGISTRATION_ID_FROM_GOOGLE = "com.google.android.c2dm.intent.REGISTRATION";
	public static final String RECEIVED_C2DM_MESSAGE_FROM_GOOGLE = "com.google.android.c2dm.intent.RECEIVE";
	public static final String START_C2DM_SERVICE = "com.mozilla.android.devmgr.intent.START_SERVICE";
	public static final String C2DM_RETRY = "com.google.android.c2dm.intent.RETRY";
}
