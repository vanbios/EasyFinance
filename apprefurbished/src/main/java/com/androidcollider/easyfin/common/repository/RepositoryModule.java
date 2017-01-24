package com.androidcollider.easyfin.common.repository;

import android.content.Context;

import com.androidcollider.easyfin.common.repository.database.Database;
import com.androidcollider.easyfin.common.repository.database.DatabaseRepository;
import com.androidcollider.easyfin.common.repository.database.DbHelper;
import com.androidcollider.easyfin.common.repository.memory.Memory;
import com.androidcollider.easyfin.common.repository.memory.MemoryRepository;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.import_export_db.ImportExportDbManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    @Memory
    Repository provideMemoryRepository(NumberFormatManager numberFormatManager,
                                       ResourcesManager resourcesManager) {
        return new MemoryRepository(numberFormatManager, resourcesManager);
    }

    @Provides
    @Singleton
    @Database
    Repository provideDatabaseRepository(DbHelper dbHelper,
                                         SharedPrefManager sharedPrefManager,
                                         NumberFormatManager numberFormatManager,
                                         ResourcesManager resourcesManager) {
        return new DatabaseRepository(dbHelper, sharedPrefManager, numberFormatManager, resourcesManager);
    }

    @Provides
    @Singleton
    Repository provideDataRepository(@Memory Repository memoryRepository,
                                     @Database Repository databaseRepository,
                                     ImportExportDbManager importExportDbManager) {
        return new DataRepository(memoryRepository, databaseRepository, importExportDbManager);
    }

    @Provides
    @Singleton
    DbHelper provideDbHelper(Context context) {
        return new DbHelper(context);
    }
}