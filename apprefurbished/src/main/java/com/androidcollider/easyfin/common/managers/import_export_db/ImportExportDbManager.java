package com.androidcollider.easyfin.common.managers.import_export_db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.repository.database.DbHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Ihor Bilous
 */

public class ImportExportDbManager {

    private final DbHelper dbHelper;
    private final Context context;
    private SQLiteDatabase db;
    private final ToastManager toastManager;

    private boolean isDBExpired;


    ImportExportDbManager(Context context, DbHelper dbHelper, ToastManager toastManager) {
        this.context = context;
        this.dbHelper = dbHelper;
        this.toastManager = toastManager;
    }

    public boolean importDatabase(Uri uri) throws IOException {
        // Close the SQLiteOpenHelper so it will commit the created empty database to internal storage.
        dbHelper.close();

        File oldDb = context.getDatabasePath(DbHelper.DATABASE_NAME);

        InputStream newDbStream = context.getContentResolver().openInputStream(uri);

        if (newDbStream != null) {
            copyFromStream(newDbStream, new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper will cache it and mark it as created.
            openLocalToWrite();
            closeLocal();
            setDBExpired(true);
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

    public boolean isDBExpired() {
        return isDBExpired;
    }

    public void setDBExpired(boolean dbExpired) {
        isDBExpired = dbExpired;
    }

    public void backupDatabase() {
        //Open local db as the input stream
        try {
            File dbFile = context.getDatabasePath(DbHelper.DATABASE_NAME);
            FileInputStream fis = new FileInputStream(dbFile);

            int stringId = context.getApplicationInfo().labelRes;
            String appName = context.getString(stringId);

            String outFilePath = Environment.getExternalStorageDirectory()
                    + "/" + appName + "/data/database/";

            File backupDirectory = new File(outFilePath);

            if (!backupDirectory.mkdirs())
                Log.d("COLLIDER", "Folder is already exist");

            File backupFile = new File(backupDirectory, DbHelper.DATABASE_NAME);

            //Open the empty db as the output stream
            OutputStream output = new FileOutputStream(backupFile);

            //transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;

            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            //Close the streams
            output.flush();
            output.close();
            fis.close();

            toastManager.showClosableToast(context,
                    context.getResources().getString(R.string.db_backup_to) + " " + outFilePath, ToastManager.LONG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void copyFromStream(InputStream inputStream, FileOutputStream toFile) {
        try {
            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                toFile.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (toFile != null) {
                try {
                    toFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}