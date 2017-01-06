package com.androidcollider.easyfin.managers.rates.rates_loader;

import android.content.Context;

import com.androidcollider.easyfin.common.api.RatesApi;
import com.androidcollider.easyfin.managers.api.ApiManager;
import com.androidcollider.easyfin.managers.connection.ConnectionManager;
import com.androidcollider.easyfin.managers.shared_pref.SharedPrefManager;
import com.androidcollider.easyfin.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class RatesLoaderModule {

    @Provides
    public RatesLoaderManager provideRatesLoaderManager(Context context, RatesApi ratesApi,
                                                        Repository repository, ConnectionManager connectionManager,
                                                        SharedPrefManager sharedPrefManager) {
        return new RatesLoaderManager(context, ratesApi, repository, connectionManager, sharedPrefManager);
    }

    @Provides
    public RatesApi getRatesApi(ApiManager apiManager) {
        return apiManager.getRetrofit().create(RatesApi.class);
    }
}