package com.androidcollider.easyfin.common.managers.rates.exchange;

import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.repository.Repository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ExchangeModule {

    @Provides
    @Singleton
    ExchangeManager provideExchangeManager(Repository repository, ResourcesManager resourcesManager) {
        return new ExchangeManager(repository, resourcesManager);
    }
}