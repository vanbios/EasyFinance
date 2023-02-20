package com.androidcollider.easyfin.common.managers.chart.data;

import android.content.Context;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ChartDataModule {

    @Provides
    ChartDataManager provideChartDataManager(NumberFormatManager numberFormatManager, ResourcesManager resourcesManager, Context context) {
        return new ChartDataManager(numberFormatManager, resourcesManager, context);
    }
}