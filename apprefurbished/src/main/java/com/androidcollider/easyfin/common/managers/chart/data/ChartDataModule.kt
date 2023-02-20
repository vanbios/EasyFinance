package com.androidcollider.easyfin.common.managers.chart.data

import android.content.Context
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class ChartDataModule {
    @Provides
    fun provideChartDataManager(
        numberFormatManager: NumberFormatManager,
        resourcesManager: ResourcesManager,
        context: Context
    ): ChartDataManager {
        return ChartDataManager(numberFormatManager, resourcesManager, context)
    }
}