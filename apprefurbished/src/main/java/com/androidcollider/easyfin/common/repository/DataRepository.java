package com.androidcollider.easyfin.common.repository;

import android.util.Log;

import com.androidcollider.easyfin.common.managers.import_export_db.ImportExportDbManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.Data;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.models.Rates;
import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.repository.database.Database;
import com.androidcollider.easyfin.common.repository.memory.Memory;
import com.androidcollider.easyfin.common.utils.BackgroundExecutor;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Ihor Bilous
 */

class DataRepository implements Repository {

    private static final String TAG = DataRepository.class.getSimpleName();

    private Repository memoryRepository;
    private Repository databaseRepository;

    private ImportExportDbManager importExportDbManager;

    private Scheduler subscribeSc = Schedulers.from(BackgroundExecutor.getSafeBackgroundExecutor());
    private Scheduler observeSc = AndroidSchedulers.mainThread();


    DataRepository(@Memory Repository memoryRepository,
                   @Database Repository databaseRepository,
                   ImportExportDbManager importExportDbManager) {
        this.memoryRepository = memoryRepository;
        this.databaseRepository = databaseRepository;
        this.importExportDbManager = importExportDbManager;
    }

    @Override
    public Flowable<Account> addNewAccount(Account account) {
        return databaseRepository.addNewAccount(account)
                .flatMap(account1 -> memoryRepository.addNewAccount(account1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<List<Account>> getAllAccounts() {
        Flowable<List<Account>> memoryObservable = memoryRepository.getAllAccounts();
        Log.d(TAG, "getAllAccounts " + String.valueOf(memoryObservable != null));
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                loadAllDataFromDB()
                        .flatMap(aBoolean -> {
                            if (aBoolean) importExportDbManager.setDBExpired(false);
                            return aBoolean ?
                                    memoryRepository.getAllAccounts() :
                                    databaseRepository.getAllAccounts();
                        })
                        .subscribeOn(subscribeSc)
                        .observeOn(observeSc);
    }

    @Override
    public Flowable<Account> updateAccount(Account account) {
        return databaseRepository.updateAccount(account)
                .flatMap(account1 -> memoryRepository.updateAccount(account1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Boolean> deleteAccount(int id) {
        return Flowable.combineLatest(
                databaseRepository.deleteAccount(id),
                memoryRepository.deleteAccount(id),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Boolean> transferBTWAccounts(int idAccount1, double accountAmount1, int idAccount2, double accountAmount2) {
        return Flowable.combineLatest(
                databaseRepository.transferBTWAccounts(idAccount1, accountAmount1, idAccount2, accountAmount2),
                memoryRepository.transferBTWAccounts(idAccount1, accountAmount1, idAccount2, accountAmount2),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Transaction> addNewTransaction(Transaction transaction) {
        return databaseRepository.addNewTransaction(transaction)
                .flatMap(transaction1 -> memoryRepository.addNewTransaction(transaction1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<List<Transaction>> getAllTransactions() {
        Flowable<List<Transaction>> memoryObservable = memoryRepository.getAllTransactions();
        Log.d(TAG, "getAllTransactions " + String.valueOf(memoryObservable != null));
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                loadAllDataFromDB()
                        .flatMap(aBoolean -> {
                            if (aBoolean) importExportDbManager.setDBExpired(false);
                            return aBoolean ?
                                    memoryRepository.getAllTransactions() :
                                    databaseRepository.getAllTransactions();
                        })
                        .subscribeOn(subscribeSc)
                        .observeOn(observeSc);
    }

    @Override
    public Flowable<Transaction> updateTransaction(Transaction transaction) {
        return databaseRepository.updateTransaction(transaction)
                .flatMap(transaction1 -> memoryRepository.updateTransaction(transaction1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        return Flowable.combineLatest(
                databaseRepository.updateTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId),
                memoryRepository.updateTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Boolean> deleteTransaction(int idAccount, int idTransaction, double amount) {
        return Flowable.combineLatest(
                databaseRepository.deleteTransaction(idAccount, idTransaction, amount),
                memoryRepository.deleteTransaction(idAccount, idTransaction, amount),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Debt> addNewDebt(Debt debt) {
        return databaseRepository.addNewDebt(debt)
                .flatMap(debt1 -> memoryRepository.addNewDebt(debt1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<List<Debt>> getAllDebts() {
        Flowable<List<Debt>> memoryObservable = memoryRepository.getAllDebts();
        Log.d(TAG, "getAllDebts " + String.valueOf(memoryObservable != null));
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                loadAllDataFromDB()
                        .flatMap(aBoolean -> {
                            if (aBoolean) importExportDbManager.setDBExpired(false);
                            return aBoolean ?
                                    memoryRepository.getAllDebts() :
                                    databaseRepository.getAllDebts();
                        })
                        .subscribeOn(subscribeSc)
                        .observeOn(observeSc);
    }

    @Override
    public Flowable<Debt> updateDebt(Debt debt) {
        return databaseRepository.updateDebt(debt)
                .flatMap(debt1 -> memoryRepository.updateDebt(debt1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        return Flowable.combineLatest(
                databaseRepository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId),
                memoryRepository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Boolean> deleteDebt(int idAccount, int idDebt, double amount, int type) {
        return Flowable.combineLatest(
                databaseRepository.deleteDebt(idAccount, idDebt, amount, type),
                memoryRepository.deleteDebt(idAccount, idDebt, amount, type),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return Flowable.combineLatest(
                databaseRepository.payFullDebt(idAccount, accountAmount, idDebt),
                memoryRepository.payFullDebt(idAccount, accountAmount, idDebt),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return Flowable.combineLatest(
                databaseRepository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount),
                memoryRepository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        return Flowable.combineLatest(
                databaseRepository.takeMoreDebt(idAccount, accountAmount, idDebt, debtAmount, debtAllAmount),
                memoryRepository.takeMoreDebt(idAccount, accountAmount, idDebt, debtAmount, debtAllAmount),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<Map<String, double[]>> getTransactionsStatistic(int position) {
        Flowable<Map<String, double[]>> memoryObservable = memoryRepository.getTransactionsStatistic(position);
        Log.d(TAG, "getTransactionsStatistic " + String.valueOf(memoryObservable != null));
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                loadAllDataFromDB()
                        .flatMap(aBoolean -> {
                            if (aBoolean) importExportDbManager.setDBExpired(false);
                            return aBoolean ?
                                    databaseRepository.getTransactionsStatistic(position) :
                                    databaseRepository.getTransactionsStatistic(position);
                        });
    }

    @Override
    public Flowable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency() {
        Flowable<Map<String, double[]>> memoryObservable = memoryRepository.getAccountsAmountSumGroupByTypeAndCurrency();
        Log.d(TAG, "getAccountsAmountSumGroupByTypeAndCurrency " + String.valueOf(memoryObservable != null));
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                loadAllDataFromDB()
                        .flatMap(aBoolean -> {
                            if (aBoolean) importExportDbManager.setDBExpired(false);
                            return aBoolean ?
                                    memoryRepository.getAccountsAmountSumGroupByTypeAndCurrency() :
                                    databaseRepository.getAccountsAmountSumGroupByTypeAndCurrency();
                        });
    }

    @Override
    public Flowable<Boolean> updateRates(List<Rates> ratesList) {
        return Flowable.combineLatest(
                databaseRepository.updateRates(ratesList),
                memoryRepository.updateRates(ratesList),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Flowable<double[]> getRates() {
        Flowable<double[]> memoryObservable = memoryRepository.getRates();
        Log.d(TAG, "getRates " + String.valueOf(memoryObservable != null));
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                Flowable.unsafeCreate((Publisher<double[]>) s ->
                        databaseRepository.getRates()
                                .subscribe(
                                        rates -> {
                                            memoryRepository.setRates(rates)
                                                    .subscribe(aBoolean -> {
                                                                s.onNext(rates);
                                                                s.onComplete();
                                                            },
                                                            Throwable::printStackTrace
                                                    );
                                        }))
                        .subscribeOn(subscribeSc)
                        .observeOn(observeSc);
    }

    @Override
    public Flowable<Boolean> setAllAccounts(List<Account> accountList) {
        return memoryRepository.setAllAccounts(accountList);
    }

    @Override
    public Flowable<Boolean> setAllTransactions(List<Transaction> transactionList) {
        return memoryRepository.setAllTransactions(transactionList);
    }

    @Override
    public Flowable<Boolean> setAllDebts(List<Debt> debtList) {
        return memoryRepository.setAllDebts(debtList);
    }

    @Override
    public Flowable<Boolean> setRates(double[] rates) {
        return memoryRepository.setRates(rates);
    }


    private boolean isDataExpired() {
        return importExportDbManager.isDBExpired();
    }

    private Flowable<Boolean> loadAllDataFromDB() {
        return Flowable.combineLatest(
                databaseRepository.getAllAccounts(),
                databaseRepository.getAllTransactions(),
                databaseRepository.getAllDebts(),
                databaseRepository.getRates(),
                Data::new
        )
                .flatMap(data ->
                        Flowable.combineLatest(
                                memoryRepository.setAllAccounts(data.getAccountList()),
                                memoryRepository.setAllTransactions(data.getTransactionList()),
                                memoryRepository.setAllDebts(data.getDebtList()),
                                memoryRepository.setRates(data.getRatesArray()),
                                (aBoolean, aBoolean2, aBoolean3, aBoolean4) ->
                                        aBoolean && aBoolean2 && aBoolean3 && aBoolean4
                        ))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }
}