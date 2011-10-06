package com.android.dm.c2dm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.android.dm.Constants;

public class C2DMessaging {

	/*
	 * Utilities class for C2DM Messaging
	 */
	public static final String EXTRA_SENDER = "sender";
	public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
	public static final String KEY_GOOGLE_REG = "dm_google_reg";
	public static final String KEY_LAST_REG_CHANGE = "dm_last_reg_change";
	public static final String KEY_BACKOFF = "dm_backoff";
	
	private static final long DEFAULT_BACKOFF = 1000;
    
	// Initiate a c2d messaging registration
	// TODO add package to this? what is the benefit of this?
    public static void register(Context context) {
    	Log.i(Constants.TAG, "Initiating registration with c2dm");
        Intent registrationIntent = new Intent(Constants.SEND_REGISTRATION_TO_GOOGLE);
        registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        registrationIntent.putExtra(EXTRA_SENDER, Constants.C2DM_EMAIL);
        context.startService(registrationIntent);
    }
    
    // Initiate a c2d unregistration
    public static void unregister(Context context) {
    	Log.i(Constants.TAG, "Initiating unregistration with c2dm");
    	Intent unregIntent = new Intent(Constants.SEND_UNREGISTRATION_TO_GOOGLE);
    	unregIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT, PendingIntent.getBroadcast(context, 0, new Intent(), 0));
    	context.startService(unregIntent);
    }
    
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(
                Constants.KEY_PREFS_FILE,
                Context.MODE_PRIVATE);
        String registrationId = prefs.getString(KEY_GOOGLE_REG, "");
        return registrationId;
    }

    public static long getLastRegistrationChange(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(
                Constants.KEY_PREFS_FILE,
                Context.MODE_PRIVATE);
        return prefs.getLong(KEY_LAST_REG_CHANGE, 0);
    }
    
    public static long getBackoff(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(
                Constants.KEY_PREFS_FILE,
                Context.MODE_PRIVATE);
        return prefs.getLong(KEY_BACKOFF, DEFAULT_BACKOFF);
    }
    
    public static void setBackoff(Context context, long backoff) {
        final SharedPreferences prefs = context.getSharedPreferences(
                Constants.KEY_PREFS_FILE,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong(KEY_BACKOFF, backoff);
        editor.commit();
    }
    
    // package
    public static void clearRegistrationId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(
                Constants.KEY_PREFS_FILE,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(KEY_GOOGLE_REG, "");
        editor.putLong(KEY_LAST_REG_CHANGE, System.currentTimeMillis());
        editor.commit();
    }

    // package
    public static void setRegistrationId(Context context, String registrationId) {
        final SharedPreferences prefs = context.getSharedPreferences(
                Constants.KEY_PREFS_FILE,
                Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(KEY_GOOGLE_REG, registrationId);
        editor.commit();
    }
}