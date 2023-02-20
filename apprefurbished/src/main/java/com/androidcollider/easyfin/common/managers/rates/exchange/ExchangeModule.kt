package com.androidcollider.easyfin.common.managers.rates.exchange

import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Ihor Bilous
 */
@Module
class ExchangeModule {
    @Provides
    @Singleton
    fun provideExchangeManager(
        repository: Repository,
        resourcesManager: ResourcesManager
    ): ExchangeManager {
        return ExchangeManager(repository, resourcesManager)
    }
}