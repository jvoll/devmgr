package com.mozilla.android.devmgr.tools;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;

public class Utilities {
	
	private static final String KEY_LOC_LAT = "com.mozilla.android.devmgr.lat";
	private static final String KEY_LOC_LONG = "com.mozilla.android.devmgr.long";
	private static final String KEY_LOC_PROVIDER = "com.mozilla.android.devmgr.loc_provider";
	private static final String KEY_LOC_ACCURACY = "com.mozilla.android.devmgr.loc_accuracy";
	private static final String KEY_LOC_TIME = "com.mozilla.android.devmgr.loc_time";
	
	// Defines what is returned when there is no value set of a shared pref
	public static final String SP_DEFAULT_STRING = "";
	public static final int SP_DEFAULT_INT = 0;
	public static final float SP_DEFAULT_FLOAT = 0;
	public static final long SP_DEFAULT_LONG = 0;
	public static final boolean SP_DEFAULT_BOOL = false;
	
	// Edit a shared preference (a persistent storage mechanism)
	public static void editSharedPref(Context context, String key, String value) {
		Editor editor = getEditor(context);
		editor.putString(key, value);
		editor.commit();
	}
	
	// Edit a shared preference (a persistent storage mechanism)
	public static void editSharedPref(Context context, String key, boolean value) {
		Editor editor = getEditor(context);
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	// Edit a shared preference (a persistent storage mechanism)
	public static void editSharedPref(Context context, String key, int value) {
		Editor editor = getEditor(context);
		editor.putInt(key, value);
		editor.commit();
	}

	// Get a shared preference (persistent storage)
	public static String getSharedPrefString(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.getString(key, SP_DEFAULT_STRING);
	}
	
	// Get a shared preference (persistent storage)
	public static boolean getSharedPrefBool(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.getBoolean(key, false);
	}
	
	// Get a shared preference (persistent storage)
	public static int getSharedPrefInt(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.getInt(key, SP_DEFAULT_INT);
	}
	
	// Reset shared preferences to defaults
	public static void resetSharedPreferences(Context context) {
		Editor editor = getEditor(context);
		editor.putString(Constants.KEY_DEVICE_ID, SP_DEFAULT_STRING);
		editor.putBoolean(Constants.KEY_ALLOW_TRACK, SP_DEFAULT_BOOL);
		editor.putString(Constants.KEY_DEVICE_NAME, SP_DEFAULT_STRING);
		editor.putInt(Constants.KEY_LOC_UPDATE_FREQUENCY, SP_DEFAULT_INT);
		resetLocation(editor);
		editor.commit();
	}

	// Save last location to persistent storage
	public static void saveLocation(Context context, Location location) {
		Editor editor = getEditor(context);
		if (location != null) {
			editor.putFloat(KEY_LOC_LAT, (float) location.getLatitude());
			editor.putFloat(KEY_LOC_LONG, (float) location.getLongitude());
			editor.putString(KEY_LOC_PROVIDER, location.getProvider());
			editor.putFloat(KEY_LOC_ACCURACY, location.getAccuracy());
			editor.putLong(KEY_LOC_TIME, location.getTime());
		} else {
			resetLocation(editor);
		}
		editor.commit();		
	}
	
	// Reset location information
	private static void resetLocation(Editor editor) {
		editor.putFloat(KEY_LOC_LAT, SP_DEFAULT_FLOAT);
		editor.putFloat(KEY_LOC_LONG, SP_DEFAULT_FLOAT);
		editor.putString(KEY_LOC_PROVIDER, SP_DEFAULT_STRING);
		editor.putFloat(KEY_LOC_ACCURACY, SP_DEFAULT_FLOAT);
		editor.putLong(KEY_LOC_TIME, SP_DEFAULT_LONG);
	}
	
	// Get last location from persistent storage
	public static Location getLocation(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
		if(prefs.contains(KEY_LOC_PROVIDER)) {
			Location location = new Location(prefs.getString(KEY_LOC_PROVIDER, ""));
			location.setLatitude((double) prefs.getFloat(KEY_LOC_LAT, 0));
			location.setLongitude((double) prefs.getFloat(KEY_LOC_LONG, 0));
			location.setAccuracy(prefs.getFloat(KEY_LOC_ACCURACY, 0));
			location.setTime( prefs.getLong(KEY_LOC_TIME, 0));
			return location;
		}
		return null;
	}
	
	// Helper to get shared preferences editor
	private static Editor getEditor(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.edit();
	}

}