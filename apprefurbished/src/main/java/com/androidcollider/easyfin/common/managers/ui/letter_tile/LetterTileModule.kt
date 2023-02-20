package com.androidcollider.easyfin.common.managers.ui.letter_tile

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class LetterTileModule {
    @Provides
    fun provideLetterTileManager(context: Context): LetterTileManager {
        return LetterTileManager(context)
    }
}