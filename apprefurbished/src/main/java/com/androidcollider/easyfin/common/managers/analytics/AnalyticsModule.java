package com.androidcollider.easyfin.common.managers.analytics;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class AnalyticsModule {

    @Provides
    @Singleton
    AnalyticsManager provideAnalyticsManager(Context context) {
        return new AnalyticsManager(context);
    }
}