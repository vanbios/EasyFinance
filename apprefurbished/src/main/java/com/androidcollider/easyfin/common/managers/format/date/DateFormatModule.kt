package com.androidcollider.easyfin.common.managers.format.date

import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class DateFormatModule {
    @Provides
    fun provideDateProvideManager(): DateFormatManager {
        return DateFormatManager()
    }
}