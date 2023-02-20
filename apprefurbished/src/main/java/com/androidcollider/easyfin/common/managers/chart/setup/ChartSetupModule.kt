package com.androidcollider.easyfin.common.managers.chart.setup;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ChartSetupModule {

    @Provides
    ChartSetupManager provideChartSetupManager(Context context) {
        return new ChartSetupManager(context);
    }
}