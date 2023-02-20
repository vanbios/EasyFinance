package com.androidcollider.easyfin.common.managers.ui.dialog;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class DialogModule {

    @Provides
    DialogManager provideDialogManager() {
        return new DialogManager();
    }
}