package com.androidcollider.easyfin.common.managers.rates.rates_info

import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Ihor Bilous
 */
@Module
class RatesInfoModule {
    @Provides
    @Singleton
    fun provideRatesInfoManager(
        repository: Repository,
        toastManager: ToastManager,
        resourcesManager: ResourcesManager
    ): RatesInfoManager {
        return RatesInfoManager(repository, toastManager, resourcesManager)
    }
}