package com.androidcollider.easyfin;


import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;


@ReportsCrashes (
        formKey = "",
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri ="http://560671.acolider.web.hosting-test.net/MAB-LAB-master/MAB-LAB-master/report/report.php"
)


public class AppController extends Application {

    public static final String AppTAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppController mInstance;

    public static GoogleAnalytics analytics;
    public final static String TRACKER_ID ="UA-65109395-1";
    public static Tracker tracker;


    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        mInstance = this;

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker(TRACKER_ID);
        tracker.enableAutoActivityTracking(true);
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

    /*public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(AppTAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }*/



    public static Context getContext() {
        return mInstance.getApplicationContext();
    }

}