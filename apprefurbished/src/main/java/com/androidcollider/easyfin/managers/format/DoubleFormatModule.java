package com.androidcollider.easyfin.managers.format;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class DoubleFormatModule {

    @Provides
    public DoubleFormatManager provideDoubleFormatManager() {
        return new DoubleFormatManager();
    }
}