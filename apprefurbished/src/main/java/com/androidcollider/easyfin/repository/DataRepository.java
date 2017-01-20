package com.androidcollider.easyfin.repository;

import com.androidcollider.easyfin.managers.import_export_db.ImportExportDbManager;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.Debt;
import com.androidcollider.easyfin.models.Rates;
import com.androidcollider.easyfin.models.Transaction;
import com.androidcollider.easyfin.repository.database.Database;
import com.androidcollider.easyfin.repository.memory.Memory;
import com.androidcollider.easyfin.utils.BackgroundExecutor;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Ihor Bilous
 */

class DataRepository implements Repository {

    private Repository memoryRepository;
    private Repository databaseRepository;

    private ImportExportDbManager importExportDbManager;

    private Scheduler subscribeSc =
            Schedulers.from(BackgroundExecutor.getSafeBackgroundExecutor())
            //AndroidSchedulers.mainThread()
            ;
    private Scheduler observeSc = AndroidSchedulers.mainThread();


    DataRepository(@Memory Repository memoryRepository,
                   @Database Repository databaseRepository,
                   ImportExportDbManager importExportDbManager) {
        this.memoryRepository = memoryRepository;
        this.databaseRepository = databaseRepository;
        this.importExportDbManager = importExportDbManager;
    }

