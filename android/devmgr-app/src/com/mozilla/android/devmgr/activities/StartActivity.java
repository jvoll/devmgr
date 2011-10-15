package com.mozilla.android.devmgr.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.mozilla.android.devmgr.R;
import com.mozilla.android.devmgr.api.Caller;
import com.mozilla.android.devmgr.base.DMActivity;
import com.mozilla.android.devmgr.services.LocationUpdateService;
import com.mozilla.android.devmgr.tools.Constants;
import com.mozilla.android.devmgr.tools.MessageHandler;
import com.mozilla.android.devmgr.tools.Utilities;

public class StartActivity extends DMActivity implements MessageHandler {
	
	Handler messageHandler;
	
	// UI Components
	ViewSwitcher vsRegister;
	CheckBox cbAllowTrack;
	TextView tvLatitude;
	TextView tvLongitude;
	TextView tvLocTime;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        
        // Create a message handler for processing messages from
        // any services (ex. location update service)
		messageHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
			switch(msg.what){
			     case Constants.MESSAGE_LOCATION_UPDATE:
		    	 	updateGUILocation();
		            break;
			   }
			}
		};
		
        // Register button handler
        Button btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				registerDevice(v);
			}
		});
        
        // Initialize UI components
        vsRegister = (ViewSwitcher) findViewById(R.id.vsRegisterDevice);
        cbAllowTrack = (CheckBox) findViewById(R.id.cbAllowTrack);
        tvLatitude = (TextView) findViewById(R.id.tvLatitudeValue);
        tvLongitude = (TextView) findViewById(R.id.tvLongitudeValue);
        tvLocTime = (TextView) findViewById(R.id.tvLocTimeValue);
        
        // Switch the view if this device is already registered
        if (!Utilities.getSharedPrefString(this, Constants.KEY_DEVICE_NAME).equalsIgnoreCase("")) {
        	View button = vsRegister.getCurrentView().findViewById(R.id.btRegister);
        	if (button != null) {
        		vsRegister.showNext();
        	}
        	
        	// Display device name
        	TextView tvNameValue = (TextView) findViewById(R.id.tvNameValue);
			tvNameValue.setText(Utilities.getSharedPrefString(this, Constants.KEY_DEVICE_NAME));
			
			// Enable allow tracking and set it appropriately
			cbAllowTrack.setEnabled(true);
			boolean allowTrack = Utilities.getSharedPrefBool(this, Constants.KEY_ALLOW_TRACK);
			cbAllowTrack.setChecked(allowTrack);
	        
	        // Set the last reported location
	        updateGUILocation();
	        
	        // Setup tracking if it is enabled
	        if (allowTrack) {
		        // Subscribe to messages from the service
		        getAppContext().subscribeMessages(this);
		        
		        // Restart the service in case it isn't running,
		        // even if it is, we'd like to get a new location since user
		        // is using the app and we want to show them responsiveness.
		        LocationUpdateService.stopServiceAndAlarm(this);
		        LocationUpdateService.startService(this);
	        }
	        
        } else {
	        cbAllowTrack.setEnabled(false);
        }
        
        // Tracking checkbox handler
        cbAllowTrack.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				trackingCheckChange(buttonView, isChecked);
			}
		});
    }
    
    ////////////////////////////// HANDLERS ////////////////////////////////////
    
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
		Utilities.editSharedPref(this, Constants.KEY_ALLOW_TRACK, isChecked);
        
		// Display a nag message to make sure users know their location will be
		// sent to and stored on Mozilla servers
		if (isChecked) {
			
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(getString(R.string.mgAllowTracking))
	    	       .setPositiveButton(getString(R.string.btYes), new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	               // Handle Yes 
	    	        	   LocationUpdateService.startService(StartActivity.this);
	    	        	   getAppContext().subscribeMessages(StartActivity.this);
	    	           }
	    	       })
	    	       .setNegativeButton(getString(R.string.btNo), new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	               // Handle No - uncheck the box and make sure no updates are happening 
	    	        	   cbAllowTrack.setChecked(false);
	    	        	   cancelLocationUpdates();
	    	           }
	    	       })
	    	       .show();
		} else {
    		cancelLocationUpdates();
    	}
    }

	public Handler getHandler() {
		return messageHandler;
	}
    
    //////////////////////////////// HELPERS ////////////////////////////////////
    
    private void cancelLocationUpdates() {
		
		// Stop service and any alarms which will start it
    	LocationUpdateService.stopServiceAndAlarm(this);
		
		// Reset location to defaults
		Utilities.saveLocation(this, null);
		updateGUILocation();
		
		// Remove this as a listener
		getAppContext().unsubscribeMessages(this);
		
    }
    
	private void updateGUILocation() {
		Location location = Utilities.getLocation(this);
		tvLatitude.setText(Double.toString(location.getLatitude()));
		tvLongitude.setText(Double.toString(location.getLongitude()));
		if (location.getTime() == 0) {
			tvLocTime.setText(getString(R.string.dfLocTime));
		} else {
			String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (location.getTime()));
			tvLocTime.setText(date);
		}
	}
}