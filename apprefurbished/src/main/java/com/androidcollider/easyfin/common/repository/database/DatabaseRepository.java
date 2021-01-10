package com.androidcollider.easyfin.common.repository.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.DateConstants;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.models.Rates;
import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.androidcollider.easyfin.common.repository.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

public class DatabaseRepository implements Repository {

    private static final String TAG = DatabaseRepository.class.getSimpleName();

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private SharedPrefManager sharedPrefManager;
    private NumberFormatManager numberFormatManager;
    private ResourcesManager resourcesManager;


    public DatabaseRepository(DbHelper dbHelper,
                              SharedPrefManager sharedPrefManager,
                              NumberFormatManager numberFormatManager,
                              ResourcesManager resourcesManager) {
        this.dbHelper = dbHelper;
        this.sharedPrefManager = sharedPrefManager;
        this.numberFormatManager = numberFormatManager;
        this.resourcesManager = resourcesManager;
    }


    @Override
    public Flowable<Account> addNewAccount(Account account) {
        return Flowable.fromCallable(() -> insertNewAccount(account));
    }

    @Override
    public Flowable<List<Account>> getAllAccounts() {
        return Flowable.fromCallable(this::getAllAccountsInfo);
    }

    @Override
    public Flowable<Account> updateAccount(Account account) {
        return Flowable.fromCallable(() -> editAccount(account));
    }

    @Override
    public Flowable<Boolean> deleteAccount(int id) {
        return Flowable.fromCallable(() -> deleteAccountDB(id));
    }

    @Override
    public Flowable<Boolean> transferBTWAccounts(int idAccount1, double accountAmount1, int idAccount2, double accountAmount2) {
        return Flowable.fromCallable(() -> updateAccountsAmountAfterTransfer(idAccount1, accountAmount1, idAccount2, accountAmount2));
    }

    @Override
    public Flowable<Transaction> addNewTransaction(Transaction transaction) {
        return Flowable.fromCallable(() -> insertNewTransaction(transaction));
    }

    @Override
    public Flowable<List<Transaction>> getAllTransactions() {
        return Flowable.fromCallable(this::getAllTransactionsInfo);
    }

    @Override
    public Flowable<Transaction> updateTransaction(Transaction transaction) {
        return Flowable.fromCallable(() -> editTransaction(transaction));
    }

