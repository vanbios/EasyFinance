package com.androidcollider.easyfin.common.managers.ui.hide_touch_outside;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class HideTouchOutsideModule {

    @Provides
    HideTouchOutsideManager provideHideTouchOutsideManager() {
        return new HideTouchOutsideManager();
    }
}