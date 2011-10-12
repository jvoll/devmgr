package com.android.dm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.dm.api.Caller;

public class StartActivity extends Activity {
	
	ViewSwitcher vsRegister;
	CheckBox cbAllowTrack;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        
        // Register button handler
        Button btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				registerDevice(v);
			}
		});
        
        // Switch the view if this device is already registered
        vsRegister = (ViewSwitcher) findViewById(R.id.vsRegisterDevice);
        vsRegister = (ViewSwitcher) findViewById(R.id.vsRegisterDevice);
        cbAllowTrack = (CheckBox) findViewById(R.id.cbAllowTrack);
        cbAllowTrack.setEnabled(false);
        if (!Utilities.getSharedPrefString(this, Constants.KEY_DEVICE_NAME).equalsIgnoreCase("")) {
        	View button = vsRegister.getCurrentView().findViewById(R.id.btRegister);
        	if (button != null) {
        		vsRegister.showNext();
        	}
        	TextView tvNameValue = (TextView) findViewById(R.id.tvNameValue);
			tvNameValue.setText(Utilities.getSharedPrefString(this, Constants.KEY_DEVICE_NAME));
			cbAllowTrack.setEnabled(true);
	        cbAllowTrack.setChecked(Utilities.getSharedPrefBool(this, Constants.KEY_ALLOW_TRACK));
        }
        
        // Tracking checkbox handler
        cbAllowTrack.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				trackingCheckChange(buttonView, isChecked);
			}
		});
    }
    
    // Handler for the Register button
    private void registerDevice(View v) {
    	
    	// Get device name
    	EditText etName = (EditText) findViewById(R.id.etName);
    	String name = etName.getText().toString();
    	if (name.equalsIgnoreCase("")) {
    		Toast.makeText(this, getString(R.string.mgEnterName), Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	// Call the api to register the device
		if (Caller.registerDevice(this, name.trim())) {
			
			// Switch the view
			vsRegister.showNext();
			cbAllowTrack.setEnabled(true);
			TextView tvNameValue = (TextView) findViewById(R.id.tvNameValue);
			tvNameValue.setText(name);
			
			// Register with google for c2dm
			// C2DMessaging.register(this);
			
		} else {
			Toast.makeText(StartActivity.this, getString(R.string.emRegisterFailed),
							Toast.LENGTH_LONG).show();
		}
    }
    
    // Handler for the Allow tracking checkbox
    private void trackingCheckChange(CompoundButton buttonView, boolean isChecked) {
    	
    	// Update preferences on the server
        if (!Caller.updateAllowTrack(this, isChecked)) {
        	Toast.makeText(this, getString(R.string.emAllowTrackingFailed), Toast.LENGTH_LONG).show();
        	cbAllowTrack.setChecked(!isChecked);
        	return;
        }
        
        // Save locally to persistent storage
		final Intent intent = new Intent();
		intent.setAction(Constants.LOCATION_SERVICE_INTENT);
        intent.setClassName(this, LocationUpdateService.class.getName());
		Utilities.editSharedPref(this, Constants.KEY_ALLOW_TRACK, isChecked);
        
		// Display a nag message to make sure users know their location will be
		// sent to and stored on Mozilla servers
		if (isChecked) {
			
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(getString(R.string.mgAllowTracking))
	    	       .setPositiveButton(getString(R.string.btYes), new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	               // Handle Yes 
	    	        	   startService(intent);
	    	           }
	    	       })
	    	       .setNegativeButton(getString(R.string.btNo), new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	               // Handle No - uncheck the box and make sure no updates are happening 
	    	        	   cbAllowTrack.setChecked(false);
	    	        	   cancelLocationUpdates(intent);
	    	           }
	    	       })
	    	       .show();
		} else {
    		cancelLocationUpdates(intent);
    	}
    }
    
    private void cancelLocationUpdates(Intent intent) {
		// Cancel any alarms that would restart the service
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.cancel(LocationUpdateService.getAlarmManagerLocationServicePI(this));
		
		// Stop the service if it is running
		stopService(intent);
    }
}