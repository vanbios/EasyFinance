package com.androidcollider.easyfin.managers.rates.exchange;

import com.androidcollider.easyfin.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ExchangeModule {

    @Provides
    public ExchangeManager provideExchangeManager(Repository repository) {
        return new ExchangeManager(repository);
    }
}
