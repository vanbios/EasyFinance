package com.androidcollider.easyfin.managers.rates.rates_info;

import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.repository.Repository;

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