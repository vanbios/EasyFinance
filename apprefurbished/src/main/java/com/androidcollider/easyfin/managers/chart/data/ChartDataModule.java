package com.androidcollider.easyfin.managers.chart.data;

import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ChartDataModule {

    @Provides
    public ChartDataManager provideChartDataManager(NumberFormatManager numberFormatManager) {
        return new ChartDataManager(numberFormatManager);
    }
}