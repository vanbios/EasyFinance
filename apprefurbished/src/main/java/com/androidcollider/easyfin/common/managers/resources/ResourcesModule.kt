package com.androidcollider.easyfin.common.managers.resources

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Ihor Bilous
 */
@Module
class ResourcesModule {
    @Provides
    @Singleton
    fun provideResourcesManager(context: Context): ResourcesManager {
        return ResourcesManager(context)
    }
}