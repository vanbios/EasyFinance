package com.androidcollider.easyfin.common.repository.memory;

import android.util.Log;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.DateConstants;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.models.Rates;
import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.repository.Repository;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

public class MemoryRepository implements Repository {

    private static final String TAG = MemoryRepository.class.getSimpleName();

    private NumberFormatManager numberFormatManager;
    private List<Account> accountList;
    private List<Transaction> transactionList;
    private List<Debt> debtList;
    private double[] ratesArray;
    private String[] currencyArray;


    public MemoryRepository(NumberFormatManager numberFormatManager,
                            ResourcesManager resourcesManager) {
        this.numberFormatManager = numberFormatManager;
        currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
    }

    @Override
    public Observable<Account> addNewAccount(Account account) {
        return Observable.create(subscriber -> {
            accountList.add(account);
            subscriber.onNext(account);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<List<Account>> getAllAccounts() {
        Log.d(TAG, "getAllAccounts");
        return accountList == null ? null : Observable.just(accountList);
    }

    @Override
    public Observable<Account> updateAccount(Account account) {
        return Observable.create(subscriber -> {
            int pos = accountList.indexOf(account);
            subscriber.onNext(pos >= 0 ? accountList.set(pos, account) : null);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> deleteAccount(int id) {
        return Observable.create(subscriber -> {
            int pos = getAccountPosById(id);
            boolean b = pos != -1;
            if (b) accountList.remove(pos);
            subscriber.onNext(b);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> transferBTWAccounts(int idAccount1, double accountAmount1, int idAccount2, double accountAmount2) {
        return Observable.create(subscriber -> {
            int pos = getAccountPosById(idAccount1);
            boolean b1 = pos != -1;
            if (b1) {
                accountList.get(pos).setAmount(accountAmount1);
            }
            pos = getAccountPosById(idAccount2);
            boolean b2 = pos != -1;
            if (b2) {
                accountList.get(pos).setAmount(accountAmount2);
            }
            subscriber.onNext(b1 && b2);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Transaction> addNewTransaction(Transaction transaction) {
        return Observable.create(subscriber -> {
            transactionList.add(0, transaction);
            int pos = getAccountPosById(transaction.getIdAccount());
            if (pos != -1) {
                accountList.get(pos).setAmount(transaction.getAccountAmount());
            }
            subscriber.onNext(transaction);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<List<Transaction>> getAllTransactions() {
        Log.d(TAG, "getAllTransactions");
        return transactionList == null ? null : Observable.just(transactionList);
    }

    @Override
    public Observable<Transaction> updateTransaction(Transaction transaction) {
        return Observable.create(subscriber -> {
            int pos = getAccountPosById(transaction.getIdAccount());
            if (pos != -1) {
                accountList.get(pos).setAmount(transaction.getAccountAmount());
            }
            pos = transactionList.indexOf(transaction);
            subscriber.onNext(pos >= 0 ? transactionList.set(pos, transaction) : null);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        return Observable.create(subscriber -> {
            int pos = getAccountPosById(transaction.getIdAccount());
            boolean b1 = pos != -1;
            if (b1) {
                accountList.get(pos).setAmount(transaction.getAccountAmount());
            }
            pos = getAccountPosById(oldAccountId);
            boolean b2 = pos != -1;
            if (b2) {
                accountList.get(pos).setAmount(oldAccountAmount);
            }
            pos = transactionList.indexOf(transaction);
            boolean b3 = pos != -1 && transactionList.set(pos, transaction) != null;
            subscriber.onNext(b1 && b2 && b3);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> deleteTransaction(int idAccount, int idTransaction, double amount) {
        return Observable.create(subscriber -> {
            int pos = getTransactionPosById(idTransaction);
            boolean b = pos != -1;
            if (b) {
                transactionList.remove(pos);
                pos = getAccountPosById(idAccount);
                b = pos != -1;
                if (b) {
                    accountList.get(pos).setAmount(accountList.get(pos).getAmount() - amount);
                }
            }
            subscriber.onNext(b);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Debt> addNewDebt(Debt debt) {
        return Observable.create(subscriber -> {
            debtList.add(debt);
            int pos = getAccountPosById(debt.getIdAccount());
            if (pos != -1) {
                accountList.get(pos).setAmount(debt.getAccountAmount());
            }
            subscriber.onNext(debt);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<List<Debt>> getAllDebts() {
        Log.d(TAG, "getAllDebts");
        return debtList == null ? null : Observable.just(debtList);
    }

    @Override
    public Observable<Debt> updateDebt(Debt debt) {
        return Observable.create(subscriber -> {
            int pos = getAccountPosById(debt.getIdAccount());
            if (pos != -1) {
                accountList.get(pos).setAmount(debt.getAccountAmount());
            }
            pos = debtList.indexOf(debt);
            subscriber.onNext(pos >= 0 ? debtList.set(pos, debt) : null);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        return Observable.create(subscriber -> {
            int pos = getAccountPosById(debt.getIdAccount());
            boolean b1 = pos != -1;
            if (b1) {
                accountList.get(pos).setAmount(debt.getAccountAmount());
            }
            pos = getAccountPosById(oldAccountId);
            boolean b2 = pos != -1;
            if (b2) {
                accountList.get(pos).setAmount(oldAccountAmount);
            }
            pos = debtList.indexOf(debt);
            boolean b3 = pos != -1 && debtList.set(pos, debt) != null;
            subscriber.onNext(b1 && b2 && b3);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> deleteDebt(int idAccount, int idDebt, double amount, int type) {
        return Observable.create(subscriber -> {
            int pos = getDebtPosById(idDebt);
            boolean b = pos != -1;
            if (b) {
                debtList.remove(pos);
                pos = getAccountPosById(idAccount);
                b = pos != -1;
                if (b) {
                    accountList.get(pos).setAmount(type == 1 ?
                            accountList.get(pos).getAmount() - amount :
                            accountList.get(pos).getAmount() + amount
                    );
                }
            }
            subscriber.onNext(b);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return Observable.create(subscriber -> {
            int pos = getDebtPosById(idDebt);
            boolean b = pos != -1;
            if (b) {
                debtList.remove(pos);
                pos = getAccountPosById(idAccount);
                b = pos != -1;
                if (b) {
                    accountList.get(pos).setAmount(accountAmount);
                }
            }
            subscriber.onNext(b);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return Observable.create(subscriber -> {
            int pos = getDebtPosById(idDebt);
            boolean b = pos != -1;
            if (b) {
                debtList.get(pos).setAmountCurrent(debtAmount);
                pos = getAccountPosById(idAccount);
                b = pos != -1;
                if (b) {
                    accountList.get(pos).setAmount(accountAmount);
                }
            }
            subscriber.onNext(b);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        return Observable.create(subscriber -> {
            int pos = getDebtPosById(idDebt);
            boolean b = pos != -1;
            if (b) {
                debtList.get(pos).setAmountCurrent(debtAmount);
                debtList.get(pos).setAmountAll(debtAllAmount);
                pos = getAccountPosById(idAccount);
                b = pos != -1;
                if (b) {
                    accountList.get(pos).setAmount(accountAmount);
                }
            }
            subscriber.onNext(b);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Map<String, double[]>> getTransactionsStatistic(int position) {
        return transactionList == null ?
                null :
                Observable.create(subscriber -> {
                    subscriber.onNext(getTransactionsStatisticByPosition(position));
                    subscriber.onCompleted();
                });
    }

    @Override
    public Observable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency() {
        return accountList == null || debtList == null ?
                null :
                Observable.create(subscriber -> {
                    subscriber.onNext(getAccountsSumGroupByTypeAndCurrency());
                    subscriber.onCompleted();
                });
    }

    @Override
    public Observable<Boolean> updateRates(List<Rates> ratesList) {
        return Observable.create(subscriber -> {
            for (int i = 0; i < ratesArray.length; i++) {
                ratesArray[i] = ratesList.get(i).getAsk();
            }
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<double[]> getRates() {
        Log.d(TAG, "getRates");
        return ratesArray == null ? null : Observable.just(ratesArray);
    }

    @Override
    public Observable<Boolean> setAllAccounts(List<Account> accountList) {
        return Observable.create(subscriber -> {
            this.accountList = new ArrayList<>();
            this.accountList.addAll(accountList);
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> setAllTransactions(List<Transaction> transactionList) {
        return Observable.create(subscriber -> {
            this.transactionList = new ArrayList<>();
            this.transactionList.addAll(transactionList);
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> setAllDebts(List<Debt> debtList) {
        return Observable.create(subscriber -> {
            this.debtList = new ArrayList<>();
            this.debtList.addAll(debtList);
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }

    @Override
    public Observable<Boolean> setRates(double[] rates) {
        return Observable.create(subscriber -> {
            this.ratesArray = new double[4];
            System.arraycopy(rates, 0, this.ratesArray, 0, rates.length);
            subscriber.onNext(true);
            subscriber.onCompleted();
        });
    }


    private Map<String, double[]> getAccountsSumGroupByTypeAndCurrency() {
        Log.d(TAG, "getAccountsSumGroupByTypeAndCurrency");
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
        Log.d(TAG, "getTransactionsStatisticByPosition");
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

        Map<String, double[]> result = new HashMap<>();
        long currentTime = System.currentTimeMillis();
        double cost, income;

        for (String currency : currencyArray) {
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

            result.put(currency, new double[]{cost, income});
        }
        return result;
    }

    private int getAccountPosById(int id) {
        for (int i = 0; i < accountList.size(); i++) {
            if (accountList.get(i).getId() == id) return i;
        }
        return -1;
    }

    private int getTransactionPosById(int id) {
        for (int i = 0; i < transactionList.size(); i++) {
            if (transactionList.get(i).getId() == id) return i;
        }
        return -1;
    }

    private int getDebtPosById(int id) {
        for (int i = 0; i < debtList.size(); i++) {
            if (debtList.get(i).getId() == id) return i;
        }
        return -1;
    }
}