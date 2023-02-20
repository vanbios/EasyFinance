package com.androidcollider.easyfin.common.managers.shared_pref

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Ihor Bilous
 */
@Module
class SharedPrefModule {
    @Provides
    @Singleton
    fun provideSharedPrefManager(context: Context): SharedPrefManager {
        return SharedPrefManager(context)
    }
}