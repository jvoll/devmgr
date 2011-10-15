package com.mozilla.android.devmgr.c2dm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mozilla.android.devmgr.tools.Constants;

/**
 * Receives messages sent from the C2DM service and passes them to an Android
 * service that can then deal with then as needed.
 */
public class C2DMBroadcastReceiver extends BroadcastReceiver {
    
    @Override
    public final void onReceive(Context context, Intent intent) {
    	Log.d(Constants.TAG, "Received something back from mother google!");
        if (Constants.RECEIVED_REGISTRATION_ID_FROM_GOOGLE.equals(intent.getAction()) ||
        		Constants.C2DM_RETRY.equalsIgnoreCase(intent.getAction())) {
            Log.d(Constants.TAG, "Received a registration ID from Google.");
            intent.setAction(Constants.REGISTRATION_INTENT);
            intent.setClassName(context, RegistrationIDReceiver.class.getName());
	        context.startService(intent);
        } else if (Constants.RECEIVED_C2DM_MESSAGE_FROM_GOOGLE.equals(intent.getAction())) {
            Log.d(Constants.TAG, "Received a C2DM message from Google.");
            handleMessage(context, intent);
        }
        else {
        	Log.w(Constants.TAG, "Got unhandled intent with action: " + intent.getAction());
        }
    }
    
    private void handleMessage(Context context, Intent intent) {
    	Log.d(Constants.TAG, "oooohhh a messsage");
    	Bundle extras = intent.getExtras();
    	Log.d(Constants.TAG, "Message: " + extras.get("payload"));
		if (extras.containsKey("payload")) {
	    	Toast.makeText(context, "Message received from c2dm " + extras.get("payload"), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "Message with no payload received from c2dm", Toast.LENGTH_LONG).show();
		}
    }
}
