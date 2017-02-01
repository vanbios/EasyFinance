package com.androidcollider.easyfin.common.managers.shared_pref;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class SharedPrefModule {

    @Provides
    @Singleton
    SharedPrefManager provideSharedPrefManager(Context context) {
        return new SharedPrefManager(context);
    }
}