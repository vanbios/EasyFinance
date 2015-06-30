package com.androidcollider.easyfin.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.DateConstants;
import com.androidcollider.easyfin.objects.Debt;
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;
import java.util.Date;


public class DataSource {

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

        int id_account = transaction.getIdAccount();

        cv1.put("id_account", id_account);
        cv1.put("date", transaction.getDate());
        cv1.put("amount", transaction.getAmount());
        cv1.put("category", transaction.getCategory());

        cv2.put("amount", transaction.getAccountAmount());

        openLocalToWrite();
        db.insert("Transactions", null, cv1);
        db.update("Account", cv2, "id_account = " + id_account, null);
        closeLocal();
    }


    public void insertNewDebt(Debt debt) {
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        int id_account = debt.getIdAccount();

        cv1.put("name", debt.getName());
        cv1.put("amount", debt.getAmount());
        cv1.put("type", debt.getType());
        cv1.put("id_account", debt.getIdAccount());
        cv1.put("date", debt.getDate());
        cv1.put("amount_first", debt.getAmount());

        cv2.put("amount", debt.getAccountAmount());


        openLocalToWrite();
        db.insert("Debt", null, cv1);
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


    public double[] getTransactionsStatistic(int position, String currency) {
        double[] arrayStatistic = new double[2];

        long period = 0;

        switch (position) {
            case 1: period = DateConstants.DAY; break;
            case 2: period = DateConstants.WEEK; break;
            case 3: period = DateConstants.MONTH; break;
            case 4: period = DateConstants.YEAR; break;
        }

        String selectQuery = "SELECT t.date, t.amount FROM Transactions t, Account a "
                               + "WHERE t.id_account = a.id_account "
                               + "AND a.currency = '" + currency + "' ";

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


    public double[] getAccountsSumGroupByCurrency(String currency) {

        String[] type = context.getResources().getStringArray(R.array.account_type_array);

        double[] result = new double[4];

        Cursor cursor;
        String selectQuery;

        openLocalToRead();

        for (int i = 0; i < type.length; i++) {

            selectQuery = "SELECT SUM(amount) FROM Account "
                    + "WHERE visibility = 1 AND "
                    + "type = '" + type[i] + "' AND "
                    + "currency = '" + currency + "' ";

            cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                result[i] = cursor.getDouble(0);
            }
            cursor.close();
        }

        selectQuery = "SELECT d.amount, d.type FROM Debt d, Account a "
                + "WHERE d.id_account = a.id_account AND "
                + "currency = '" + currency + "' ";

        cursor = db.rawQuery(selectQuery, null);

        double debtSum = 0;

        double debtVal;
        int debtType;

        if (cursor.moveToFirst()) {
            int amountColIndex = cursor.getColumnIndex("amount");
            int typeColIndex = cursor.getColumnIndex("type");

            do {
                debtVal = cursor.getDouble(amountColIndex);
                debtType = cursor.getInt(typeColIndex);

                if (debtType == 1) {
                    debtVal *= -1;
                }

                debtSum += debtVal;
            }

            while (cursor.moveToNext());

            cursor.close();

            result[3] = debtSum;
        }

            closeLocal();
            return result;
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

        String selectQuery = "SELECT t.amount, date, category, name, type, a.currency, t.id_account, id_transaction "
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
            int idAccountColIndex = cursor.getColumnIndex("id_account");
            int idTransColIndex = cursor.getColumnIndex("id_transaction");


            for (int i = cursor.getCount()-1; i >= 0; i--){
                cursor.moveToPosition(i);
                Transaction transaction = new Transaction(
                        cursor.getLong(dateColIndex),
                        cursor.getDouble(amountColIndex),
                        cursor.getString(categoryColIndex),
                        cursor.getString(nameColIndex),
                        cursor.getString(currencyColIndex),
                        cursor.getString(typeColIndex),
                        cursor.getInt(idAccountColIndex),
                        cursor.getInt(idTransColIndex));

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


    public ArrayList<Debt> getAllDebtInfo() {
        ArrayList<Debt> debtArrayList = new ArrayList<>();

        String selectQuery = "SELECT d.name AS d_name, d.amount, d.type, date, a.name AS a_name, currency, d.id_account, id_debt "
                + "FROM Debt d, Account a "
                + "WHERE d.id_account = a.id_account ";

        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            int dNameColIndex = cursor.getColumnIndex("d_name");
            int amountColIndex = cursor.getColumnIndex("amount");
            int typeColIndex = cursor.getColumnIndex("type");
            int dateColIndex = cursor.getColumnIndex("date");
            int aNameColIndex = cursor.getColumnIndex("a_name");
            int curColIndex = cursor.getColumnIndex("currency");
            int idAccountColIndex = cursor.getColumnIndex("id_account");
            int idDebtColIndex = cursor.getColumnIndex("id_debt");

            do {
                Debt debt = new Debt(
                        cursor.getString(dNameColIndex),
                        cursor.getDouble(amountColIndex),
                        cursor.getInt(typeColIndex),
                        cursor.getLong(dateColIndex),
                        cursor.getString(aNameColIndex),
                        cursor.getString(curColIndex),
                        cursor.getInt(idAccountColIndex),
                        cursor.getInt(idDebtColIndex)
                );

                debtArrayList.add(debt);
            }
            while (cursor.moveToNext());

            cursor.close();
            closeLocal();

            return debtArrayList;
        }

        cursor.close();
        closeLocal();

        return debtArrayList;
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
        closeLocal();
    }


    public boolean checkAccountForTransactionOrDebtExist(int id) {

        String selectQuery = "SELECT COUNT(id_transaction) FROM Transactions "
                + "WHERE id_account = '" + id +  "' ";

        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            int c = cursor.getInt(0);
            cursor.close();

            if (c > 0) {
            return true;}}


        selectQuery = "SELECT COUNT(id_debt) FROM Debt "
                + "WHERE id_account = '" + id +  "' ";

        cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            int c = cursor.getInt(0);

            if (c > 0) {
                cursor.close();
                closeLocal();

                return true;}}


        cursor.close();
        closeLocal();

        return false;
    }



    public void deleteTransaction(int id_account, int id_trans, double amount) {

        String selectQuery = "SELECT amount FROM Account "
                + "WHERE id_account = '" + id_account + "' ";


        openLocalToWrite();

        Cursor cursor = db.rawQuery(selectQuery, null);

        double accountAmount = 0;

        if(cursor.moveToFirst()) {
            accountAmount = cursor.getDouble(0);
        }

        cursor.close();

        accountAmount -= amount;


        ContentValues cv = new ContentValues();

        cv.put("amount", accountAmount);

        db.update("Account", cv, "id_account = " + id_account, null);
        db.delete("Transactions", "id_transaction = '" + id_trans + "' ", null);

        closeLocal();
    }



    public void deleteDebt(int id_account, int id_debt, double amount, int type) {

        String selectQuery = "SELECT amount FROM Account "
                + "WHERE id_account = '" + id_account + "' ";


        openLocalToWrite();

        Cursor cursor = db.rawQuery(selectQuery, null);

        double accountAmount = 0;

        if(cursor.moveToFirst()) {
            accountAmount = cursor.getDouble(0);
        }

        cursor.close();


        if (type == 1) {
            accountAmount -= amount;
        }

        else {
            accountAmount += amount;
        }


        ContentValues cv = new ContentValues();

        cv.put("amount", accountAmount);

        db.update("Account", cv, "id_account = " + id_account, null);
        db.delete("Debt", "id_debt = '" + id_debt + "' ", null);

        closeLocal();
    }


    public void payAllDebt(int idAccount, double accountAmount, int idDebt) {

        ContentValues cv = new ContentValues();

        cv.put("amount", accountAmount);

        openLocalToWrite();

        db.update("Account", cv, "id_account = " + idAccount, null);
        db.delete("Debt", "id_debt = '" + idDebt + "' ", null);

        closeLocal();
    }


    public void payPartDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {

        ContentValues cv1 = new ContentValues();
        cv1.put("amount", accountAmount);

        ContentValues cv2 = new ContentValues();
        cv2.put("amount", debtAmount);

        openLocalToWrite();

        db.update("Account", cv1, "id_account = " + idAccount, null);
        db.update("Debt", cv2, "id_debt = '" + idDebt + "' ", null);

        closeLocal();
    }

}