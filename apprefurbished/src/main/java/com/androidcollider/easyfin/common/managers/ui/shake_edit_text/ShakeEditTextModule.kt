package com.androidcollider.easyfin.common.managers.ui.shake_edit_text

import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class ShakeEditTextModule {
    @Provides
    fun provideShakeEditTextManager(): ShakeEditTextManager {
        return ShakeEditTextManager()
    }
}