package com.mozilla.android.devmgr.api;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.mozilla.android.devmgr.tools.Constants;
import com.mozilla.android.devmgr.tools.Utilities;

public class Caller {
	
	private static final String ERROR_TAG = "CALLER";

	// TODO: potential problems with printing error messages (if message is null)
	
	// Webservice Information
	private static final String REGISTER_API = "/api/register";
	private static final String REGISTER_C2DM_ID_API ="/api/c2dm/%id/register";
	private static final String LOCATION_API = "/api/%id/location";
	private static final String ALLOW_TRACK_API = "/api/%id/allowtrack";
	private static final String WIPE_API = "/api/%id/wipestatus";
	private static final String LOCATION_FREQUENCY_API = "/api/%id/trackfrequency";
	
	// Disable ability to instantiate this class
	private Caller() {}

	// Register a device using an Http Create request
	public static boolean registerDevice(Context context, String deviceName) {
		
		// Prepare the request
		String jsonString = "{\"name\":\"" + deviceName + "\"}";
		HttpPost httpPost = new HttpPost(getApiUrl(context) + REGISTER_API);
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
	public static boolean updateC2DMId(Context context, String id) {
		
		// Prepare the request
		String jsonString = "{\"google_id\":\"" + id + "\"}";
		HttpPut httpPut = new HttpPut(getApiUrl(context) + REGISTER_C2DM_ID_API.replace("%id", getDeviceID(context)));
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
	public static boolean updateLocation(Context context, Location location) {
		
		// Prepare the request
		String jsonString = "{\"latitude\":" + location.getLatitude() + ", \"longitude\":" + location.getLongitude()
							+ ", \"loc_timestamp\":" + location.getTime() + "}";
		HttpPut httpPut = new HttpPut(getApiUrl(context) + LOCATION_API.replace("%id", getDeviceID(context)));
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
	public static boolean updateAllowTrack(Context context, boolean allowTrack) {
		
		// Prepare the request
		String jsonString = "{\"allow_tracking\":\"" + allowTrack + "\"}";
		HttpPut httpPut = new HttpPut(getApiUrl(context) + ALLOW_TRACK_API.replace("%id", getDeviceID(context)));
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
	public static boolean checkWipeRequest(Context context) {
		
		// Prepare the request
		HttpGet httpGet = new HttpGet(getApiUrl(context) + WIPE_API.replace("%id", getDeviceID(context)));
		
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
	
	// Check the requested location update frequency
	public static int getLocationUpdateFrequency(Context context) {
		
		// Prepare the request
		HttpGet httpGet = new HttpGet(getApiUrl(context) + LOCATION_FREQUENCY_API.replace("%id", getDeviceID(context)));
		
		// Make the call
		HttpResponse response = makeHttpCall(httpGet);
		
		// Check response
		if (checkResponse(HttpStatus.SC_OK, response)) {
			JSONObject json = extractJSON(response);
			try {
				String id = json.getString("id");
				if (!id.equalsIgnoreCase(getDeviceID(context))) {
					Log.e(ERROR_TAG, "Returned id of wipe request doesn't match this device's id");
					return 0;
				}
				return json.getInt("track_frequency");
			} catch (JSONException e) {
				Log.e(ERROR_TAG, e.getMessage());
			}
		}
		return 0;
		
	}
	
	// Update the requested location update frequency
	public static boolean updateLocationUpdateFrequency(Context context, int frequency) {
		
		// Prepare the request
		String jsonString = "{\"track_frequency\":\"" + frequency + "\"}";
		HttpPut httpPut = new HttpPut(getApiUrl(context) + LOCATION_FREQUENCY_API.replace("%id", getDeviceID(context)));
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
	
	//////////////////// HELPERS /////////////////////////////
	
	// Helper for making a HttpCall
	private static HttpResponse makeHttpCall(HttpUriRequest request) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;
		
		try {
			response = httpClient.execute(request);
		} catch (IllegalArgumentException e) {
			Log.e(ERROR_TAG, e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e(ERROR_TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(ERROR_TAG, e.getMessage());
		}
		return response;
	}
	
	// Helper function to extract a JSON object from a response
	private static JSONObject extractJSON(HttpResponse response) {
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
	private static String convertStreamToString(InputStream is) { 
	    return new Scanner(is).useDelimiter("\\A").next();
	}
	
	// Helper function to get this device's id
	private static String getDeviceID(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Constants.KEY_PREFS_FILE, Context.MODE_PRIVATE);
		return prefs.getString(Constants.KEY_DEVICE_ID, "-1");
	}
	
	// Helper to check for successful response code
	private static boolean checkResponse(int expectedResponseCode, HttpResponse response) {
		if (response == null) {
			Log.e(ERROR_TAG, "Connection to API failed");
			return false;
		} else if (response.getStatusLine().getStatusCode() != expectedResponseCode) {
			Log.e(ERROR_TAG, "Got http status code: " + response.getStatusLine().getStatusCode());
			return false;
		}
		return true;
	}
	
	// Helper to get the API URL
	private static String getApiUrl(Context context) {
		String url = Utilities.getSharedPrefString(context, Constants.KEY_SERVER_ADDRESS);
		if (url.equalsIgnoreCase(Utilities.SP_DEFAULT_STRING)) {
			url = Constants.DEFAULT_API_URL;
		}
		return url;
	}
}