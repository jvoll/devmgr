package com.android.dm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Utilities {
	
	public static void editSharedPref(Context context, String key, String value) {
		Editor editor = getEditor(context);
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void editSharedPref(Context context, String key, boolean value) {
		Editor editor = getEditor(context);
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static String getSharedPrefString(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.getString(key, "");
	}
	
	public static boolean getSharedPrefBool(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.getBoolean(key, false);
	}
	
	// Helper to get shared preferences editor
	private static Editor getEditor(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.edit();
	}

}
