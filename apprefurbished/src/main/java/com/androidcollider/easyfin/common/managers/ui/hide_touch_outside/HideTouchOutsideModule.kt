package com.androidcollider.easyfin.common.managers.ui.hide_touch_outside

import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class HideTouchOutsideModule {
    @Provides
    fun provideHideTouchOutsideManager(): HideTouchOutsideManager {
        return HideTouchOutsideManager()
    }
}