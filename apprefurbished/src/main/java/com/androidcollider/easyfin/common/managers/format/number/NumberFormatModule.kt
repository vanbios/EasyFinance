package com.androidcollider.easyfin.common.managers.format.number

import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class NumberFormatModule {
    @Provides
    fun provideDoubleFormatManager(): NumberFormatManager {
        return NumberFormatManager()
    }
}