package com.androidcollider.easyfin.common.managers.chart.setup

import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class ChartSetupModule {
    @Provides
    fun provideChartSetupManager(resourcesManager: ResourcesManager): ChartSetupManager {
        return ChartSetupManager(resourcesManager)
    }
}