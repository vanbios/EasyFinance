package com.androidcollider.easyfin.common.managers.ui.toast;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ToastModule {

    @Provides
    @Singleton
    ToastManager provideToastManager() {
        return new ToastManager();
    }
}