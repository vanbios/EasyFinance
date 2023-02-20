package com.androidcollider.easyfin.common.managers.format.number;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class NumberFormatModule {

    @Provides
    NumberFormatManager provideDoubleFormatManager() {
        return new NumberFormatManager();
    }
}