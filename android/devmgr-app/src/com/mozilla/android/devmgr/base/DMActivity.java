package com.mozilla.android.devmgr.base;

import com.mozilla.android.devmgr.R;
import com.mozilla.android.devmgr.activities.SettingsActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mnSettings:
			showSettings();
			return true;
		case R.id.mnAbout:
			showAbout();
			return true;
		}
		return false;
	}
	
	private void showSettings() {
		startActivity(new Intent(this, SettingsActivity.class));
	}
	
	private void showAbout() {
		
	}
}
