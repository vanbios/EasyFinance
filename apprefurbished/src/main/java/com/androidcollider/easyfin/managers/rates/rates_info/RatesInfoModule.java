package com.androidcollider.easyfin.managers.rates.rates_info;

import android.content.Context;

import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class RatesInfoModule {

    @Provides
    public RatesInfoManager provideRatesInfoManager(Context context, Repository repository,
                                                    ToastManager toastManager, ResourcesManager resourcesManager) {
        return new RatesInfoManager(context, repository, toastManager, resourcesManager);
    }
}