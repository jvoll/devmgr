package com.android.dm.tools;

import java.util.ArrayList;

import android.app.Application;

import com.android.dm.Constants;

public class DMApplication extends Application {
	
	private DMActivity currentActivity;
	private ArrayList<MessageHandler> messageHandlers;
	
	/*
	 * This class subclasses Application and is utilized as a
	 * way for Activities and Services (DMActivity and DMService)
	 * to communicate with one another without using intents.
	 */
	public DMApplication() {
		super();
		messageHandlers = new ArrayList<MessageHandler>();
	}

	public DMActivity getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(DMActivity currentActivity) {
		this.currentActivity = currentActivity;
	}
	
	/*
	 * Location change notification service
	 */
	public void subscribeMessages(MessageHandler handler) {
		messageHandlers.add(handler);
	}
	
	public void unsubscribeMessages(MessageHandler handler) {
		messageHandlers.remove(handler);
	}
	
	public void notifyLocationChanged() {
		for (MessageHandler handler : messageHandlers) {
			handler.getHandler().sendEmptyMessage(Constants.MESSAGE_LOCATION_UPDATE);
		}
	}
}
