package com.androidcollider.easyfin.repository.memory;

import android.content.Context;
import android.util.Log;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.DateConstants;
import com.androidcollider.easyfin.models.Debt;
import com.androidcollider.easyfin.models.Rates;
import com.androidcollider.easyfin.models.Transaction;
import com.androidcollider.easyfin.repository.Repository;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

public class MemoryRepository implements Repository {

    private Context context;
    private NumberFormatManager numberFormatManager;
    private List<Account> accountList;
    private List<Transaction> transactionList;
    private List<Debt> debtList;
    private double[] ratesArray;
    private String[] currencyArray;


    public MemoryRepository(Context context, NumberFormatManager numberFormatManager) {
        this.context = context;
        this.numberFormatManager = numberFormatManager;
        currencyArray = context.getResources().getStringArray(R.array.account_currency_array);
    }

    @Override
    public Observable<Account> addNewAccount(Account account) {
        return Observable.<Account>create(subscriber -> {
            accountList.add(account);
            subscriber.onNext(account);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<List<Account>> getAllAccounts() {
        return accountList == null ? null : Observable.just(accountList);
    }

    @Override
    public Observable<Account> updateAccount(Account account) {
        return Observable.<Account>create(subscriber -> {
            int pos = accountList.indexOf(account);
            subscriber.onNext(pos >= 0 ? accountList.set(pos, account) : null);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> deleteAccount(int id) {
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> transferBTWAccounts(int idAccount1, double accountAmount1, int idAccount2, double accountAmount2) {
        return Observable.just(true);
    }

    @Override
    public Observable<Transaction> addNewTransaction(Transaction transaction) {
        return Observable.<Transaction>create(subscriber -> {
            transactionList.add(0, transaction);
            subscriber.onNext(transaction);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<List<Transaction>> getAllTransactions() {
        return transactionList == null ? null : Observable.just(transactionList);
    }

    @Override
    public Observable<Transaction> updateTransaction(Transaction transaction) {
        return Observable.<Transaction>create(subscriber -> {
            int pos = transactionList.indexOf(transaction);
            subscriber.onNext(pos >= 0 ? transactionList.set(pos, transaction) : null);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> deleteTransaction(int idAccount, int idTransaction, double amount) {
        return Observable.just(true);
    }

    @Override
    public Observable<Debt> addNewDebt(Debt debt) {
        return Observable.<Debt>create(subscriber -> {
            debtList.add(debt);
            subscriber.onNext(debt);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<List<Debt>> getAllDebts() {
        return debtList == null ? null : Observable.just(debtList);
    }

    @Override
    public Observable<Debt> updateDebt(Debt debt) {
        return Observable.<Debt>create(subscriber -> {
            int pos = debtList.indexOf(debt);
            subscriber.onNext(pos >= 0 ? debtList.set(pos, debt) : null);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> deleteDebt(int idAccount, int idDebt, double amount, int type) {
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return Observable.just(true);
    }

    @Override
    public Observable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        return Observable.just(true);
    }

    @Override
    public Observable<Map<String, double[]>> getTransactionsStatistic(int position) {
        return transactionList == null ?
                null :
                Observable.<Map<String, double[]>>create(subscriber -> {
                    subscriber.onNext(getTransactionsStatisticByPosition(position));
                    subscriber.onCompleted();
                });
    }

    @Override
    public Observable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency() {
        return accountList == null || debtList == null ?
                null :
                Observable.<Map<String, double[]>>create(subscriber -> {
                    subscriber.onNext(getAccountsSumGroupByTypeAndCurrency());
                    subscriber.onCompleted();
                });
    }

    @Override
    public Observable<Boolean> updateRates(List<Rates> ratesList) {
        return Observable.<Boolean>create(subscriber -> {
            for (int i = 0; i < ratesArray.length; i++) {
                ratesArray[i] = ratesList.get(i).getAsk();
            }
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<double[]> getRates() {
        return ratesArray == null ? null : Observable.just(ratesArray);
    }

    @Override
    public Observable<Boolean> setAllAccounts(List<Account> accountList) {
        return Observable.<Boolean>create(subscriber -> {
            this.accountList = new ArrayList<>();
            this.accountList.addAll(accountList);
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> setAllTransactions(List<Transaction> transactionList) {
        return Observable.<Boolean>create(subscriber -> {
            this.transactionList = new ArrayList<>();
            this.transactionList.addAll(transactionList);
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> setAllDebts(List<Debt> debtList) {
        return Observable.<Boolean>create(subscriber -> {
            this.debtList = new ArrayList<>();
            this.debtList.addAll(debtList);
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> setRates(double[] rates) {
        return Observable.<Boolean>create(subscriber -> {
            this.ratesArray = new double[4];
            System.arraycopy(rates, 0, this.ratesArray, 0, rates.length);
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }


    private Map<String, double[]> getAccountsSumGroupByTypeAndCurrency() {
        Log.d("COLLIDER", "accounts stat memory!");
        Map<String, double[]> results = new HashMap<>();

        for (String currency : currencyArray) {
            List<Account> accountListFilteredByCurrency =
                    Stream.of(accountList)
                            .filter(a -> a.getCurrency().equals(currency))
                            .collect(Collectors.toList());
            double[] result = new double[4];
            double accountSum;
            for (int i = 0; i < 3; i++) {
                final int type = i;
                accountSum = 0;
                List<Account> accountListFilteredByType =
                        Stream.of(accountListFilteredByCurrency)
                                .filter(a -> a.getType() == type)
                                .collect(Collectors.toList());

                for (Account account : accountListFilteredByType) {
                    accountSum += account.getAmount();
                }
                result[i] = accountSum;
            }

            List<Debt> debtListFilteredByCurrency =
                    Stream.of(debtList)
                            .filter(d -> d.getCurrency().equals(currency))
                            .collect(Collectors.toList());

            double debtSum = 0;
            double debtVal;

            for (Debt debt : debtListFilteredByCurrency) {
                debtVal = debt.getAmountCurrent();
                if (debt.getType() == 1) {
                    debtVal *= -1;
                }
                debtSum += debtVal;
            }

            result[3] = debtSum;
            results.put(currency, result);
        }
        return results;
    }

    private Map<String, double[]> getTransactionsStatisticByPosition(int position) {
        Log.d("COLLIDER", "trans stat memory!");
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

        Map<String, double[]> result = new HashMap<>();

        long currentTime = new Date().getTime();

        double cost, income;

        for (String currency : currencyArray) {
            double[] arrStat = new double[2];

            List<Transaction> transactionListFilteredByCurrency =
                    Stream.of(transactionList)
                            .filter(t -> t.getCurrency().equals(currency))
                            .collect(Collectors.toList());

            cost = 0;
            income = 0;

            for (Transaction transaction : transactionListFilteredByCurrency) {
                long date = transaction.getDate();
                double amount = transaction.getAmount();
                if (currentTime > date && period >= (currentTime - date)) {
                    if (numberFormatManager.isDoubleNegative(amount)) {
                        cost += amount;
                    } else {
                        income += amount;
                    }
                }
            }

            arrStat[0] = cost;
            arrStat[1] = income;
            result.put(currency, arrStat);
        }
        return result;
    }
}