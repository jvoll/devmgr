package com.android.dm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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
import com.android.dm.c2dm.C2DMessaging;

public class StartActivity extends Activity {
	
	ViewSwitcher vsRegister;
	
	
	// Temporary Debug stuff
	boolean reg = true;
	public Button btRegister;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        
        // Configure location tracking
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            	// Called when a new location is found by the network location provider.
            	updateLocation(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
          };

        // Register the listener with the Location Manager to receive location updates
        // TODO optimize location gathering as specified on 
        // http://developer.android.com/guide/topics/location/obtaining-user-location.html
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        // Register button handler
        btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				registerDevice(v);
			}
		});
        
        // Tracking checkbox handler
        CheckBox cbAllowTrack = (CheckBox) findViewById(R.id.cbAllowTrack);
        cbAllowTrack.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				trackingCheckChange(buttonView, isChecked);
			}
		});
        
        vsRegister = (ViewSwitcher) findViewById(R.id.vsRegisterDevice);
        // TODO disable check box until device registered and change instructions label accordingly
        // Switch the view if this device is already registered
        vsRegister = (ViewSwitcher) findViewById(R.id.vsRegisterDevice);
        SharedPreferences prefs = getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
        if (prefs.contains(Constants.KEY_DEVICE_NAME)) {
        	Log.i("prefs", "device already registered as: " + prefs.getString(Constants.KEY_DEVICE_NAME, "none"));
        	View button = vsRegister.getCurrentView().findViewById(R.id.btRegister);
        	if (button != null) {
        		vsRegister.showNext();
        	}
        	TextView tvNameValue = (TextView) findViewById(R.id.tvNameValue);
			tvNameValue.setText(prefs.getString(Constants.KEY_DEVICE_NAME, getString(R.id.tvNameValue)));
        }
    }
    
    // Handler for a location change event
    private void updateLocation(Location location) {
    	Double latitude = location.getLatitude();
    	Double longitude = location.getLongitude();
    	TextView tvLatitude = (TextView) findViewById(R.id.tvLatitudeValue);
    	TextView tvLongitude = (TextView) findViewById(R.id.tvLongitudeValue);
    	tvLatitude.setText(Double.toString(latitude));
    	tvLongitude.setText(Double.toString(longitude));
    	Caller.getIntance().updateLocation(this, latitude, longitude);
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
    	
    	// Get allow track settings
    	CheckBox cbAllowTrack = (CheckBox) findViewById(R.id.cbAllowTrack);
    	
    	// Call the api to register the device
		if ( Caller.getIntance().registerDevice(this, name.trim(), cbAllowTrack.isChecked()) ) {
			
			// Switch the view
			vsRegister.showNext();
			TextView tvNameValue = (TextView) findViewById(R.id.tvNameValue);
			tvNameValue.setText(name);
			
			// Register with google for c2dm
			C2DMessaging.register(this);
			
		} else {
			Toast.makeText(StartActivity.this, getString(R.string.emRegisterFailed),
							Toast.LENGTH_LONG).show();
		}
    }
    
    // Handler for the Allow tracking checkbox
    private void trackingCheckChange(CompoundButton buttonView, boolean isChecked) {
    	
    	if ( Caller.getIntance().updateAllowTrack(this, isChecked) ) {
    		Utilities.editSharedPref(this, Constants.KEY_ALLOW_TRACK, isChecked);
    	}
    }
}