package com.androidcollider.easyfin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androidcollider.easyfin.utils.DBExportImportUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class DbHelper extends SQLiteOpenHelper {

    Context context;
    public static final String DATABASE_NAME = "FinU.db";
    private static final int DATABASE_VERSION = 1;

    public static String DB_FILEPATH = "/data/data/com.androidcollider.easyfin/databases/" + DATABASE_NAME;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    public boolean importDatabase(String dbPath) throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File newDb = new File(dbPath);
        File oldDb = new File(DB_FILEPATH);
        if (newDb.exists()) {
            DBExportImportUtils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SqlQueries.create_account_table);
        db.execSQL(SqlQueries.create_transactions_table);
        db.execSQL(SqlQueries.create_debt_table);
        db.execSQL(SqlQueries.create_rates_table);
    }

    // Method for update database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}
