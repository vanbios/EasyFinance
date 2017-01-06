package com.androidcollider.easyfin.managers.import_export_db;

import android.content.Context;

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
    public ImportExportDbManager provideImportDbManager(Context context, DbHelper dbHelper) {
        return new ImportExportDbManager(context, dbHelper);
    }
}
