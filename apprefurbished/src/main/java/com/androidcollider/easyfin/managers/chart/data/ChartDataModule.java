package com.androidcollider.easyfin.managers.chart.data;

import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.managers.resources.ResourcesManager;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ChartDataModule {

    @Provides
    ChartDataManager provideChartDataManager(NumberFormatManager numberFormatManager, ResourcesManager resourcesManager) {
        return new ChartDataManager(numberFormatManager, resourcesManager);
    }
}