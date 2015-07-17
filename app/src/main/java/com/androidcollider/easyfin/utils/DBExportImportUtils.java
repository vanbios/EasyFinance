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
import java.io.OutputStream;
import java.nio.channels.FileChannel;


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

        if (!backupDirectory.mkdirs()) {
            Log.d("COLLIDER", "Folder create problem");
        }

        File backupFile = new File(backupDirectory, DbHelper.DATABASE_NAME);

        //Open the empty db as the output stream
        OutputStream output = new FileOutputStream(backupFile);

        //transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;

        while ((length = fis.read(buffer))>0){
            output.write(buffer, 0, length);
        }

        //Close the streams
        output.flush();
        output.close();
        fis.close();


        ToastUtils.showClosableToast(context,
                context.getResources().getString(R.string.db_backup_to)
                        + " " + outFilePath, 2);
    }





    /**
     * Creates the specified <code>toFile</code> as a byte for byte copy of the
     * <code>fromFile</code>. If <code>toFile</code> already exists, then it
     * will be replaced with a copy of <code>fromFile</code>. The name and path
     * of <code>toFile</code> will be that of <code>toFile</code>.<br/>
     * <br/>
     * <i> Note: <code>fromFile</code> and <code>toFile</code> will be closed by
     * this function.</i>
     *
     * @param fromFile
     *            - FileInputStream for the file to copy from.
     * @param toFile
     *            - FileInputStream for the file to copy to.
     */


    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {

        FileChannel fromChannel = null;
        FileChannel toChannel = null;

        try {

            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);

        } finally {

            try {

                if (fromChannel != null) {
                    fromChannel.close();
                }

            } finally {

                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

}
