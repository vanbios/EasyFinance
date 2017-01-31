package com.androidcollider.easyfin.common.managers.analytics;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * @author Ihor Bilous
 */

public class AnalyticsManager {

    public GoogleAnalytics analytics;
    private final static String TRACKER_ID = "UA-65734136-1";
    private Tracker tracker;

    AnalyticsManager(Context context) {
        analytics = GoogleAnalytics.getInstance(context);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(TRACKER_ID);
        tracker.enableAutoActivityTracking(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableExceptionReporting(true);
    }

    public void sendScreeName(String screenName) {
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendAction(String s, String s1, String label) {
        tracker.send(new HitBuilders.EventBuilder(s, s1)
                .setLabel(label)
                .build());
    }
}