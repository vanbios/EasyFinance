package com.androidcollider.easyfin.managers.rates.rates_info;

import android.content.Context;

import com.androidcollider.easyfin.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class RatesInfoModule {

    @Provides
    public RatesInfoManager provideRatesInfoManager(Context context, Repository repository) {
        return new RatesInfoManager(context, repository);
    }
}
