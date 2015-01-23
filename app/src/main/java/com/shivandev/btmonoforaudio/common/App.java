package com.shivandev.btmonoforaudio.common;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.shivandev.btmonoforaudio.R;

public class App extends Application {
	private Tracker mTrackers;

	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
	}

	public static Context getContext() {
		return context;
	}

	public synchronized Tracker getTracker() {
		if (mTrackers == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(getContext());
			mTrackers =  analytics.newTracker(R.xml.app_tracker);
		}
		return mTrackers;
	}

	public void sendTrackInfo(String screenName) {
		// Get tracker.
		Tracker t = getTracker();
		// Set screen name. Where path is a String representing the screen name.
		t.setScreenName(screenName);
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
	}
}
