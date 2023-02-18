package com.androidcollider.easyfin.common.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Ihor Bilous
 */
@Module
internal class AppModule(private val application: Application) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return application
    }
}