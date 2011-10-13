package com.mozilla.android.devmgr.tools;

import android.app.Activity;

public class DMActivity extends Activity {

	public DMActivity() {
		super();
	}
	
	public DMApplication getAppContext() {
		return (DMApplication) getApplicationContext();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getAppContext().setCurrentActivity(this);
	}
}
