package com.mozilla.android.devmgr.c2dm;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mozilla.android.devmgr.api.Caller;
import com.mozilla.android.devmgr.tools.Constants;

public class RegistrationIDReceiver extends IntentService {
	// TODO various locking stuff for power consumption and other purposes:
	// http://www.vogella.de/articles/AndroidCloudToDeviceMessaging/article.html
    private static final String EXTRA_ERROR = "error";
    private static final String EXTRA_REGISTRATION_ID = "registration_id";
    private static final String EXTRA_UNREGISTERED = "unregistered";
    private static final String ERROR_SERVICE_NOT_AVAIL = "SERVICE_NOT_AVAILABLE";

    public RegistrationIDReceiver() {
        super(Constants.C2DM_EMAIL);
    	android.os.Debug.waitForDebugger();
    }

    @Override
    public final void onHandleIntent(Intent intent) {
        Log.d(Constants.TAG, "Received intent to register");
        
        final String registrationId = intent.getStringExtra(
                EXTRA_REGISTRATION_ID);
        String error = intent.getStringExtra(EXTRA_ERROR);
        String removed = intent.getStringExtra(EXTRA_UNREGISTERED);
        
        if (removed != null) {
        	// Unregistered
        	C2DMessaging.clearRegistrationId(this);
        } else if (error == null) {
            registerDevice(registrationId);
        } else {
            handleRegistrationError(error, intent);
        }
    }

    // Save registration id
    private void registerDevice(String registrationId) {
        Log.d(Constants.TAG, "Saving and sending google id: " +
                registrationId);
        
        C2DMessaging.setRegistrationId(this, registrationId);
        
        // TODO check if call to server worked, try again later if failed
        Caller.updateC2DMId(this, registrationId);
    }

    private void handleRegistrationError(String error, Intent intent) {
        Log.e(Constants.TAG, "Registration error " + error);
        
        // Clear registration since we aren't registered at this point
        C2DMessaging.clearRegistrationId(this);
        
        if (error.equalsIgnoreCase(ERROR_SERVICE_NOT_AVAIL)) {
        	long backoffTimeMs = C2DMessaging.getBackoff(this);
        	
        	Log.d(Constants.TAG, "Scheduling registratin retry, backoff = " 
        							+ backoffTimeMs);
        	
        	Intent retryIntent = new Intent(Constants.C2DM_RETRY);
        	PendingIntent retryPIntent = PendingIntent.getBroadcast(this, 0, retryIntent, 0);
        	
        	AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			am.set(AlarmManager.ELAPSED_REALTIME, backoffTimeMs, retryPIntent);

			// Next retry should wait longer.
			backoffTimeMs *= 2;
			C2DMessaging.setBackoff(this, backoffTimeMs);
        }
        
    }
}
