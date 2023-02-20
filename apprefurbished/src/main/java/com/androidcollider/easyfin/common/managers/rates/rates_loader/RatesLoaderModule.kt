package com.androidcollider.easyfin.common.managers.rates.rates_loader;

import android.content.Context;

import com.androidcollider.easyfin.common.api.RatesApi;
import com.androidcollider.easyfin.common.managers.connection.ConnectionManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.api.ApiManager;
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager;
import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class RatesLoaderModule {

    @Provides
    RatesLoaderManager provideRatesLoaderManager(Context context, RatesApi ratesApi,
                                                 Repository repository, ConnectionManager connectionManager,
                                                 SharedPrefManager sharedPrefManager, ResourcesManager resourcesManager) {
        return new RatesLoaderManager(context, ratesApi, repository, connectionManager, sharedPrefManager, resourcesManager);
    }

    @Provides
    RatesApi getRatesApi(ApiManager apiManager) {
        return apiManager.getRetrofit().create(RatesApi.class);
    }
}