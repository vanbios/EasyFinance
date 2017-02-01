package com.androidcollider.easyfin.common.managers.resources;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ResourcesModule {

    @Provides
    @Singleton
    ResourcesManager provideResourcesManager(Context context) {
        return new ResourcesManager(context);
    }
}