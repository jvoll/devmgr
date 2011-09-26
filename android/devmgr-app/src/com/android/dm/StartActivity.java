package com.android.dm;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ViewSwitcher;

public class StartActivity extends Activity {
	
	ViewSwitcher vsRegister;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        
        vsRegister = (ViewSwitcher) findViewById(R.id.vsRegisterDevice);
        
        // Register button handler
        Button btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// Get caller to register the device
				// On success show the other view
			}
		});
        
        // Allow tracking checkbox handler
        CheckBox cbAllowTrack = (CheckBox) findViewById(R.id.cbAllowTrack);
        cbAllowTrack.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// Get caller to allow tracking
				// Setup background thread to send GPS updates
			}
		});
    }
}