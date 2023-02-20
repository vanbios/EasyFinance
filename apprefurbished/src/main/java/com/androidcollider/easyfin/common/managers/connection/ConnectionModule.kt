package com.androidcollider.easyfin.common.managers.connection

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class ConnectionModule {
    @Provides
    fun provideConnectionManager(context: Context): ConnectionManager {
        return ConnectionManager(context)
    }
}