package com.androidcollider.easyfin.managers.connection;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ConnectionModule {

    @Provides
    ConnectionManager provideConnectionManager(Context context) {
        return new ConnectionManager(context);
    }
}
