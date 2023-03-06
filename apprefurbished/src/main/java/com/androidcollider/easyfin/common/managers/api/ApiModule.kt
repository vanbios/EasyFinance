package com.androidcollider.easyfin.common.managers.api

import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class ApiModule {
    @Provides
    fun provideApiManager(): ApiManager {
        return ApiManager()
    }
}