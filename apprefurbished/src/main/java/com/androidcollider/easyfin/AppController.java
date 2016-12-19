package com.androidcollider.easyfin;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

public class AppController extends Application {

    private static AppController mInstance;

    public static GoogleAnalytics analytics;
    private final static String TRACKER_ID = "UA-65734136-1";
    private static Tracker tracker;


    public static Tracker tracker() {
        return tracker;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        mInstance = this;

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(TRACKER_ID);
        tracker.enableAutoActivityTracking(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableExceptionReporting(true);
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }


    public static Context getContext() {
        return mInstance.getApplicationContext();
    }

}