package com.androidcollider.easyfin.home;

import com.androidcollider.easyfin.common.managers.chart.data.ChartDataManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class HomeModule {

    @Provides
    HomeMVP.Presenter provideHomeMVPPresenter(HomeMVP.Model model,
                                              ResourcesManager resourcesManager,
                                              NumberFormatManager numberFormatManager,
                                              ExchangeManager exchangeManager,
                                              ChartDataManager chartDataManager) {
        return new HomePresenter(model, resourcesManager, numberFormatManager, exchangeManager, chartDataManager);
    }

    @Provides
    HomeMVP.Model provideHomeMVPModel(Repository repository) {
        return new HomeModel(repository);
    }
}