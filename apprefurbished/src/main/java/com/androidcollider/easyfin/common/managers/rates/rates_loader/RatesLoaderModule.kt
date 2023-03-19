package com.androidcollider.easyfin.common.managers.rates.rates_loader

import android.content.Context
import com.androidcollider.easyfin.common.api.RatesApi
import com.androidcollider.easyfin.common.managers.api.ApiManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class RatesLoaderModule {
    @Provides
    fun provideRatesLoaderManager(
        context: Context,
        ratesApi: RatesApi,
        repository: Repository,
        sharedPrefManager: SharedPrefManager,
        resourcesManager: ResourcesManager
    ): RatesLoaderManager {
        return RatesLoaderManager(
            context,
            ratesApi,
            repository,
            sharedPrefManager,
            resourcesManager
        )
    }

    @Provides
    fun getRatesApi(apiManager: ApiManager): RatesApi {
        return apiManager.retrofit.create(RatesApi::class.java)
    }
}