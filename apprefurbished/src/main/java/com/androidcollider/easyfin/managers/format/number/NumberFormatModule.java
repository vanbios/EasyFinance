package com.androidcollider.easyfin.managers.format.number;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class NumberFormatModule {

    @Provides
    public NumberFormatManager provideDoubleFormatManager() {
        return new NumberFormatManager();
    }
}