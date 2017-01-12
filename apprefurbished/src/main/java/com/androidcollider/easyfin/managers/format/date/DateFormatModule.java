package com.androidcollider.easyfin.managers.format.date;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class DateFormatModule {

    @Provides
    public DateFormatManager provideDateProvideManager() {
        return new DateFormatManager();
    }
}