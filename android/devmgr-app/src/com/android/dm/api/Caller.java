package com.android.dm.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.dm.Constants;
import com.android.dm.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Caller {
	
	private static final String ERROR_TAG = "CALLER";

	// TODO: potential problems with printing error messages (if message is null)
	
	// Webservice Information
	// TODO: use a config file for this stuff
	private static final String API_URL = "http://10.0.2.2:8000/api/";
	private static final String REGISTER_API = "devices/register";
	private static final String REGISTER_C2DM_ID_API ="devices/c2dm/%id/register";
	private static final String LOCATION_API = "devices/%id/location";
	private static final String WIPE_API = "devices/%id/wipestatus";
	
	// Singleton class
	private static Caller instance = null;
	private Caller() {}
	
	public static Caller getIntance() {
		if (instance == null) {
			instance = new Caller();
		}
		return instance;
	}
	
	// Register a device using an Http Create request
	public boolean registerDevice(Context context, String deviceName, boolean allowTracking) {
		
		// Prepare the request
		String jsonString = "{\"name\":\"" + deviceName + "\", \"allow_tracking\":\"" + allowTracking + "\"}";
		HttpPost httpPost = new HttpPost(API_URL + REGISTER_API);
		httpPost.addHeader("Content-Type", "application/json");
		try {
			httpPost.setEntity(new StringEntity(jsonString));
		} catch (UnsupportedEncodingException e) {
			Log.e(ERROR_TAG, e.getMessage());
		}
		
		// Make the call
		HttpResponse response = makeHttpCall(httpPost);
		
		// Check the response and store the returned device_id
		if (checkResponse(HttpStatus.SC_CREATED, response)) {
			JSONObject json = extractJSON(response);
			try {
				Utilities.editSharedPref(context, Constants.KEY_DEVICE_ID, json.getString("id").replace("L", ""));
				Utilities.editSharedPref(context, Constants.KEY_DEVICE_NAME, deviceName);
			} catch (JSONException e) {
				Log.e(ERROR_TAG, e.getMessage());
			}
			return true;
		}
		return false;
	}
	
	// Send google c2dm id with server
	public boolean updateC2DMId(Context context, String id) {
		
		// Prepare the request
		String jsonString = "{\"google_id\":\"" + id + "\"}";
		HttpPut httpPut = new HttpPut(API_URL + REGISTER_C2DM_ID_API.replace("%id", getDeviceID(context)));
		httpPut.addHeader("Content-Type", "application/json");
		try {
			httpPut.setEntity(new StringEntity(jsonString));
		} catch (UnsupportedEncodingException e) {
			Log.e(ERROR_TAG, e.getMessage());
		}
		
		// Make the call
		HttpResponse response = makeHttpCall(httpPut);
		
		// Check the response
		return checkResponse(HttpStatus.SC_OK, response);
	}
	
	// Send the current location of a device
	public boolean updateLocation(Context context, double latitude, double longitude) {
		
		// Prepare the request
		String jsonString = "{\"latitude\":" + latitude + ", \"longitude\":" + longitude + "}";
		HttpPut httpPut = new HttpPut(API_URL + LOCATION_API.replace("%id", getDeviceID(context)));
		httpPut.addHeader("Content-Type", "application/json");
		try {
			httpPut.setEntity(new StringEntity(jsonString));
		} catch (UnsupportedEncodingException e) {
			Log.e(ERROR_TAG, e.getMessage());
		}
		
		// Make the call
		HttpResponse response = makeHttpCall(httpPut);
		
		// Check response
		return checkResponse(HttpStatus.SC_OK, response);
	}
	
	// Update whether a device is allowed to be tracked
	public boolean updateAllowTrack(Context context, boolean allowTrack) {
		
		// Prepare the request
		String jsonString = "{\"allow_tracking\":\"" + allowTrack + "\"}";
		HttpPut httpPut = new HttpPut(API_URL + LOCATION_API.replace("%id", getDeviceID(context)));
		httpPut.addHeader("Content-Type", "application/json");
		try {
			httpPut.setEntity(new StringEntity(jsonString));
		} catch (UnsupportedEncodingException e) {
			Log.e(ERROR_TAG, e.getMessage());
		}
		
		// Make the call
		HttpResponse response = makeHttpCall(httpPut);
		
		// Check response
		return checkResponse(HttpStatus.SC_OK, response);
	}
	
	// Check if the device is to be wiped (returns true if wipe requested)
	public boolean checkWipeRequest(Context context) {
		
		// Prepare the request
		HttpGet httpGet = new HttpGet(API_URL + WIPE_API.replace("%id", getDeviceID(context)));
		
		// Make the call
		HttpResponse response = makeHttpCall(httpGet);
		
		// Check response
		if (checkResponse(HttpStatus.SC_OK, response)) {
			JSONObject json = extractJSON(response);
			try {
				String id = json.getString("id");
				if (!id.equalsIgnoreCase(getDeviceID(context))) {
					Log.e(ERROR_TAG, "Returned id of wipe request doesn't match this device's id");
					return false;
				}
				return Boolean.parseBoolean(json.getString("wipe_requested"));
			} catch (JSONException e) {
				Log.e(ERROR_TAG, e.getMessage());
			}
		}
		return false;
	}
	
	// Helper for making a HttpCall
	private HttpResponse makeHttpCall(HttpUriRequest request) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		
		try {
			response = httpClient.execute(request);
		} catch (ClientProtocolException e) {
			Log.e(ERROR_TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(ERROR_TAG, e.getMessage());
		}
		return response;
	}
	
	//////////////////// HELPERS /////////////////////////////
	
	// Helper function to extract a JSON object from a response
	private JSONObject extractJSON(HttpResponse response) {
		JSONObject json = null;
		try {
			String content = convertStreamToString(response.getEntity().getContent());
			json = new JSONObject(content);
		} catch (IllegalStateException e) {
			Log.e(ERROR_TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(ERROR_TAG, e.getMessage());
		} catch (JSONException e) {
			Log.e(ERROR_TAG, e.getMessage());
		}
		return json;
	}
	
	// Helper function to convert an InputStream to a String
	private String convertStreamToString(InputStream is) { 
	    return new Scanner(is).useDelimiter("\\A").next();
	}
	
	// Helper function to get this device's id
	private String getDeviceID(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.getString(Constants.KEY_DEVICE_ID, "-1");
	}
	
	// Helper to check for successful response code
	private boolean checkResponse(int expectedResponseCode, HttpResponse response) {
		if (response == null) {
			Log.e(ERROR_TAG, "Connection to API failed on device register");
			return false;
		} else if (response.getStatusLine().getStatusCode() != expectedResponseCode) {
			Log.e(ERROR_TAG, "Failure registering device");
			return false;
		}
		return true;
	}
}