package com.androidcollider.easyfin.managers.import_db;

import android.content.Context;

import com.androidcollider.easyfin.repository.database.DbHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class ImportDbModule {

    @Provides
    @Singleton
    public ImportDbManager provideImportDbManager(Context context, DbHelper dbHelper) {
        return new ImportDbManager(context, dbHelper);
    }
}
