package com.androidcollider.easyfin.repository;

import android.content.Context;

import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.managers.shared_pref.SharedPrefManager;
import com.androidcollider.easyfin.repository.database.Database;
import com.androidcollider.easyfin.repository.database.DatabaseRepository;
import com.androidcollider.easyfin.repository.database.DbHelper;
import com.androidcollider.easyfin.repository.memory.Memory;
import com.androidcollider.easyfin.repository.memory.MemoryRepository;

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
    public Repository provideMemoryRepository(Context context, NumberFormatManager numberFormatManager, ResourcesManager resourcesManager) {
        return new MemoryRepository(numberFormatManager, resourcesManager);
    }

    @Provides
    @Singleton
    @Database
    public Repository provideDatabaseRepository(Context context, DbHelper dbHelper,
                                                SharedPrefManager sharedPrefManager, NumberFormatManager numberFormatManager,
                                                ResourcesManager resourcesManager) {
        return new DatabaseRepository(dbHelper, sharedPrefManager, numberFormatManager, resourcesManager);
    }

    @Provides
    @Singleton
    public Repository provideDataRepository(@Memory Repository memoryRepository,
                                            @Database Repository databaseRepository) {
        return new DataRepository(memoryRepository, databaseRepository);
    }

    @Provides
    @Singleton
    public DbHelper provideDbHelper(Context context) {
        return new DbHelper(context);
    }
}