    @Override
    public Flowable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        return Flowable.fromCallable(() -> editTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId));
    }

    @Override
    public Flowable<Boolean> deleteTransaction(int idAccount, int idTransaction, double amount) {
        return Flowable.fromCallable(() -> deleteTransactionDB(idAccount, idTransaction, amount));
    }

    @Override
    public Flowable<Debt> addNewDebt(Debt debt) {
        return Flowable.fromCallable(() -> insertNewDebt(debt));
    }

    @Override
    public Flowable<List<Debt>> getAllDebts() {
        return Flowable.fromCallable(this::getAllDebtInfo);
    }

    @Override
    public Flowable<Debt> updateDebt(Debt debt) {
        return Flowable.fromCallable(() -> editDebt(debt));
    }

    @Override
    public Flowable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        return Flowable.fromCallable(() -> editDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId));
    }

    @Override
    public Flowable<Boolean> deleteDebt(int idAccount, int idDebt, double amount, int type) {
        return Flowable.fromCallable(() -> deleteDebtDB(idAccount, idDebt, amount, type));
    }

    @Override
    public Flowable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return Flowable.fromCallable(() -> payAllDebt(idAccount, accountAmount, idDebt));
    }

    @Override
    public Flowable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return Flowable.fromCallable(() -> payPartDebt(idAccount, accountAmount, idDebt, debtAmount));
    }

    @Override
    public Flowable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        return Flowable.fromCallable(() -> takeMoreDebtDB(idAccount, accountAmount, idDebt, debtAmount, debtAllAmount));
    }

    @Override
    public Flowable<Map<String, double[]>> getTransactionsStatistic(int position) {
        return Flowable.fromCallable(() -> getTransactionsStatisticDB(position));
    }

    @Override
    public Flowable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency() {
        return Flowable.fromCallable(this::getAccountsSumGroupByTypeAndCurrency);
    }

    @Override
    public Flowable<Boolean> updateRates(List<Rates> ratesList) {
        return Flowable.fromCallable(() -> insertRates(ratesList));
    }

    @Override
    public Flowable<double[]> getRates() {
        return Flowable.fromCallable(this::getRatesDB);
    }

    @Override
    public Flowable<Boolean> setAllAccounts(List<Account> accountList) {
        throw new IllegalStateException("do not perform this action!");
    }

    @Override
    public Flowable<Boolean> setAllTransactions(List<Transaction> transactionList) {
        throw new IllegalStateException("do not perform this action!");
    }

    @Override
    public Flowable<Boolean> setAllDebts(List<Debt> debtList) {
        throw new IllegalStateException("do not perform this action!");
    }

    @Override
    public Flowable<Boolean> setRates(double[] rates) {
        throw new IllegalStateException("do not perform this action!");
    }

    @Override
    public Flowable<TransactionCategory> addNewTransactionIncomeCategory(TransactionCategory transactionCategory) {
        return Flowable.fromCallable(() -> insertNewTransactionIncomeCategory(transactionCategory));
    }

    @Override
    public Flowable<List<TransactionCategory>> getAllTransactionIncomeCategories() {
        return Flowable.fromCallable(this::getTransactionIncomeCategories);
    }

    @Override
    public Flowable<TransactionCategory> updateTransactionIncomeCategory(TransactionCategory transactionCategory) {
        return Flowable.fromCallable(() -> editTransactionIncomeCategory(transactionCategory));
    }

    @Override
    public Flowable<Boolean> deleteTransactionIncomeCategory(int id) {
        return Flowable.fromCallable(() -> makeTransactionCategoryIncomeInvisible(id));
    }

    @Override
    public Flowable<Boolean> setAllTransactionIncomeCategories(List<TransactionCategory> transactionCategoryList) {
        throw new IllegalStateException("do not perform this action!");
    }

    @Override
    public Flowable<TransactionCategory> addNewTransactionExpenseCategory(TransactionCategory transactionCategory) {
        return Flowable.fromCallable(() -> insertNewTransactionExpenseCategory(transactionCategory));
    }

    @Override
    public Flowable<List<TransactionCategory>> getAllTransactionExpenseCategories() {
        return Flowable.fromCallable(this::getTransactionExpenseCategories);
    }

    @Override
    public Flowable<TransactionCategory> updateTransactionExpenseCategory(TransactionCategory transactionCategory) {
        return Flowable.fromCallable(() -> editTransactionExpenseCategory(transactionCategory));
    }

    @Override
    public Flowable<Boolean> deleteTransactionExpenseCategory(int id) {
        return Flowable.fromCallable(() -> makeTransactionCategoryExpenseInvisible(id));
    }

    @Override
    public Flowable<Boolean> setAllTransactionExpenseCategories(List<TransactionCategory> transactionCategoryList) {
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
        Log.d(TAG, "getTransactionsStatisticDB");
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
            case 5:
                period = Long.MAX_VALUE;
                break;
        }

        String[] currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);

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
                        if (numberFormatManager.isDoubleNegative(amount)) {
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
        Log.d(TAG, "getAccountsSumGroupByTypeAndCurrency");
        String[] currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
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
        Log.d(TAG, "getAllAccountsInfo");
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
                Account account = new Account();
                account.setId(cursor.getInt(idColIndex));
                account.setName(cursor.getString(nameColIndex));
                account.setAmount(cursor.getDouble(amountColIndex));
                account.setType(cursor.getInt(typeColIndex));
                account.setCurrency(cursor.getString(currencyColIndex));

                accountArrayList.add(account);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        closeLocal();
        return accountArrayList;
    }

    private List<Transaction> getAllTransactionsInfo() {
        Log.d(TAG, "getAllTransactionsInfo");
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

                Transaction transaction = new Transaction();
                transaction.setDate(cursor.getLong(dateColIndex));
                transaction.setAmount(cursor.getDouble(amountColIndex));
                transaction.setCategory(cursor.getInt(categoryColIndex));
                transaction.setAccountName(cursor.getString(nameColIndex));
                transaction.setCurrency(cursor.getString(currencyColIndex));
                transaction.setAccountType(cursor.getInt(typeColIndex));
                transaction.setAccountType(cursor.getInt(typeColIndex));
                transaction.setIdAccount(cursor.getInt(idAccountColIndex));
                transaction.setId(cursor.getInt(idTransColIndex));

                transactionArrayList.add(transaction);
            }
        }
        cursor.close();
        closeLocal();
        return transactionArrayList;
    }

    private List<Debt> getAllDebtInfo() {
        Log.d(TAG, "getAllDebtInfo");
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
                Debt debt = new Debt();
                debt.setName(cursor.getString(dNameColIndex));
                debt.setAmountCurrent(cursor.getDouble(amountColIndex));
                debt.setAmountAll(cursor.getDouble(amountAllColIndex));
                debt.setType(cursor.getInt(typeColIndex));
                debt.setDate(cursor.getLong(dateColIndex));
                debt.setAccountName(cursor.getString(aNameColIndex));
                debt.setCurrency(cursor.getString(curColIndex));
                debt.setIdAccount(cursor.getInt(idAccountColIndex));
                debt.setId(cursor.getInt(idDebtColIndex));

                debtArrayList.add(debt);
            } while (cursor.moveToNext());
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
        boolean res2 = deleteDebtQuery(id_debt);
        closeLocal();
        return res1 && res2;
    }

    private boolean payAllDebt(int idAccount, double accountAmount, int idDebt) {
        ContentValues cv = new ContentValues();
        cv.put("amount", accountAmount);
        openLocalToWrite();
        boolean res1 = updateAccountQuery(cv, idAccount);
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
        boolean res2 = updateDebtQuery(cv2, idDebt);
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
        sharedPrefManager.setRatesInsertFirstTimeStatus(true);
        sharedPrefManager.setRatesUpdateTime();
        return true;
    }

    private double[] getRatesDB() {
        Log.d(TAG, "getRatesDB");
        String[] rateNamesArray = resourcesManager.getStringArray(ResourcesManager.STRING_JSON_RATES);
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

    private long insertTransactionCategoryQuery(ContentValues cv, boolean isExpense) {
        return db.insert(isExpense ?
                        "Transactions_Category_Expense" :
                        "Transactions_Category_Income",
                null, cv);
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

    private boolean updateTransactionCategoryQuery(ContentValues cv, int id, boolean isExpense) {
        return db.update(isExpense ?
                        "Transactions_Category_Expense" :
                        "Transactions_Category_Income",
                cv, "id_category = " + id, null) > 0;
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

    private List<TransactionCategory> getTransactionCategoriesDB(boolean isExpense) {
        List<TransactionCategory> categoryList = new ArrayList<>();

        String[] initCategoriesArray = resourcesManager.getStringArray(isExpense ?
                ResourcesManager.STRING_TRANSACTION_CATEGORY_EXPENSE :
                ResourcesManager.STRING_TRANSACTION_CATEGORY_INCOME
        );

        for (int i = 0; i < initCategoriesArray.length; i++) {
            categoryList.add(new TransactionCategory(i, initCategoriesArray[i], 1));
        }

        String selectQuery = isExpense ?
                "SELECT * FROM Transactions_Category_Expense " :
                "SELECT * FROM Transactions_Category_Income ";

        openLocalToRead();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id_category");
            int nameColIndex = cursor.getColumnIndex("name");
            int visibilityColIndex = cursor.getColumnIndex("visibility");
            do {
                int id = cursor.getInt(idColIndex);
                String name = cursor.getString(nameColIndex);
                int visibility = cursor.getInt(visibilityColIndex);
                categoryList.add(new TransactionCategory(id, name, visibility));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        closeLocal();
        return categoryList;
    }

    private List<TransactionCategory> getTransactionIncomeCategories() {
        return getTransactionCategoriesDB(false);
    }

    private List<TransactionCategory> getTransactionExpenseCategories() {
        return getTransactionCategoriesDB(true);
    }

    private boolean makeTransactionCategoryInvisible(int id, boolean isExpense) {
        ContentValues cv = new ContentValues();
        cv.put("visibility", 0);

        openLocalToWrite();
        boolean res = updateTransactionCategoryQuery(cv, id, isExpense);
        closeLocal();
        return res;
    }

    private boolean makeTransactionCategoryIncomeInvisible(int id) {
        return makeTransactionCategoryInvisible(id, false);
    }

    private boolean makeTransactionCategoryExpenseInvisible(int id) {
        return makeTransactionCategoryInvisible(id, true);
    }

    private TransactionCategory editTransactionCategory(TransactionCategory transactionCategory, boolean isExpense) {
        ContentValues cv = new ContentValues();
        cv.put("name", transactionCategory.getName());

        int id = transactionCategory.getId();

        openLocalToWrite();
        updateTransactionCategoryQuery(cv, id, isExpense);
        closeLocal();
        return transactionCategory;
    }

    private TransactionCategory editTransactionIncomeCategory(TransactionCategory transactionCategory) {
        return editTransactionCategory(transactionCategory, false);
    }

    private TransactionCategory editTransactionExpenseCategory(TransactionCategory transactionCategory) {
        return editTransactionCategory(transactionCategory, true);
    }

    private TransactionCategory insertNewTransactionCategory(TransactionCategory transactionCategory, boolean isExpense) {
        ContentValues cv = new ContentValues();

        cv.put("name", transactionCategory.getName());
        cv.put("id_category", transactionCategory.getId());

        openLocalToWrite();
        insertTransactionCategoryQuery(cv, isExpense);
        closeLocal();

        return transactionCategory;
    }

    private TransactionCategory insertNewTransactionIncomeCategory(TransactionCategory transactionCategory) {
        return insertNewTransactionCategory(transactionCategory, false);
    }

    private TransactionCategory insertNewTransactionExpenseCategory(TransactionCategory transactionCategory) {
        return insertNewTransactionCategory(transactionCategory, true);
    }
}