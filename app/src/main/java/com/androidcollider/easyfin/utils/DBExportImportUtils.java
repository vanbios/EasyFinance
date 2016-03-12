package com.androidcollider.easyfin.utils;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.androidcollider.easyfin.AppController;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.database.DbHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DBExportImportUtils {

    public static void backupDB() {
        try {
            backupDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void backupDatabase() throws IOException {
        Context context = AppController.getContext();
        //Open local db as the input stream
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

        ToastUtils.showClosableToast(context,
                context.getResources().getString(R.string.db_backup_to) + " " + outFilePath, 2);
    }


    public static void copyFromStream(InputStream inputStream, FileOutputStream toFile) throws IOException {
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
