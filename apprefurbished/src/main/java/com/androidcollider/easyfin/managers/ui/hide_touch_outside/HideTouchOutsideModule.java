package com.androidcollider.easyfin.managers.ui.hide_touch_outside;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class HideTouchOutsideModule {

    @Provides
    public HideTouchOutsideManager provideHideTouchOutsideManager() {
        return new HideTouchOutsideManager();
    }
}