package com.androidcollider.easyfin.managers.api;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ApiModule {

    @Provides
    ApiManager provideApiManager() {
        return new ApiManager();
    }
}
