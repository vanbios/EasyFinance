package com.androidcollider.easyfin.repository;

import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.Debt;
import com.androidcollider.easyfin.models.Rates;
import com.androidcollider.easyfin.models.Transaction;

import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

public class DatabaseRepository implements Repository {

    @Override
    public Observable<Boolean> addNewAccount(Account account) {
        return null;
    }

    @Override
    public Observable<List<Account>> getAllAccounts() {
        return null;
    }

    @Override
    public Observable<Boolean> updateAccount(Account account) {
        return null;
    }

    @Override
    public Observable<Boolean> deleteAccount(int id) {
        return null;
    }

    @Override
    public Observable<Boolean> transferBTWAccounts(int idAccount1, double accountAmount1, int idAccount2, double accountAmount2) {
        return null;
    }

    @Override
    public Observable<Boolean> addNewTransaction(Transaction transaction) {
        return null;
    }

    @Override
    public Observable<List<Transaction>> getAllTransactions() {
        return null;
    }

    @Override
    public Observable<Boolean> updateTransaction(Transaction transaction) {
        return null;
    }

    @Override
    public Observable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        return null;
    }

    @Override
    public Observable<Boolean> deleteTransaction(int idAccount, int idTransaction, double amount) {
        return null;
    }

    @Override
    public Observable<Boolean> addNewDebt(Debt debt) {
        return null;
    }

    @Override
    public Observable<List<Debt>> getAllDebts() {
        return null;
    }

    @Override
    public Observable<Boolean> updateDebt(Debt debt) {
        return null;
    }

    @Override
    public Observable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        return null;
    }

    @Override
    public Observable<Boolean> deleteDebt(int idAccount, int idDebt, double amount, int type) {
        return null;
    }

    @Override
    public Observable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return null;
    }

    @Override
    public Observable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return null;
    }

    @Override
    public Observable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        return null;
    }

    @Override
    public Observable<Map<String, double[]>> getTransactionsStatistic(int position) {
        return null;
    }

    @Override
    public Observable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency() {
        return null;
    }

    @Override
    public Observable<Boolean> updateRates(List<Rates> ratesList) {
        return null;
    }

    @Override
    public Observable<double[]> getRates() {
        return null;
    }

    @Override
    public Observable<Boolean> setAllAccounts(List<Account> accountList) {
        return null;
    }

    @Override
    public Observable<Boolean> setAllTransactions(List<Transaction> transactionList) {
        return null;
    }

    @Override
    public Observable<Boolean> setAllDebts(List<Debt> debtList) {
        return null;
    }

    @Override
    public Observable<Boolean> setRates(double[] rates) {
        return null;
    }
}
