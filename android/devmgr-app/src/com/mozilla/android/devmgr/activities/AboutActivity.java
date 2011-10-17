package com.mozilla.android.devmgr.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mozilla.android.devmgr.R;
import com.mozilla.android.devmgr.base.DMActivity;
import com.mozilla.android.devmgr.services.LocationUpdateService;
import com.mozilla.android.devmgr.tools.Constants;
import com.mozilla.android.devmgr.tools.Utilities;

public class AboutActivity extends DMActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		
		final TextView tvDeviceIdValue = (TextView) findViewById(R.id.tvDeviceIdValue);			
		
		// Set registration id
		String deviceId = Utilities.getSharedPrefString(this, Constants.KEY_DEVICE_ID);
		if (!deviceId.equalsIgnoreCase(Utilities.SP_DEFAULT_STRING)) {
			tvDeviceIdValue.setText(deviceId);
		}
		
		// Setup reset registration button
		Button btResetRegistration = (Button) findViewById(R.id.btResetRegistration);
		btResetRegistration.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LocationUpdateService.stopServiceAndAlarm(AboutActivity.this);
				Utilities.resetSharedPreferences(AboutActivity.this);
				tvDeviceIdValue.setText(getString(R.string.dfDeviceId));
			}
		});
	}
}
