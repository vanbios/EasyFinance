package com.androidcollider.easyfin.common.repository.memory;

import android.util.Log;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.DateConstants;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.models.Rates;
import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.androidcollider.easyfin.common.repository.Repository;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

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
    private List<TransactionCategory> transactionCategoryIncomeList;
    private List<TransactionCategory> transactionCategoryExpenseList;


    public MemoryRepository(NumberFormatManager numberFormatManager,
                            ResourcesManager resourcesManager) {
        this.numberFormatManager = numberFormatManager;
        currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
    }

    @Override
    public Flowable<Account> addNewAccount(Account account) {
        return Flowable.fromCallable(() -> {
            accountList.add(account);
            return account;
        });
    }

    @Override
    public Flowable<List<Account>> getAllAccounts() {
        Log.d(TAG, "getAllAccounts");
        return accountList == null ? null : Flowable.just(accountList);
    }

    @Override
    public Flowable<Account> updateAccount(Account account) {
        return Flowable.fromCallable(() -> {
            int pos = accountList.indexOf(account);
            return pos >= 0 ? accountList.set(pos, account) : null;
        });
    }

    @Override
    public Flowable<Boolean> deleteAccount(int id) {
        return Flowable.fromCallable(() -> {
            int pos = getAccountPosById(id);
            boolean b = pos != -1;
            if (b) accountList.remove(pos);
            return b;
        });
    }

    @Override
    public Flowable<Boolean> transferBTWAccounts(int idAccount1, double accountAmount1, int idAccount2, double accountAmount2) {
        return Flowable.fromCallable(() -> {
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
            return b1 && b2;
        });
    }

    @Override
    public Flowable<Transaction> addNewTransaction(Transaction transaction) {
        return Flowable.fromCallable(() -> {
            transactionList.add(0, transaction);
            int pos = getAccountPosById(transaction.getIdAccount());
            if (pos != -1) {
                accountList.get(pos).setAmount(transaction.getAccountAmount());
            }
            return transaction;
        });
    }

    @Override
    public Flowable<List<Transaction>> getAllTransactions() {
        Log.d(TAG, "getAllTransactions");
        return transactionList == null ? null : Flowable.just(transactionList);
    }

    @Override
    public Flowable<Transaction> updateTransaction(Transaction transaction) {
        return Flowable.fromCallable(() -> {
            int pos = getAccountPosById(transaction.getIdAccount());
            if (pos != -1) {
                accountList.get(pos).setAmount(transaction.getAccountAmount());
            }
            pos = transactionList.indexOf(transaction);
            return pos >= 0 ? transactionList.set(pos, transaction) : null;
        });
    }

    @Override
    public Flowable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        return Flowable.fromCallable(() -> {
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
            return b1 && b2 && b3;
        });
    }

    @Override
    public Flowable<Boolean> deleteTransaction(int idAccount, int idTransaction, double amount) {
        return Flowable.fromCallable(() -> {
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
            return b;
        });
    }

    @Override
    public Flowable<Debt> addNewDebt(Debt debt) {
        return Flowable.fromCallable(() -> {
            debtList.add(debt);
            int pos = getAccountPosById(debt.getIdAccount());
            if (pos != -1) {
                accountList.get(pos).setAmount(debt.getAccountAmount());
            }
            return debt;
        });
    }

    @Override
    public Flowable<List<Debt>> getAllDebts() {
        Log.d(TAG, "getAllDebts");
        return debtList == null ? null : Flowable.just(debtList);
    }

    @Override
    public Flowable<Debt> updateDebt(Debt debt) {
        return Flowable.fromCallable(() -> {
            int pos = getAccountPosById(debt.getIdAccount());
            if (pos != -1) {
                accountList.get(pos).setAmount(debt.getAccountAmount());
            }
            pos = debtList.indexOf(debt);
            return pos >= 0 ? debtList.set(pos, debt) : null;
        });
    }

    @Override
    public Flowable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        return Flowable.fromCallable(() -> {
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
            return b1 && b2 && b3;
        });
    }

    @Override
    public Flowable<Boolean> deleteDebt(int idAccount, int idDebt, double amount, int type) {
        return Flowable.fromCallable(() -> {
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
            return b;
        });
    }

    @Override
    public Flowable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return Flowable.fromCallable(() -> {
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
            return b;
        });
    }

    @Override
    public Flowable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return Flowable.fromCallable(() -> {
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
            return b;
        });
    }

    @Override
    public Flowable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        return Flowable.fromCallable(() -> {
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
            return b;
        });
    }

    @Override
    public Flowable<Map<String, double[]>> getTransactionsStatistic(int position) {
        return transactionList == null ?
                null :
                Flowable.fromCallable(() -> getTransactionsStatisticByPosition(position));
    }

    @Override
    public Flowable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency() {
        return accountList == null || debtList == null ?
                null :
                Flowable.fromCallable(this::getAccountsSumGroupByTypeAndCurrency);
    }

    @Override
    public Flowable<Boolean> updateRates(List<Rates> ratesList) {
        return Flowable.fromCallable(() -> {
            for (int i = 0; i < ratesArray.length; i++) {
                ratesArray[i] = ratesList.get(i).getAsk();
            }
            return true;
        });
    }

    @Override
    public Flowable<double[]> getRates() {
        Log.d(TAG, "getRates");
        return ratesArray == null ? null : Flowable.just(ratesArray);
    }

    @Override
    public Flowable<Boolean> setAllAccounts(List<Account> accountList) {
        return Flowable.fromCallable(() -> {
            this.accountList = new ArrayList<>();
            this.accountList.addAll(accountList);
            return true;
        });
    }

    @Override
    public Flowable<Boolean> setAllTransactions(List<Transaction> transactionList) {
        return Flowable.fromCallable(() -> {
            this.transactionList = new ArrayList<>();
            this.transactionList.addAll(transactionList);
            return true;
        });
    }

    @Override
    public Flowable<Boolean> setAllDebts(List<Debt> debtList) {
        return Flowable.fromCallable(() -> {
            this.debtList = new ArrayList<>();
            this.debtList.addAll(debtList);
            return true;
        });
    }

    @Override
    public Flowable<Boolean> setRates(double[] rates) {
        return Flowable.fromCallable(() -> {
            this.ratesArray = new double[4];
            System.arraycopy(rates, 0, this.ratesArray, 0, rates.length);
            return true;
        });
    }

    @Override
    public Flowable<TransactionCategory> addNewTransactionIncomeCategory(TransactionCategory transactionCategory) {
        return Flowable.fromCallable(() -> {
            transactionCategoryIncomeList.add(transactionCategory);
            return transactionCategory;
        });
    }

    @Override
    public Flowable<List<TransactionCategory>> getAllTransactionIncomeCategories() {
        return transactionCategoryIncomeList == null ? null : Flowable.just(transactionCategoryIncomeList);
    }

    @Override
    public Flowable<TransactionCategory> updateTransactionIncomeCategory(TransactionCategory transactionCategory) {
        return Flowable.fromCallable(() -> {
            int pos = transactionCategoryIncomeList.indexOf(transactionCategory);
            return pos >= 0 ? transactionCategoryIncomeList.set(pos, transactionCategory) : null;
        });
    }

    @Override
    public Flowable<Boolean> deleteTransactionIncomeCategory(int id) {
        return Flowable.fromCallable(() -> {
            int pos = getTransactionCategoryIncomePosById(id);
            boolean b = pos != -1;
            if (b) transactionCategoryIncomeList.remove(pos);
            return b;
        });
    }

    @Override
    public Flowable<Boolean> setAllTransactionIncomeCategories(List<TransactionCategory> transactionCategoryList) {
        return Flowable.fromCallable(() -> {
            this.transactionCategoryIncomeList = new ArrayList<>();
            this.transactionCategoryIncomeList.addAll(transactionCategoryList);
            return true;
        });
    }

    @Override
    public Flowable<TransactionCategory> addNewTransactionExpenseCategory(TransactionCategory transactionCategory) {
        return Flowable.fromCallable(() -> {
            transactionCategoryExpenseList.add(transactionCategory);
            return transactionCategory;
        });
    }

    @Override
    public Flowable<List<TransactionCategory>> getAllTransactionExpenseCategories() {
        return transactionCategoryExpenseList == null ? null : Flowable.just(transactionCategoryExpenseList);
    }

    @Override
    public Flowable<TransactionCategory> updateTransactionExpenseCategory(TransactionCategory transactionCategory) {
        return Flowable.fromCallable(() -> {
            int pos = transactionCategoryExpenseList.indexOf(transactionCategory);
            return pos >= 0 ? transactionCategoryExpenseList.set(pos, transactionCategory) : null;
        });
    }

    @Override
    public Flowable<Boolean> deleteTransactionExpenseCategory(int id) {
        return Flowable.fromCallable(() -> {
            int pos = getTransactionCategoryExpensePosById(id);
            boolean b = pos != -1;
            if (b) transactionCategoryExpenseList.remove(pos);
            return b;
        });
    }

    @Override
    public Flowable<Boolean> setAllTransactionExpenseCategories(List<TransactionCategory> transactionCategoryList) {
        return Flowable.fromCallable(() -> {
            this.transactionCategoryExpenseList = new ArrayList<>();
            this.transactionCategoryExpenseList.addAll(transactionCategoryList);
            return true;
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

    private int getTransactionCategoryIncomePosById(int id) {
        for (int i = 0; i < transactionCategoryIncomeList.size(); i++) {
            if (transactionCategoryIncomeList.get(i).getId() == id) return i;
        }
        return -1;
    }

    private int getTransactionCategoryExpensePosById(int id) {
        for (int i = 0; i < transactionCategoryExpenseList.size(); i++) {
            if (transactionCategoryExpenseList.get(i).getId() == id) return i;
        }
        return -1;
    }
}