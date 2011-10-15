package com.mozilla.android.devmgr.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mozilla.android.devmgr.R;
import com.mozilla.android.devmgr.api.Caller;
import com.mozilla.android.devmgr.base.DMActivity;
import com.mozilla.android.devmgr.services.LocationUpdateService;
import com.mozilla.android.devmgr.tools.Constants;
import com.mozilla.android.devmgr.tools.Utilities;

public class SettingsActivity extends DMActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		
		// Handle press of save button
		Button btSave = (Button) findViewById(R.id.btSave);
		btSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveSettings();
			}
		});
		
		// Handle press of the cancel button
		Button btCancel = (Button) findViewById(R.id.btCancel);
		btCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		// Display the current server address
		EditText etServerAddress = (EditText) findViewById(R.id.etServerAddress);
		String url = Utilities.getSharedPrefString(this, Constants.KEY_SERVER_ADDRESS);
		if (url.equalsIgnoreCase(Utilities.SP_DEFAULT_STRING)) {
			url = Constants.DEFAULT_API_URL;
		}
		etServerAddress.setText(url);
		
		// Hide location update frequency edit text and display a message if tracking is disabled
		EditText etLocationUpdateFrequency = (EditText) findViewById(R.id.etLocationUpdateFrequency);
		if (Utilities.getSharedPrefBool(this, Constants.KEY_ALLOW_TRACK)) {
			int frequency = Caller.getLocationUpdateFrequency(this);
			etLocationUpdateFrequency.setText(Integer.toString(frequency));
			TextView tvLocTrackingDisabled = (TextView) findViewById(R.id.tvTrackingDisabled);
			tvLocTrackingDisabled.setVisibility(View.GONE);
		}
		else {
			etLocationUpdateFrequency.setVisibility(View.GONE);
		}
	}
	
	/////////////////////// HANDLERS //////////////////////////
	
	// Save settings
	private void saveSettings() {
		
		// Save the server address
		EditText etServerAddress = (EditText) findViewById(R.id.etServerAddress);
		String address = etServerAddress.getText().toString();
		if (!address.equalsIgnoreCase("")) {
			// Remove trailing slash if it exists
			if (address.charAt(address.length() -1) == '/') {
				address = address.substring(0, address.length() -1);
			}
			// Save the address
			Utilities.editSharedPref(this, Constants.KEY_SERVER_ADDRESS, address);
		}
		
		// Save the location update frequency and set a new alarm with the new frequency
		if (Utilities.getSharedPrefBool(this, Constants.KEY_ALLOW_TRACK)) {
			EditText etLocationUpdateFrequency = (EditText) findViewById(R.id.etLocationUpdateFrequency);
			int frequency = Integer.parseInt(etLocationUpdateFrequency.getText().toString());
			Utilities.editSharedPref(this, Constants.KEY_LOC_UPDATE_FREQUENCY, frequency);
			Caller.updateLocationUpdateFrequency(this, frequency);
			LocationUpdateService.stopServiceAndAlarm(this);
			LocationUpdateService.setAlarm(this);
		}
		
		// TODO catch errors, allow users to correct mistakes, notify of save successful
		finish();
	}
}