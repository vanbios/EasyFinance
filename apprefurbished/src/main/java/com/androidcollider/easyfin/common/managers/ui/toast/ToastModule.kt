package com.androidcollider.easyfin.common.managers.ui.toast

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Ihor Bilous
 */
@Module
class ToastModule {
    @Provides
    @Singleton
    fun provideToastManager(): ToastManager {
        return ToastManager()
    }
}