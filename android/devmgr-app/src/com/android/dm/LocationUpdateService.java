package com.android.dm;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import com.android.dm.api.Caller;
import com.android.dm.tools.DMService;

public class LocationUpdateService extends DMService {
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// Acquire a reference to the system Location Manager
			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// Define a listener that responds to location updates
			mLocationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					// Called when a new location is found by the network
					// location provider.
					updateLocation(location);
					
					setAlarm();
					stopSelf();
				}

				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}

				public void onProviderEnabled(String provider) {
				}

				public void onProviderDisabled(String provider) {
				}
			};

			// Register the listener with the Location Manager to receive
			// location updates
			// TODO optimize location gathering as specified on
			// http://developer.android.com/guide/topics/location/obtaining-user-location.html
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		}
	}
	
	@Override
	public void onCreate() {
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		mLocationManager.removeUpdates(mLocationListener);
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}

	private void updateLocation(Location location) {
		// TODO eventually do check for improved accuracy and only store if this
		// is better
		// than an older one, etc.
		Utilities.saveLocation(this, location);
		Caller.updateLocation(this, location);
		getAppContext().notifyLocationChanged();
	}

	private void setAlarm() {

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		// Get frequency of location reporting in seconds
		// TODO used C2DM to make this a push call when this changes
		int frequency = Caller.getLocationUpdateFrequency(this);
		calendar.add(Calendar.SECOND, frequency);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				getAlarmManagerLocationServicePI(this));
	}
	
	public static PendingIntent getAlarmManagerLocationServicePI(Context context) {
		Intent myIntent = new Intent(context, LocationUpdateService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);
		return pendingIntent;
	}
}