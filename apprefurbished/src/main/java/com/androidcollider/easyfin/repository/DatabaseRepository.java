package com.androidcollider.easyfin.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.DateConstants;
import com.androidcollider.easyfin.models.Debt;
import com.androidcollider.easyfin.models.Rates;
import com.androidcollider.easyfin.models.Transaction;
import com.androidcollider.easyfin.repository.database.DbHelper;
import com.androidcollider.easyfin.utils.DBExportImportUtils;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.SharedPref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Ihor Bilous
 */

class DatabaseRepository implements Repository {

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
    private SharedPref sharedPref;


    DatabaseRepository(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
        sharedPref = new SharedPref(context);
    }


    @Override
    public Observable<Account> addNewAccount(Account account) {
        return Observable.<Account>create(subscriber -> {
            subscriber.onNext(insertNewAccount(account));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<List<Account>> getAllAccounts() {
        return Observable.<List<Account>>create(subscriber -> {
            subscriber.onNext(getAllAccountsInfo());
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Account> updateAccount(Account account) {
        return Observable.<Account>create(subscriber -> {
            subscriber.onNext(editAccount(account));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Boolean> deleteAccount(int id) {
        return Observable.<Boolean>create(subscriber -> {
            subscriber.onNext(deleteAccountDB(id));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Boolean> transferBTWAccounts(int idAccount1, double accountAmount1, int idAccount2, double accountAmount2) {
        return Observable.<Boolean>create(subscriber -> {
            subscriber.onNext(updateAccountsAmountAfterTransfer(idAccount1, accountAmount1, idAccount2, accountAmount2));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Transaction> addNewTransaction(Transaction transaction) {
        return Observable.<Transaction>create(subscriber -> {
            subscriber.onNext(insertNewTransaction(transaction));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<List<Transaction>> getAllTransactions() {
        return Observable.<List<Transaction>>create(subscriber -> {
            subscriber.onNext(getAllTransactionsInfo());
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Transaction> updateTransaction(Transaction transaction) {
        return Observable.<Transaction>create(subscriber -> {
            subscriber.onNext(editTransaction(transaction));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        return Observable.<Boolean>create(subscriber -> {
            subscriber.onNext(editTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Boolean> deleteTransaction(int idAccount, int idTransaction, double amount) {
        return Observable.<Boolean>create(subscriber -> {
            subscriber.onNext(deleteTransactionDB(idAccount, idTransaction, amount));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Debt> addNewDebt(Debt debt) {
        return Observable.<Debt>create(subscriber -> {
            subscriber.onNext(insertNewDebt(debt));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<List<Debt>> getAllDebts() {
        return Observable.<List<Debt>>create(subscriber -> {
            subscriber.onNext(getAllDebtInfo());
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Debt> updateDebt(Debt debt) {
        return Observable.<Debt>create(subscriber -> {
            subscriber.onNext(editDebt(debt));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        return Observable.<Boolean>create(subscriber -> {
            subscriber.onNext(editDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Boolean> deleteDebt(int idAccount, int idDebt, double amount, int type) {
        return Observable.<Boolean>create(subscriber -> {
            subscriber.onNext(deleteDebtDB(idAccount, idDebt, amount, type));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return Observable.<Boolean>create(subscriber -> {
            subscriber.onNext(payAllDebt(idAccount, accountAmount, idDebt));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return Observable.<Boolean>create(subscriber -> {
            subscriber.onNext(payPartDebt(idAccount, accountAmount, idDebt, debtAmount));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        return Observable.<Boolean>create(subscriber -> {
            subscriber.onNext(takeMoreDebtDB(idAccount, accountAmount, idDebt, debtAmount, debtAllAmount));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Map<String, double[]>> getTransactionsStatistic(int position) {
        return Observable.<Map<String, double[]>>create(subscriber -> {
            subscriber.onNext(getTransactionsStatisticDB(position));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency() {
        return Observable.<Map<String, double[]>>create(subscriber -> {
            subscriber.onNext(getAccountsSumGroupByTypeAndCurrency());
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<Boolean> updateRates(List<Rates> ratesList) {
        return Observable.<Boolean>create(subscriber -> {
            subscriber.onNext(insertRates(ratesList));
            subscriber.onCompleted();
        })
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())*/;
    }

    @Override
    public Observable<double[]> getRates() {
        return Observable.<double[]>create(subscriber -> {
            subscriber.onNext(getRatesDB());
            subscriber.onCompleted();
        });
                /*.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());*/
    }

    @Override
    public Observable<Boolean> setAllAccounts(List<Account> accountList) {
        throw new IllegalStateException("do not perform this action!");
    }

    @Override
    public Observable<Boolean> setAllTransactions(List<Transaction> transactionList) {
        throw new IllegalStateException("do not perform this action!");
    }

    @Override
    public Observable<Boolean> setAllDebts(List<Debt> debtList) {
        throw new IllegalStateException("do not perform this action!");
    }

    @Override
    public Observable<Boolean> setRates(double[] rates) {
        throw new IllegalStateException("do not perform this action!");
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

    private Account insertNewAccount(Account account) {
        ContentValues cv = new ContentValues();

        cv.put("name", account.getName());
        cv.put("amount", account.getAmount());
        cv.put("type", account.getType());
        cv.put("currency", account.getCurrency());

        openLocalToWrite();
        int id = (int) insertAccountQuery(cv);
        closeLocal();
        if (id > 0) {
            account.setId(id);
        }
        return account;
    }

    private Transaction insertNewTransaction(Transaction transaction) {
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        int id_account = transaction.getIdAccount();

        cv1.put("id_account", id_account);
        cv1.put("date", transaction.getDate());
        cv1.put("amount", transaction.getAmount());
        cv1.put("category", transaction.getCategory());

        cv2.put("amount", transaction.getAccountAmount());

        openLocalToWrite();
        int transId = (int) insertTransactionQuery(cv1);
        updateAccountQuery(cv2, id_account);
        closeLocal();
        if (transId > 0) {
            transaction.setId(transId);
        }
        return transaction;
    }

    private Debt insertNewDebt(Debt debt) {
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
        int debtId = (int) insertDebtQuery(cv1);
        updateAccountQuery(cv2, id_account);
        closeLocal();
        if (debtId > 0) {
            debt.setId(debtId);
        }
        return debt;
    }

    private boolean updateAccountsAmountAfterTransfer(int id_account_1, double amount_1,
                                                      int id_account_2, double amount_2) {
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        cv1.put("amount", amount_1);
        cv2.put("amount", amount_2);

        openLocalToWrite();

        boolean res1 = updateAccountQuery(cv1, id_account_1);
        boolean res2 = updateAccountQuery(cv2, id_account_2);

        closeLocal();
        return res1 && res2;
    }

    private Map<String, double[]> getTransactionsStatisticDB(int position) {
        Log.d("COLLIDER", "trans stat db!");
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

        Map<String, double[]> result = new HashMap<>();

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

    private Map<String, double[]> getAccountsSumGroupByTypeAndCurrency() {
        Log.d("COLLIDER", "accounts stat db!");
        String[] currencyArray = context.getResources().getStringArray(R.array.account_currency_array);
        Map<String, double[]> results = new HashMap<>();
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

    private List<Account> getAllAccountsInfo() {
        List<Account> accountArrayList = new ArrayList<>();
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

    private List<Transaction> getAllTransactionsInfo() {
        List<Transaction> transactionArrayList = new ArrayList<>();

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

            /*if (cursorCount > 120) {
                limit = cursorCount - 120;
            }*/

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
    }

    private List<Debt> getAllDebtInfo() {
        List<Debt> debtArrayList = new ArrayList<>();

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
    }

    private Account editAccount(Account account) {
        ContentValues cv = new ContentValues();

        cv.put("name", account.getName());
        cv.put("amount", account.getAmount());
        cv.put("type", account.getType());
        cv.put("currency", account.getCurrency());

        int id = account.getId();

        openLocalToWrite();
        updateAccountQuery(cv, id);
        closeLocal();
        return account;
    }

    private boolean deleteAccountDB(int id) {
        return checkAccountForTransactionOrDebtExist(id) ?
                makeAccountInvisible(id) :
                deleteAccountFromDB(id);
    }

    private boolean deleteAccountFromDB(int id) {
        openLocalToWrite();
        boolean res = deleteAccountQuery(id);
        closeLocal();
        return res;
    }

    private boolean makeAccountInvisible(int id) {
        ContentValues cv = new ContentValues();
        cv.put("visibility", 0);

        openLocalToWrite();
        boolean res = updateAccountQuery(cv, id);
        closeLocal();
        return res;
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

    private boolean deleteTransactionDB(int id_account, int id_trans, double amount) {
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

        boolean res1 = updateAccountQuery(cv, id_account);
        //db.update("Account", cv, "id_account = " + id_account, null);
        boolean res2 = deleteTransactionQuery(id_trans);

        closeLocal();
        return res1 && res2;
    }

    private boolean deleteDebtDB(int id_account, int id_debt, double amount, int type) {
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
        boolean res1 = updateAccountQuery(cv, id_account);
        //db.update("Account", cv, "id_account = " + id_account, null);
        boolean res2 = deleteDebtQuery(id_debt);
        closeLocal();
        return res1 && res2;
    }

    private boolean payAllDebt(int idAccount, double accountAmount, int idDebt) {
        ContentValues cv = new ContentValues();
        cv.put("amount", accountAmount);
        openLocalToWrite();
        boolean res1 = updateAccountQuery(cv, idAccount);
        //db.update("Account", cv, "id_account = " + idAccount, null);
        boolean res2 = deleteDebtQuery(idDebt);
        closeLocal();
        return res1 && res2;
    }

    private boolean payPartDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        ContentValues cv1 = new ContentValues();
        cv1.put("amount", accountAmount);

        ContentValues cv2 = new ContentValues();
        cv2.put("amount_current", debtAmount);

        openLocalToWrite();
        boolean res1 = updateAccountQuery(cv1, idAccount);
        //db.update("Account", cv1, "id_account = " + idAccount, null);
        boolean res2 = updateDebtQuery(cv2, idDebt);
        //db.update("Debt", cv2, "id_debt = '" + idDebt + "' ", null);
        closeLocal();
        return res1 && res2;
    }

    private boolean takeMoreDebtDB(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        ContentValues cv1 = new ContentValues();
        cv1.put("amount", accountAmount);

        ContentValues cv2 = new ContentValues();
        cv2.put("amount_current", debtAmount);
        cv2.put("amount_all", debtAllAmount);

        openLocalToWrite();
        boolean res1 = updateAccountQuery(cv1, idAccount);
        boolean res2 = updateDebtQuery(cv2, idDebt);
        closeLocal();
        return res1 && res2;
    }

    private boolean insertRates(List<Rates> ratesList) {
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
        return true;
    }

    private double[] getRatesDB() {
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
    }

    private Transaction editTransaction(Transaction transaction) {
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
        updateTransactionQuery(cv1, id_transaction);
        updateAccountQuery(cv2, id_account);
        closeLocal();
        return transaction;
    }

    private boolean editTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
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
        boolean res1 = updateTransactionQuery(cv1, id_transaction);
        boolean res2 = updateAccountQuery(cv2, id_account);
        boolean res3 = updateAccountQuery(cv3, oldAccountId);
        closeLocal();
        return res1 && res2 && res3;
    }

    private Debt editDebt(Debt debt) {
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
        updateDebtQuery(cv1, id_debt);
        updateAccountQuery(cv2, id_account);
        closeLocal();
        return debt;
    }

    private boolean editDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
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
        boolean res1 = updateDebtQuery(cv1, id_debt);
        boolean res2 = updateAccountQuery(cv2, id_account);
        boolean res3 = updateAccountQuery(cv3, oldAccountId);
        closeLocal();
        return res1 && res2 && res3;
    }

    private long insertAccountQuery(ContentValues cv) {
        return db.insert("Account", null, cv);
    }

    private long insertTransactionQuery(ContentValues cv) {
        return db.insert("Transactions", null, cv);
    }

    private long insertDebtQuery(ContentValues cv) {
        return db.insert("Debt", null, cv);
    }

    private boolean updateAccountQuery(ContentValues cv, int id) {
        return db.update("Account", cv, "id_account = " + id, null) > 0;
    }

    private boolean updateTransactionQuery(ContentValues cv, int id) {
        return db.update("Transactions", cv, "id_transaction = " + id, null) > 0;
    }

    private boolean updateDebtQuery(ContentValues cv, int id) {
        return db.update("Debt", cv, "id_debt = " + id, null) > 0;
    }

    private boolean deleteAccountQuery(int idAccount) {
        return db.delete("Account", "id_account = '" + idAccount + "' ", null) > 0;
    }

    private boolean deleteTransactionQuery(int idTrans) {
        return db.delete("Transactions", "id_transaction = " + idTrans, null) > 0;
    }

    private boolean deleteDebtQuery(int idDebt) {
        return db.delete("Debt", "id_debt = " + idDebt, null) > 0;
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
}