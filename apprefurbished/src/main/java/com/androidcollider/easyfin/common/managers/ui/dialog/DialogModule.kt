package com.androidcollider.easyfin.common.managers.ui.dialog

import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class DialogModule {
    @Provides
    fun provideDialogManager(): DialogManager {
        return DialogManager()
    }
}