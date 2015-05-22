package com.androidcollider.easyfin.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.androidcollider.easyfin.objects.AccountInfo;

import java.util.ArrayList;


public class DataSource {
    private final static String TAG = "Андроідний Коллайдер";
    private final static String APP_PREFERENCES = "EasyfinPref";

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
    private SharedPreferences sPref;

    public DataSource(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
        sPref = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    //Open database
    public void openLocal() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    //Close database
    public void closeLocal() {
        db.close();
    }

    public void openLocalToRead() throws SQLException {
        db = dbHelper.getReadableDatabase();
    }

    public void insertLocal(String tableName, ContentValues cv) {
        openLocal();
        db.insert (tableName, null, cv);
        closeLocal();
    }

    public ArrayList<String> getAllAccounts() {
        ArrayList<String> accounts = new ArrayList<>();
        String selectQuery = "SELECT name FROM Account ";
        openLocalToRead();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int nameColIndex = cursor.getColumnIndex("name");

        if (cursor.moveToFirst()) {
            do {
                accounts.add(cursor.getString(nameColIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        closeLocal();
        return accounts;
    }




    public double getExpenseSum(String type) {
        String selectQuery = "SELECT SUM(amount) FROM Account WHERE type = '" + type + "' ";
        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            return cursor.getDouble(0);}

        cursor.close();
        closeLocal();
        return 0;
    }

    public ArrayList<AccountInfo> getAllAccountsInfo() {

        ArrayList<AccountInfo> accountInfoArrayList = new ArrayList<>();

        String selectQuery = "SELECT * FROM Account ";
        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id_account");
            int nameColIndex = cursor.getColumnIndex("name");
            int amountColIndex = cursor.getColumnIndex("amount");
            int typeColIndex = cursor.getColumnIndex("type");
            int currencyColIndex = cursor.getColumnIndex("currency");

            do {
                AccountInfo accountInfo = new AccountInfo(
                        cursor.getInt(idColIndex),
                        cursor.getString(nameColIndex),
                        cursor.getDouble(amountColIndex),
                        cursor.getString(typeColIndex),
                        cursor.getString(currencyColIndex));

                accountInfoArrayList.add(accountInfo);

            }
            while (cursor.moveToNext());

            cursor.close();
            closeLocal();

            return accountInfoArrayList;
        }

        cursor.close();
        closeLocal();

        return accountInfoArrayList;
    }


}
