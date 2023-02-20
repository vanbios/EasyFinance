package com.androidcollider.easyfin.common.managers.rates.rates_info;

import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.repository.Repository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class RatesInfoModule {

    @Provides
    @Singleton
    RatesInfoManager provideRatesInfoManager(Repository repository,
                                             ToastManager toastManager,
                                             ResourcesManager resourcesManager) {
        return new RatesInfoManager(repository, toastManager, resourcesManager);
    }
}