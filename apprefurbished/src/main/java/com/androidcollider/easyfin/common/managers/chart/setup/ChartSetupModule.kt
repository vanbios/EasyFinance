package com.androidcollider.easyfin.common.managers.chart.setup

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class ChartSetupModule {
    @Provides
    fun provideChartSetupManager(context: Context): ChartSetupManager {
        return ChartSetupManager(context)
    }
}