package com.androidcollider.easyfin.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.DateConstants;
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;
import java.util.Date;


public class DataSource {
    //private final static String TAG = "Андроідний Коллайдер";
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

    //Open database to write
    public void openLocalToWrite() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    //Open database to read
    public void openLocalToRead() throws SQLException {
        db = dbHelper.getReadableDatabase();
    }

    //Close database
    public void closeLocal() {
        db.close();
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
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        int id_account = transaction.getId_account();

        cv1.put("id_account", id_account);
        cv1.put("date", transaction.getDate());
        cv1.put("amount", transaction.getAmount());
        cv1.put("category", transaction.getCategory());
        cv1.put("currency", transaction.getCurrency());

        cv2.put("amount", transaction.getAccount_amount());

        openLocalToWrite();
        db.insert("Transactions", null, cv1);
        db.update("Account", cv2, "id_account = " + id_account, null);
        closeLocal();
    }



    public void updateAccountsAmountAfterTransfer(int id_account_1, double amount_1, int id_account_2, double amount_2) {
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        cv1.put("amount", amount_1);
        cv2.put("amount", amount_2);

        openLocalToWrite();

        db.update("Account", cv1, "id_account = " + id_account_1, null);
        db.update("Account", cv2, "id_account = " + id_account_2, null);

        closeLocal();

    }

    public ArrayList<String> getAllAccountNames() {
        ArrayList<String> accounts = new ArrayList<>();
        String selectQuery = "SELECT name FROM Account WHERE visibility = 1 ";
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



    public double[] getTransactionsStatistic(int position, String currency) {
        double[] arrayStatistic = new double[2];

        long period = 0;

        DateConstants dateConstants = new DateConstants();

        switch (position) {
            case 1: period = dateConstants.getDay(); break;
            case 2: period = dateConstants.getWeek(); break;
            case 3: period = dateConstants.getMonth(); break;
            case 4: period = dateConstants.getYear(); break;
        }

        String selectQuery = "SELECT date, amount FROM Transactions "
                               + "WHERE currency = '" + currency + "' ";

        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        double cost = 0.0;
        double income = 0.0;

        long currentTime = new Date().getTime();

        if (cursor.moveToFirst()) {
            int amountColIndex = cursor.getColumnIndex("amount");
            int dateColIndex = cursor.getColumnIndex("date");

            for (int i=cursor.getCount()-1; i>=0;i--){
                cursor.moveToPosition(i);

                long date = cursor.getLong(dateColIndex);
                double amount = cursor.getDouble(amountColIndex);

                if (currentTime > date && period >= (currentTime - date)) {

                    if (FormatUtils.isDoubleNegative(amount)) {
                        cost += amount;
                    }

                    else {
                        income += amount;
                    }
                }
            }
            cursor.close();
            closeLocal();

            arrayStatistic[0] = cost;
            arrayStatistic[1] = income;
            return arrayStatistic;
        }

        closeLocal();
        cursor.close();

        arrayStatistic[0] = cost;
        arrayStatistic[1] = income;

        return arrayStatistic;
    }



    public double getAccountsSumGroupByCurrency(String type, String currency) {
        String selectQuery = "SELECT SUM(amount) FROM Account "
                + "WHERE visibility = 1 AND "
                + "type = '" + type + "' AND "
                + "currency = '" + currency + "' ";

        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            double d = cursor.getDouble(0);
            cursor.close();
            closeLocal();
            return d;}

        cursor.close();
        closeLocal();
        return 0;
    }



    public ArrayList<Account> getAllAccountsInfo() {

        ArrayList<Account> accountArrayList = new ArrayList<>();

        String selectQuery = "SELECT * FROM Account WHERE visibility = 1";
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

        String selectQuery = "SELECT t.amount, date, category, name, type, t.currency "
                + "FROM Transactions t, Account a "
                + "WHERE t.id_account = a.id_account ";

        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int amountColIndex = cursor.getColumnIndex("amount");
            int dateColIndex = cursor.getColumnIndex("date");
            int categoryColIndex = cursor.getColumnIndex("category");
            int nameColIndex = cursor.getColumnIndex("name");
            int currencyColIndex = cursor.getColumnIndex("currency");
            int typeColIndex = cursor.getColumnIndex("type");

            for (int i=cursor.getCount()-1; i>=0;i--){
                cursor.moveToPosition(i);
                Transaction transaction = new Transaction(
                        cursor.getLong(dateColIndex),
                        cursor.getDouble(amountColIndex),
                        cursor.getString(categoryColIndex),
                        cursor.getString(nameColIndex),
                        cursor.getString(currencyColIndex),
                        cursor.getString(typeColIndex));

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




    public void editAccount(Account account) {
        ContentValues cv = new ContentValues();

        cv.put("name", account.getName());
        cv.put("amount", account.getAmount());
        cv.put("type", account.getType());
        cv.put("currency", account.getCurrency());

        int id = account.getId();

        openLocalToWrite();

        db.update("Account", cv, "id_account = '" + id + "' ", null);

        closeLocal();
    }

    public void deleteAccount(int id) {
        openLocalToWrite();

        db.delete("Account", "id_account = '" + id + "' ", null);

        closeLocal();
    }

    public void makeAccountInvisible(int id) {

        ContentValues cv = new ContentValues();
        cv.put("visibility", 0);

        openLocalToWrite();

        db.update("Account", cv, "id_account = '" + id + "' ", null);
    }


    public boolean checkAccountNameMatches(String name) {
        ArrayList<String> accounts = getAllAccountNames();

        for (String account : accounts) {
            if (account.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkAccountTransactionExist(int id) {

        String selectQuery = "SELECT COUNT(id_transaction) FROM Transactions "
                + "WHERE id_account = '" + id +  "' ";

        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            int c = cursor.getInt(0);
            cursor.close();
            closeLocal();
            if (c > 0) {
            return true;}}

        cursor.close();
        closeLocal();

        return false;
    }


}