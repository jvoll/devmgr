package com.android.dm.tools;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DMService extends Service {
	
	public DMService() {
		super();
	}
	
	public DMApplication getAppContext() {
		return (DMApplication) getApplicationContext();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
