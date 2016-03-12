package com.androidcollider.easyfin;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AppController extends Application {

    public static final String AppTAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppController mInstance;

    public static GoogleAnalytics analytics;
    private final static String TRACKER_ID ="UA-65734136-1";
    private static Tracker tracker;


    public static Tracker tracker() {
        return tracker;
    }


    @Override
    public void onCreate() {
        super.onCreate();

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

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? AppTAG : tag);
        getRequestQueue().add(req);
    }


    public static Context getContext() {
        return mInstance.getApplicationContext();
    }

}