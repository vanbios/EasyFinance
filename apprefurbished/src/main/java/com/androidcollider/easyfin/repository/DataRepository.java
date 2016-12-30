package com.androidcollider.easyfin.repository;

import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.Debt;
import com.androidcollider.easyfin.models.Rates;
import com.androidcollider.easyfin.models.Transaction;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Ihor Bilous
 */

public class DataRepository implements Repository {

    private Repository memoryRepository;
    private Repository databaseRepository;

    public DataRepository(@Memory Repository memoryRepository,
                          @Database Repository databaseRepository) {
        this.memoryRepository = memoryRepository;
        this.databaseRepository = databaseRepository;
    }

    @Override
    public Observable<Boolean> addNewAccount(Account account) {
        return Observable.combineLatest(
                databaseRepository.addNewAccount(account),
                memoryRepository.addNewAccount(account),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Account>> getAllAccounts() {
        return memoryRepository.getAllAccounts();
    }

    @Override
    public Observable<Boolean> updateAccount(Account account) {
        return Observable.combineLatest(
                databaseRepository.updateAccount(account),
                memoryRepository.updateAccount(account),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> deleteAccount(int id) {
        return Observable.combineLatest(
                databaseRepository.deleteAccount(id),
                memoryRepository.deleteAccount(id),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> transferBTWAccounts(int idAccount1, double accountAmount1, int idAccount2, double accountAmount2) {
        return Observable.combineLatest(
                databaseRepository.transferBTWAccounts(idAccount1, accountAmount1, idAccount2, accountAmount2),
                memoryRepository.transferBTWAccounts(idAccount1, accountAmount1, idAccount2, accountAmount2),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> addNewTransaction(Transaction transaction) {
        return Observable.combineLatest(
                databaseRepository.addNewTransaction(transaction),
                memoryRepository.addNewTransaction(transaction),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Transaction>> getAllTransactions() {
        return memoryRepository.getAllTransactions();
    }

    @Override
    public Observable<Boolean> updateTransaction(Transaction transaction) {
        return Observable.combineLatest(
                databaseRepository.updateTransaction(transaction),
                memoryRepository.updateTransaction(transaction),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        return Observable.combineLatest(
                databaseRepository.updateTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId),
                memoryRepository.updateTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> deleteTransaction(int idAccount, int idTransaction, double amount) {
        return Observable.combineLatest(
                databaseRepository.deleteTransaction(idAccount, idTransaction, amount),
                memoryRepository.deleteTransaction(idAccount, idTransaction, amount),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> addNewDebt(Debt debt) {
        return Observable.combineLatest(
                databaseRepository.addNewDebt(debt),
                memoryRepository.addNewDebt(debt),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Debt>> getAllDebts() {
        return memoryRepository.getAllDebts();
    }

    @Override
    public Observable<Boolean> updateDebt(Debt debt) {
        return Observable.combineLatest(
                databaseRepository.updateDebt(debt),
                memoryRepository.updateDebt(debt),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        return Observable.combineLatest(
                databaseRepository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId),
                memoryRepository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> deleteDebt(int idAccount, int idDebt, double amount, int type) {
        return Observable.combineLatest(
                databaseRepository.deleteDebt(idAccount, idDebt, amount, type),
                memoryRepository.deleteDebt(idAccount, idDebt, amount, type),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return Observable.combineLatest(
                databaseRepository.payFullDebt(idAccount, accountAmount, idDebt),
                memoryRepository.payFullDebt(idAccount, accountAmount, idDebt),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return Observable.combineLatest(
                databaseRepository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount),
                memoryRepository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        return Observable.combineLatest(
                databaseRepository.takeMoreDebt(idAccount, accountAmount, idDebt, debtAmount, debtAllAmount),
                memoryRepository.takeMoreDebt(idAccount, accountAmount, idDebt, debtAmount, debtAllAmount),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Map<String, double[]>> getTransactionsStatistic(int position) {
        return memoryRepository.getTransactionsStatistic(position);
    }

    @Override
    public Observable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency() {
        return memoryRepository.getAccountsAmountSumGroupByTypeAndCurrency();
    }

    @Override
    public Observable<Boolean> updateRates(List<Rates> ratesList) {
        return Observable.combineLatest(
                databaseRepository.updateRates(ratesList),
                memoryRepository.updateRates(ratesList),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<double[]> getRates() {
        return memoryRepository.getRates();
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
