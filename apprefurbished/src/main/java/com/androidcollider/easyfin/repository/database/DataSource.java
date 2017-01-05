package com.androidcollider.easyfin.repository.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.DateConstants;
import com.androidcollider.easyfin.utils.DBExportImportUtils;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class DataSource {

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
    //private SharedPref sharedPref;

    public DataSource(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
        //sharedPref = new SharedPref(App.getContext());
    }

    //Open database to write
    private void openLocalToWrite() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    //Open database to read
    private void openLocalToRead() throws SQLException {
        db = dbHelper.getReadableDatabase();
    }

    //Close database
    private void closeLocal() {
        db.close();
    }

    /*public void insertNewAccount(Account account) {
        ContentValues cv = new ContentValues();

        cv.put("name", account.getName());
        cv.put("amount", account.getAmount());
        cv.put("type", account.getType());
        cv.put("currency", account.getCurrency());

        openLocalToWrite();
        db.insert("Account", null, cv);
        closeLocal();
    }*/

    /*public void insertNewTransaction(Transaction transaction) {
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
        updateAccount(cv2, id_account);
        //db.update("Account", cv2, "id_account = " + id_account, null);
        closeLocal();
    }*/

    /*public void insertNewDebt(Debt debt) {
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        int id_account = debt.getIdAccount();

        cv1.put("name", debt.getName());
        cv1.put("amount_current", debt.getAmountCurrent());
        cv1.put("type", debt.getType());
        cv1.put("id_account", debt.getIdAccount());
        cv1.put("deadline", debt.getDate());
        cv1.put("amount_all", debt.getAmountCurrent());

        cv2.put("amount", debt.getAccountAmount());

        openLocalToWrite();
        db.insert("Debt", null, cv1);
        updateAccount(cv2, id_account);
        //db.update("Account", cv2, "id_account = " + id_account, null);
        closeLocal();
    }*/

    /*public void updateAccountsAmountAfterTransfer(int id_account_1, double amount_1,
                                                  int id_account_2, double amount_2) {
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        cv1.put("amount", amount_1);
        cv2.put("amount", amount_2);

        openLocalToWrite();

        updateAccount(cv1, id_account_1);
        updateAccount(cv2, id_account_2);
        *//*db.update("Account", cv1, "id_account = " + id_account_1, null);
        db.update("Account", cv2, "id_account = " + id_account_2, null);*//*

        closeLocal();
    }*/

    public HashMap<String, double[]> getTransactionsStatistic(int position) {

        long period = 0;

        switch (position) {
            case 1:
                period = DateConstants.DAY;
                break;
            case 2:
                period = DateConstants.WEEK;
                break;
            case 3:
                period = DateConstants.MONTH;
                break;
            case 4:
                period = DateConstants.YEAR;
                break;
        }

        String[] currencyArray = context.getResources().getStringArray(R.array.account_currency_array);

        HashMap<String, double[]> result = new HashMap<>();

        Cursor cursor;
        String selectQuery;

        openLocalToRead();

        for (String currency : currencyArray) {
            double[] arrStat = new double[2];

            selectQuery = "SELECT t.date, t.amount FROM Transactions t, Account a "
                    + "WHERE t.id_account = a.id_account "
                    + "AND a.currency = '" + currency + "' ";

            cursor = db.rawQuery(selectQuery, null);

            double cost = 0.0;
            double income = 0.0;

            if (cursor.moveToFirst()) {
                int amountColIndex = cursor.getColumnIndex("amount");
                int dateColIndex = cursor.getColumnIndex("date");

                long currentTime = new Date().getTime();

                for (int i = cursor.getCount() - 1; i >= 0; i--) {
                    cursor.moveToPosition(i);
                    long date = cursor.getLong(dateColIndex);
                    double amount = cursor.getDouble(amountColIndex);
                    if (currentTime > date && period >= (currentTime - date)) {
                        if (DoubleFormatUtils.isDoubleNegative(amount)) {
                            cost += amount;
                        } else {
                            income += amount;
                        }
                    }
                }
            }
            cursor.close();

            arrStat[0] = cost;
            arrStat[1] = income;

            result.put(currency, arrStat);
        }

        closeLocal();
        return result;
    }

    public HashMap<String, double[]> getAccountsSumGroupByTypeAndCurrency() {
        String[] currencyArray = context.getResources().getStringArray(R.array.account_currency_array);
        HashMap<String, double[]> results = new HashMap<>();
        Cursor cursor;
        String selectQuery;

        openLocalToRead();

        for (String currency : currencyArray) {
            double[] result = new double[4];
            for (int i = 0; i < 3; i++) {
                selectQuery = "SELECT SUM(amount) FROM Account "
                        + "WHERE visibility = 1 AND "
                        + "type = '" + i + "' AND "
                        + "currency = '" + currency + "' ";

                cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    result[i] = cursor.getDouble(0);
                }
                cursor.close();
            }

            selectQuery = "SELECT d.amount_current, d.type FROM Debt d, Account a "
                    + "WHERE d.id_account = a.id_account AND "
                    + "currency = '" + currency + "' ";

            cursor = db.rawQuery(selectQuery, null);

            double debtSum = 0;

            double debtVal;
            int debtType;

            if (cursor.moveToFirst()) {
                int amountColIndex = cursor.getColumnIndex("amount_current");
                int typeColIndex = cursor.getColumnIndex("type");
                do {
                    debtVal = cursor.getDouble(amountColIndex);
                    debtType = cursor.getInt(typeColIndex);
                    if (debtType == 1) {
                        debtVal *= -1;
                    }
                    debtSum += debtVal;
                } while (cursor.moveToNext());

                cursor.close();

                result[3] = debtSum;
            }
            results.put(currency, result);
        }
        closeLocal();
        return results;
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
                Account account = Account.builder()
                        .id(cursor.getInt(idColIndex))
                        .name(cursor.getString(nameColIndex))
                        .amount(cursor.getDouble(amountColIndex))
                        .type(cursor.getInt(typeColIndex))
                        .currency(cursor.getString(currencyColIndex))
                        .build();

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

    /*public ArrayList<Transaction> getAllTransactionsInfo() {
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

            int cursorCount = cursor.getCount();
            int limit = 0;

            if (cursorCount > 120) {
                limit = cursorCount - 120;
            }

            for (int i = cursorCount - 1; i >= limit; i--) {
                cursor.moveToPosition(i);

                Transaction transaction = Transaction.builder()
                        .date(cursor.getLong(dateColIndex))
                        .amount(cursor.getDouble(amountColIndex))
                        .category(cursor.getInt(categoryColIndex))
                        .accountName(cursor.getString(nameColIndex))
                        .currency(cursor.getString(currencyColIndex))
                        .accountType(cursor.getInt(typeColIndex))
                        .accountType(cursor.getInt(typeColIndex))
                        .idAccount(cursor.getInt(idAccountColIndex))
                        .id(cursor.getInt(idTransColIndex))
                        .build();

                transactionArrayList.add(transaction);
            }
            cursor.close();
            closeLocal();
            return transactionArrayList;
        }
        cursor.close();
        closeLocal();
        return transactionArrayList;
    }*/

    /*public ArrayList<Debt> getAllDebtInfo() {
        ArrayList<Debt> debtArrayList = new ArrayList<>();

        String selectQuery = "SELECT d.name AS d_name, d.amount_current, d.amount_all, "
                + "d.type, deadline, a.name AS a_name, currency, d.id_account, id_debt "
                + "FROM Debt d, Account a "
                + "WHERE d.id_account = a.id_account ";

        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int dNameColIndex = cursor.getColumnIndex("d_name");
            int amountColIndex = cursor.getColumnIndex("amount_current");
            int amountAllColIndex = cursor.getColumnIndex("amount_all");
            int typeColIndex = cursor.getColumnIndex("type");
            int dateColIndex = cursor.getColumnIndex("deadline");
            int aNameColIndex = cursor.getColumnIndex("a_name");
            int curColIndex = cursor.getColumnIndex("currency");
            int idAccountColIndex = cursor.getColumnIndex("id_account");
            int idDebtColIndex = cursor.getColumnIndex("id_debt");

            do {
                Debt debt = Debt.builder()
                        .name(cursor.getString(dNameColIndex))
                        .amountCurrent(cursor.getDouble(amountColIndex))
                        .amountAll(cursor.getDouble(amountAllColIndex))
                        .type(cursor.getInt(typeColIndex))
                        .date(cursor.getLong(dateColIndex))
                        .accountName(cursor.getString(aNameColIndex))
                        .currency(cursor.getString(curColIndex))
                        .idAccount(cursor.getInt(idAccountColIndex))
                        .id(cursor.getInt(idDebtColIndex))
                        .build();
                debtArrayList.add(debt);
            } while (cursor.moveToNext());

            cursor.close();
            closeLocal();
            return debtArrayList;
        }

        cursor.close();
        closeLocal();
        return debtArrayList;
    }*/

    /*public void editAccount(Account account) {
        ContentValues cv = new ContentValues();

        cv.put("name", account.getName());
        cv.put("amount", account.getAmount());
        cv.put("type", account.getType());
        cv.put("currency", account.getCurrency());

        int id = account.getId();

        openLocalToWrite();
        updateAccount(cv, id);
        //db.update("Account", cv, "id_account = '" + id + "' ", null);
        closeLocal();
    }*/

    public void deleteAccount(int id) {
        if (checkAccountForTransactionOrDebtExist(id)) {
            makeAccountInvisible(id);
        } else {
            deleteAccountFromDB(id);
        }
    }

    private void deleteAccountFromDB(int id) {
        openLocalToWrite();
        db.delete("Account", "id_account = '" + id + "' ", null);
        closeLocal();
    }

    private void makeAccountInvisible(int id) {
        ContentValues cv = new ContentValues();
        cv.put("visibility", 0);

        openLocalToWrite();
        updateAccount(cv, id);
        //db.update("Account", cv, "id_account = '" + id + "' ", null);
        closeLocal();
    }

    private boolean checkAccountForTransactionOrDebtExist(int id) {
        String selectQuery = "SELECT COUNT(id_transaction) FROM Transactions "
                + "WHERE id_account = '" + id + "' ";

        openLocalToRead();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            cursor.close();
            return true;
        }

        cursor.close();

        selectQuery = "SELECT COUNT(id_debt) FROM Debt "
                + "WHERE id_account = '" + id + "' ";

        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            cursor.close();
            closeLocal();
            return true;
        }

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
        if (cursor.moveToFirst()) {
            accountAmount = cursor.getDouble(0);
        }
        cursor.close();
        accountAmount -= amount;

        ContentValues cv = new ContentValues();

        cv.put("amount", accountAmount);

        updateAccount(cv, id_account);
        //db.update("Account", cv, "id_account = " + id_account, null);
        db.delete("Transactions", "id_transaction = '" + id_trans + "' ", null);

        closeLocal();
    }

    public void deleteDebt(int id_account, int id_debt, double amount, int type) {
        String selectQuery = "SELECT amount FROM Account "
                + "WHERE id_account = '" + id_account + "' ";

        openLocalToWrite();

        Cursor cursor = db.rawQuery(selectQuery, null);

        double accountAmount = 0;
        if (cursor.moveToFirst()) {
            accountAmount = cursor.getDouble(0);
        }
        cursor.close();

        if (type == 1) {
            accountAmount -= amount;
        } else {
            accountAmount += amount;
        }

        ContentValues cv = new ContentValues();
        cv.put("amount", accountAmount);
        updateAccount(cv, id_account);
        //db.update("Account", cv, "id_account = " + id_account, null);
        db.delete("Debt", "id_debt = '" + id_debt + "' ", null);
        closeLocal();
    }

    /*public void payAllDebt(int idAccount, double accountAmount, int idDebt) {
        ContentValues cv = new ContentValues();
        cv.put("amount", accountAmount);
        openLocalToWrite();
        updateAccount(cv, idAccount);
        //db.update("Account", cv, "id_account = " + idAccount, null);
        db.delete("Debt", "id_debt = '" + idDebt + "' ", null);
        closeLocal();
    }

    public void payPartDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        ContentValues cv1 = new ContentValues();
        cv1.put("amount", accountAmount);

        ContentValues cv2 = new ContentValues();
        cv2.put("amount_current", debtAmount);

        openLocalToWrite();
        updateAccount(cv1, idAccount);
        //db.update("Account", cv1, "id_account = " + idAccount, null);
        updateDebt(cv2, idDebt);
        //db.update("Debt", cv2, "id_debt = '" + idDebt + "' ", null);
        closeLocal();
    }

    public void takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        ContentValues cv1 = new ContentValues();
        cv1.put("amount", accountAmount);

        ContentValues cv2 = new ContentValues();
        cv2.put("amount_current", debtAmount);
        cv2.put("amount_all", debtAllAmount);

        openLocalToWrite();
        updateAccount(cv1, idAccount);
        //db.update("Account", cv1, "id_account = " + idAccount, null);
        updateDebt(cv2, idDebt);
        //db.update("Debt", cv2, "id_debt = '" + idDebt + "' ", null);
        closeLocal();
    }*/

    /*public void insertRates(ArrayList<Rates> ratesList) {
        ContentValues cv = new ContentValues();

        openLocalToWrite();
        int id;
        for (Rates rates : ratesList) {
            id = rates.getId();

            cv.put("date", rates.getDate());
            cv.put("currency", rates.getCurrency());
            cv.put("rate_type", rates.getRateType());
            cv.put("bid", rates.getBid());
            cv.put("ask", rates.getAsk());

            if (db.update("Rates", cv, "id_rate = '" + id + "' ", null) == 0) {
                cv.put("id_rate", id);
                db.insert("Rates", null, cv);
            }
        }
        closeLocal();
        //InMemoryRepository.getInstance().setRatesForExchange();
        sharedPref.setRatesInsertFirstTimeStatus(true);
        sharedPref.setRatesUpdateTime();
    }*/

    /*public double[] getRates() {
        String[] rateNamesArray = context.getResources().getStringArray(R.array.json_rates_array);
        String rateType = "bank";
        double[] results = new double[4];
        Cursor cursor;
        String selectQuery;

        openLocalToRead();

        for (int i = 0; i < rateNamesArray.length; i++) {
            String currency = rateNamesArray[i];

            selectQuery = "SELECT ask FROM Rates "
                    + "WHERE rate_type = '" + rateType + "' "
                    + "AND currency = '" + currency + "' ";

            cursor = db.rawQuery(selectQuery, null);
            results[i] = cursor.moveToFirst() ? cursor.getDouble(0) : 0;
            cursor.close();
        }
        closeLocal();
        return results;
    }*/

    /*public void editTransaction(Transaction transaction) {
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        int id_account = transaction.getIdAccount();
        int id_transaction = transaction.getId();

        cv1.put("id_account", id_account);
        cv1.put("date", transaction.getDate());
        cv1.put("amount", transaction.getAmount());
        cv1.put("category", transaction.getCategory());

        cv2.put("amount", transaction.getAccountAmount());

        openLocalToWrite();
        updateTransaction(cv1, id_transaction);
        //db.update("Transactions", cv1, "id_transaction = " + id_transaction, null);
        updateAccount(cv2, id_account);
        //db.update("Account", cv2, "id_account = " + id_account, null);
        closeLocal();
    }

    public void editTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();
        ContentValues cv3 = new ContentValues();

        int id_account = transaction.getIdAccount();
        int id_transaction = transaction.getId();

        cv1.put("id_account", id_account);
        cv1.put("date", transaction.getDate());
        cv1.put("amount", transaction.getAmount());
        cv1.put("category", transaction.getCategory());

        cv2.put("amount", transaction.getAccountAmount());

        cv3.put("amount", oldAccountAmount);

        openLocalToWrite();
        updateTransaction(cv1, id_transaction);
        //db.update("Transactions", cv1, "id_transaction = " + id_transaction, null);
        updateAccount(cv2, id_account);
        updateAccount(cv3, oldAccountId);
        *//*db.update("Account", cv2, "id_account = " + id_account, null);
        db.update("Account", cv3, "id_account = " + oldAccountId, null);*//*
        closeLocal();
    }

    public void editDebt(Debt debt) {
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        int id_account = debt.getIdAccount();
        int id_debt = debt.getId();

        cv1.put("name", debt.getName());
        cv1.put("amount_current", debt.getAmountCurrent());
        cv1.put("type", debt.getType());
        cv1.put("id_account", debt.getIdAccount());
        cv1.put("deadline", debt.getDate());
        cv1.put("amount_all", debt.getAmountCurrent());

        cv2.put("amount", debt.getAccountAmount());

        openLocalToWrite();
        updateDebt(cv1, id_debt);
        //db.update("Debt", cv1, "id_debt = " + id_debt, null);
        updateAccount(cv2, id_account);
        //db.update("Account", cv2, "id_account = " + id_account, null);
        closeLocal();
    }

    public void editDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();
        ContentValues cv3 = new ContentValues();

        int id_account = debt.getIdAccount();
        int id_debt = debt.getId();

        cv1.put("name", debt.getName());
        cv1.put("amount_current", debt.getAmountCurrent());
        cv1.put("type", debt.getType());
        cv1.put("id_account", debt.getIdAccount());
        cv1.put("deadline", debt.getDate());
        cv1.put("amount_all", debt.getAmountCurrent());

        cv2.put("amount", debt.getAccountAmount());

        cv3.put("amount", oldAccountAmount);

        openLocalToWrite();
        updateDebt(cv1, id_debt);
        //db.update("Debt", cv1, "id_debt = " + id_debt, null);
        updateAccount(cv2, id_account);
        updateAccount(cv3, oldAccountId);
        *//*db.update("Account", cv2, "id_account = " + id_account, null);
        db.update("Account", cv3, "id_account = " + oldAccountId, null);*//*
        closeLocal();
    }*/

    private void updateAccount(ContentValues cv, int id) {
        db.update("Account", cv, "id_account = " + id, null);
    }

    /*private void updateTransaction(ContentValues cv, int id) {
        db.update("Transactions", cv, "id_transaction = " + id, null);
    }

    private void updateDebt(ContentValues cv, int id) {
        db.update("Debt", cv, "id_debt = " + id, null);
    }*/


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
}