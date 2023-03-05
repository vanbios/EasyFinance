package com.androidcollider.easyfin.common.repository

import android.content.Context
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.import_export_db.ImportExportDbManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager
import com.androidcollider.easyfin.common.repository.database.Database
import com.androidcollider.easyfin.common.repository.database.DatabaseRepository
import com.androidcollider.easyfin.common.repository.database.DbHelper
import com.androidcollider.easyfin.common.repository.memory.Memory
import com.androidcollider.easyfin.common.repository.memory.MemoryRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Ihor Bilous
 */
@Module
class RepositoryModule {
    @Provides
    @Singleton
    @Memory
    fun provideMemoryRepository(
        numberFormatManager: NumberFormatManager,
        resourcesManager: ResourcesManager
    ): Repository {
        return MemoryRepository(numberFormatManager, resourcesManager)
    }

    @Provides
    @Singleton
    @Database
    fun provideDatabaseRepository(
        dbHelper: DbHelper,
        sharedPrefManager: SharedPrefManager,
        numberFormatManager: NumberFormatManager,
        resourcesManager: ResourcesManager
    ): Repository {
        return DatabaseRepository(
            dbHelper,
            sharedPrefManager,
            numberFormatManager,
            resourcesManager
        )
    }

    @Provides
    @Singleton
    fun provideDataRepository(
        @Memory memoryRepository: Repository,
        @Database databaseRepository: Repository,
        importExportDbManager: ImportExportDbManager
    ): Repository {
        return DataRepository(memoryRepository, databaseRepository, importExportDbManager)
    }

    @Provides
    @Singleton
    fun provideDbHelper(context: Context): DbHelper {
        return DbHelper(context)
    }
}