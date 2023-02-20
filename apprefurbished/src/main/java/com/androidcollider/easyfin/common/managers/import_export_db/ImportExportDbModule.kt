package com.androidcollider.easyfin.common.managers.import_export_db

import android.content.Context
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.repository.database.DbHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Ihor Bilous
 */
@Module
class ImportExportDbModule {
    @Provides
    @Singleton
    fun provideImportDbManager(
        context: Context,
        dbHelper: DbHelper,
        toastManager: ToastManager
    ): ImportExportDbManager {
        return ImportExportDbManager(context, dbHelper, toastManager)
    }
}