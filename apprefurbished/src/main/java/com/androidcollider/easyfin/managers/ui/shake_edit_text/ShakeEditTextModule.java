package com.androidcollider.easyfin.managers.ui.shake_edit_text;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ShakeEditTextModule {

    @Provides
    ShakeEditTextManager provideShakeEditTextManager() {
        return new ShakeEditTextManager();
    }
}