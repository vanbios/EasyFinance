package com.androidcollider.easyfin.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.Transaction;

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
    public void openLocalToWrite() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    //Close database
    public void closeLocal() {
        db.close();
    }

    public void openLocalToRead() throws SQLException {
        db = dbHelper.getReadableDatabase();
    }


    public void insertNewAccount(Account account) {
        ContentValues cv = new ContentValues();

        cv.put("name", account.getName());
        cv.put("amount", account.getAmount());
        cv.put("type", account.getType());
        cv.put("currency", account.getCurrency());

        openLocalToWrite();
        db.insert("Account", null, cv);
        closeLocal();
    }

    public void insertNewTransaction(Transaction transaction) {
        ContentValues cv = new ContentValues();

        cv.put("date", transaction.getDate());
        cv.put("id_account", transaction.getId_account());
        cv.put("amount", transaction.getAmount());
        cv.put("category", transaction.getCategory());

        openLocalToWrite();
        db.insert("Transactions", null, cv);
        closeLocal();
    }

    public double getAccountAmountForTransaction(int id_account) {
        String selectQuery = "SELECT amount FROM Account WHERE id_account = '" + id_account + "' ";

        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            return cursor.getDouble(0);}

        cursor.close();
        closeLocal();

        return 0;
    }

    public void updateAccountAmountAfterTransaction(int id_account, double amount) {
        ContentValues cv = new ContentValues();

        cv.put("amount", amount);

        openLocalToWrite();

        db.update("Account", cv, "id_account = " + id_account, null);

        closeLocal();

    }

    public ArrayList<String> getAllAccountNames() {
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

    public ArrayList<Account> getAllAccountsInfo() {

        ArrayList<Account> accountArrayList = new ArrayList<>();

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
                Account account = new Account(
                        cursor.getInt(idColIndex),
                        cursor.getString(nameColIndex),
                        cursor.getDouble(amountColIndex),
                        cursor.getString(typeColIndex),
                        cursor.getString(currencyColIndex));

                accountArrayList.add(account);
            }
            while (cursor.moveToNext());

            cursor.close();
            closeLocal();

            return accountArrayList;
        }

        cursor.close();
        closeLocal();

        return accountArrayList;
    }

    public ArrayList<Transaction> getAllTransactionsInfo(){
        ArrayList<Transaction> transactionArrayList = new ArrayList<>();

        String selectQuery = "SELECT T.amount, date, category, name, currency FROM Account A, Transactions T " +
                "WHERE T.id_account = A.id_account ";

        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int amountColIndex = cursor.getColumnIndex("amount");
            int dateColIndex = cursor.getColumnIndex("date");
            int categoryColIndex = cursor.getColumnIndex("category");
            int nameColIndex = cursor.getColumnIndex("name");
            int currencyColIndex = cursor.getColumnIndex("currency");

            for (int i=cursor.getCount()-1; i>=0;i--){
                cursor.moveToPosition(i);
                Transaction transaction = new Transaction(
                        cursor.getLong(dateColIndex),
                        cursor.getDouble(amountColIndex),
                        cursor.getString(categoryColIndex),
                        cursor.getString(nameColIndex),
                        cursor.getString(currencyColIndex));

                transactionArrayList.add(transaction);
            }
            cursor.close();
            closeLocal();
            return transactionArrayList;
        }
        cursor.close();
        closeLocal();

        return transactionArrayList;
    }


}
