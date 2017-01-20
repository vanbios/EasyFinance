package com.androidcollider.easyfin.managers.import_export_db;

import android.content.Context;

import com.androidcollider.easyfin.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.repository.database.DbHelper;

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
