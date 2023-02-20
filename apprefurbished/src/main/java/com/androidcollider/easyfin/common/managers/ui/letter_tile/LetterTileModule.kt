package com.androidcollider.easyfin.common.managers.ui.letter_tile;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class LetterTileModule {

    @Provides
    LetterTileManager provideLetterTileManager(Context context) {
        return new LetterTileManager(context);
    }
}