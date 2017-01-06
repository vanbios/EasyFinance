package com.androidcollider.easyfin.managers.import_db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.androidcollider.easyfin.repository.database.DbHelper;
import com.androidcollider.easyfin.utils.DBExportImportUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ihor Bilous
 */

public class ImportDbManager {

    private DbHelper dbHelper;
    private Context context;
    private SQLiteDatabase db;


    ImportDbManager(Context context, DbHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    public boolean importDatabase(Uri uri) throws IOException {
        // Close the SQLiteOpenHelper so it will commit the created empty database to internal storage.
        dbHelper.close();

        File oldDb = context.getDatabasePath(DbHelper.DATABASE_NAME);

        InputStream newDbStream = context.getContentResolver().openInputStream(uri);

        if (newDbStream != null) {
            DBExportImportUtils.copyFromStream(newDbStream, new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper will cache it and mark it as created.
            openLocalToWrite();
            closeLocal();
            return true;
        }
        return false;
    }

    //Open database to write
    private void openLocalToWrite() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    //Close database
    private void closeLocal() {
        db.close();
    }
}