package com.androidcollider.easyfin.managers.chart;

import com.androidcollider.easyfin.managers.format.DoubleFormatManager;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ChartDataModule {

    @Provides
    public ChartDataManager provideChartDataManager(DoubleFormatManager doubleFormatManager) {
        return new ChartDataManager(doubleFormatManager);
    }
}
