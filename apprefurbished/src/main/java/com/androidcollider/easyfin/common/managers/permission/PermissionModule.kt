package com.androidcollider.easyfin.common.managers.permission

import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class PermissionModule {
    @Provides
    fun providePermissionManager(): PermissionManager {
        return PermissionManager()
    }
}