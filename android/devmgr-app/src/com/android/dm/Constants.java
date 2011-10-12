package com.android.dm;

public interface Constants {

	// Keys for Shared Preferences
	public static final String KEY_PREFS_FILE = "com.android.dm";
	public static final String KEY_DEVICE_ID = "com.android.dm.id";
	public static final String KEY_ALLOW_TRACK = "com.android.dm.track";
	public static final String KEY_DEVICE_NAME = "com.android.dm.name";

	// Intent name for location update service
	public static final String LOCATION_SERVICE_INTENT = "com.android.dm.intent.START_LOCATION_SERVICE";

	// C2DM Related Constants
	public static final String C2DM_EMAIL = "jvoll@mozilla.com";
	public static final String TAG = "c2dm";

	public static final String REGISTRATION_INTENT = "com.android.dm.c2dm.intent.REGISTER";
	public static final String SEND_REGISTRATION_TO_GOOGLE = "com.google.android.c2dm.intent.REGISTER";
	public static final String SEND_UNREGISTRATION_TO_GOOGLE = "com.google.android.c2dm.intent.UNREGISTER";
	public static final String RECEIVED_REGISTRATION_ID_FROM_GOOGLE = "com.google.android.c2dm.intent.REGISTRATION";
	public static final String RECEIVED_C2DM_MESSAGE_FROM_GOOGLE = "com.google.android.c2dm.intent.RECEIVE";
	public static final String START_C2DM_SERVICE = "com.android.dm.intent.START_SERVICE";
	public static final String C2DM_RETRY = "com.google.android.c2dm.intent.RETRY";
}