    @Override
    public Observable<Account> addNewAccount(Account account) {
        return databaseRepository.addNewAccount(account)
                .flatMap(account1 -> memoryRepository.addNewAccount(account1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<List<Account>> getAllAccounts() {
        Observable<List<Account>> memoryObservable = memoryRepository.getAllAccounts();
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                Observable.create((Observable.OnSubscribe<List<Account>>) subscriber ->
                        databaseRepository.getAllAccounts()
                                .subscribe(accounts -> {
                                    memoryRepository.setAllAccounts(accounts)
                                            .subscribe(aBoolean -> {
                                                subscriber.onNext(accounts);
                                                subscriber.onCompleted();
                                            });
                                }))
                        .subscribeOn(subscribeSc)
                        .observeOn(observeSc);
    }

    @Override
    public Observable<Account> updateAccount(Account account) {
        return databaseRepository.updateAccount(account)
                .flatMap(account1 -> memoryRepository.updateAccount(account1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Boolean> deleteAccount(int id) {
        return Observable.combineLatest(
                databaseRepository.deleteAccount(id),
                memoryRepository.deleteAccount(id),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Boolean> transferBTWAccounts(int idAccount1, double accountAmount1, int idAccount2, double accountAmount2) {
        return Observable.combineLatest(
                databaseRepository.transferBTWAccounts(idAccount1, accountAmount1, idAccount2, accountAmount2),
                memoryRepository.transferBTWAccounts(idAccount1, accountAmount1, idAccount2, accountAmount2),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Transaction> addNewTransaction(Transaction transaction) {
        return databaseRepository.addNewTransaction(transaction)
                .flatMap(transaction1 -> memoryRepository.addNewTransaction(transaction1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<List<Transaction>> getAllTransactions() {
        Observable<List<Transaction>> memoryObservable = memoryRepository.getAllTransactions();
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                Observable.create((Observable.OnSubscribe<List<Transaction>>) subscriber ->
                        databaseRepository.getAllTransactions()
                                .subscribe(transactions -> {
                                    memoryRepository.setAllTransactions(transactions)
                                            .subscribe(aBoolean -> {
                                                subscriber.onNext(transactions);
                                                subscriber.onCompleted();
                                            });
                                }))
                        .subscribeOn(subscribeSc)
                        .observeOn(observeSc);
    }

    @Override
    public Observable<Transaction> updateTransaction(Transaction transaction) {
        return databaseRepository.updateTransaction(transaction)
                .flatMap(transaction1 -> memoryRepository.updateTransaction(transaction1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        return Observable.combineLatest(
                databaseRepository.updateTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId),
                memoryRepository.updateTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Boolean> deleteTransaction(int idAccount, int idTransaction, double amount) {
        return Observable.combineLatest(
                databaseRepository.deleteTransaction(idAccount, idTransaction, amount),
                memoryRepository.deleteTransaction(idAccount, idTransaction, amount),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Debt> addNewDebt(Debt debt) {
        return databaseRepository.addNewDebt(debt)
                .flatMap(debt1 -> memoryRepository.addNewDebt(debt1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<List<Debt>> getAllDebts() {
        Observable<List<Debt>> memoryObservable = memoryRepository.getAllDebts();
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                Observable.create((Observable.OnSubscribe<List<Debt>>) subscriber ->
                        databaseRepository.getAllDebts()
                                .subscribe(debts -> {
                                    memoryRepository.setAllDebts(debts)
                                            .subscribe(aBoolean -> {
                                                subscriber.onNext(debts);
                                                subscriber.onCompleted();
                                            });
                                }))
                        .subscribeOn(subscribeSc)
                        .observeOn(observeSc);
    }

    @Override
    public Observable<Debt> updateDebt(Debt debt) {
        return databaseRepository.updateDebt(debt)
                .flatMap(debt1 -> memoryRepository.updateDebt(debt1))
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        return Observable.combineLatest(
                databaseRepository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId),
                memoryRepository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Boolean> deleteDebt(int idAccount, int idDebt, double amount, int type) {
        return Observable.combineLatest(
                databaseRepository.deleteDebt(idAccount, idDebt, amount, type),
                memoryRepository.deleteDebt(idAccount, idDebt, amount, type),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return Observable.combineLatest(
                databaseRepository.payFullDebt(idAccount, accountAmount, idDebt),
                memoryRepository.payFullDebt(idAccount, accountAmount, idDebt),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return Observable.combineLatest(
                databaseRepository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount),
                memoryRepository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        return Observable.combineLatest(
                databaseRepository.takeMoreDebt(idAccount, accountAmount, idDebt, debtAmount, debtAllAmount),
                memoryRepository.takeMoreDebt(idAccount, accountAmount, idDebt, debtAmount, debtAllAmount),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<Map<String, double[]>> getTransactionsStatistic(int position) {
        Observable<Map<String, double[]>> memoryObservable = memoryRepository.getTransactionsStatistic(position);
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                databaseRepository.getTransactionsStatistic(position);
    }

    @Override
    public Observable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency() {
        Observable<Map<String, double[]>> memoryObservable = memoryRepository.getAccountsAmountSumGroupByTypeAndCurrency();
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                databaseRepository.getAccountsAmountSumGroupByTypeAndCurrency();
    }

    @Override
    public Observable<Boolean> updateRates(List<Rates> ratesList) {
        return Observable.combineLatest(
                databaseRepository.updateRates(ratesList),
                memoryRepository.updateRates(ratesList),
                (aBoolean, aBoolean2) -> aBoolean && aBoolean2)
                .subscribeOn(subscribeSc)
                .observeOn(observeSc);
    }

    @Override
    public Observable<double[]> getRates() {
        Observable<double[]> memoryObservable = memoryRepository.getRates();
        return memoryObservable != null && !isDataExpired() ?
                memoryObservable :
                Observable.create((Observable.OnSubscribe<double[]>) subscriber ->
                        databaseRepository.getRates()
                                .subscribe(rates -> {
                                    memoryRepository.setRates(rates)
                                            .subscribe(aBoolean -> {
                                                subscriber.onNext(rates);
                                                subscriber.onCompleted();
                                            });
                                }))
                        .subscribeOn(subscribeSc)
                        .observeOn(observeSc);
    }

    @Override
    public Observable<Boolean> setAllAccounts(List<Account> accountList) {
        return memoryRepository.setAllAccounts(accountList);
    }

    @Override
    public Observable<Boolean> setAllTransactions(List<Transaction> transactionList) {
        return memoryRepository.setAllTransactions(transactionList);
    }

    @Override
    public Observable<Boolean> setAllDebts(List<Debt> debtList) {
        return memoryRepository.setAllDebts(debtList);
    }

    @Override
    public Observable<Boolean> setRates(double[] rates) {
        return memoryRepository.setRates(rates);
    }


    private boolean isDataExpired() {
        return importExportDbManager.isDBExpired();
    }
}