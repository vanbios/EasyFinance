package com.androidcollider.easyfin.common.managers.import_export_db;

import android.content.Context;

import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.repository.database.DbHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ImportExportDbModule {

    @Provides
    @Singleton
    ImportExportDbManager provideImportDbManager(Context context, DbHelper dbHelper, ToastManager toastManager) {
        return new ImportExportDbManager(context, dbHelper, toastManager);
    }
}
