package com.androidcollider.easyfin.common.app;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;
import lombok.Getter;

public class App extends Application {

    @Getter
    private AppComponent component;

    private static App mInstance;

    public static GoogleAnalytics analytics;
    private final static String TRACKER_ID = "UA-65734136-1";
    private static Tracker tracker;


    public static Tracker tracker() {
        return tracker;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        Fabric.with(this, new Crashlytics());

        mInstance = this;

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(TRACKER_ID);
        tracker.enableAutoActivityTracking(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableExceptionReporting(true);
    }

    public static Context getContext() {
        return mInstance.getApplicationContext();
    }
}