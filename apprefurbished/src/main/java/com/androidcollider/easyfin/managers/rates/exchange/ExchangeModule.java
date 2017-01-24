package com.androidcollider.easyfin.managers.rates.exchange;

import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.repository.Repository;

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