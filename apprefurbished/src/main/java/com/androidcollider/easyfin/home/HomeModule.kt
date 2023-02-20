package com.androidcollider.easyfin.home

import com.androidcollider.easyfin.common.managers.chart.data.ChartDataManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class HomeModule {
    @Provides
    fun provideHomeMVPPresenter(
        model: HomeMVP.Model,
        resourcesManager: ResourcesManager,
        numberFormatManager: NumberFormatManager,
        exchangeManager: ExchangeManager,
        chartDataManager: ChartDataManager
    ): HomeMVP.Presenter {
        return HomePresenter(
            model,
            resourcesManager,
            numberFormatManager,
            exchangeManager,
            chartDataManager
        )
    }

    @Provides
    fun provideHomeMVPModel(repository: Repository): HomeMVP.Model {
        return HomeModel(repository)
    }
}