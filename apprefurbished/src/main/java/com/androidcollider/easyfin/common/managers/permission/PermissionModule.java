package com.androidcollider.easyfin.common.managers.permission;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class PermissionModule {

    @Provides
    PermissionManager providePermissionManager() {
        return new PermissionManager();
    }
}