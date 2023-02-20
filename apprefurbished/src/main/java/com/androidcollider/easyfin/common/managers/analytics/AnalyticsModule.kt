package com.androidcollider.easyfin.common.managers.analytics

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Ihor Bilous
 */
@Module
class AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalyticsManager(context: Context): AnalyticsManager {
        return AnalyticsManager(context)
    }
}