package com.androidcollider.easyfin.common.repository;

import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.models.Rates;
import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.models.TransactionCategory;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

public interface Repository {

    // Accounts

    Flowable<Account> addNewAccount(Account account);

    Flowable<List<Account>> getAllAccounts();

    Flowable<Account> updateAccount(Account account);

    Flowable<Boolean> deleteAccount(int id);

    Flowable<Boolean> transferBTWAccounts(int idAccount1,
                                          double accountAmount1,
                                          int idAccount2,
                                          double accountAmount2);

    Flowable<Boolean> setAllAccounts(List<Account> accountList);


    // Transactions

    Flowable<Transaction> addNewTransaction(Transaction transaction);

    Flowable<List<Transaction>> getAllTransactions();

    Flowable<Transaction> updateTransaction(Transaction transaction);

    Flowable<Boolean> updateTransactionDifferentAccounts(Transaction transaction,
                                                         double oldAccountAmount,
                                                         int oldAccountId);

    Flowable<Boolean> deleteTransaction(int idAccount,
                                        int idTransaction,
                                        double amount);

    Flowable<Boolean> setAllTransactions(List<Transaction> transactionList);


    // Debts

    Flowable<Debt> addNewDebt(Debt debt);

    Flowable<List<Debt>> getAllDebts();

    Flowable<Debt> updateDebt(Debt debt);

    Flowable<Boolean> updateDebtDifferentAccounts(Debt debt,
                                                  double oldAccountAmount,
                                                  int oldAccountId);

    Flowable<Boolean> deleteDebt(int idAccount,
                                 int idDebt,
                                 double amount,
                                 int type);

    Flowable<Boolean> payFullDebt(int idAccount,
                                  double accountAmount,
                                  int idDebt);

    Flowable<Boolean> payPartOfDebt(int idAccount,
                                    double accountAmount,
                                    int idDebt,
                                    double debtAmount);

    Flowable<Boolean> takeMoreDebt(int idAccount,
                                   double accountAmount,
                                   int idDebt,
                                   double debtAmount,
                                   double debtAllAmount);

    Flowable<Boolean> setAllDebts(List<Debt> debtList);


    // Statistic

    Flowable<Map<String, double[]>> getTransactionsStatistic(int position);

    Flowable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency();


    // Rates

    Flowable<Boolean> updateRates(List<Rates> ratesList);

    Flowable<double[]> getRates();

    Flowable<Boolean> setRates(double[] rates);


    // Transaction Category

    // Income

    Flowable<TransactionCategory> addNewTransactionIncomeCategory(TransactionCategory transactionCategory);

    Flowable<List<TransactionCategory>> getAllTransactionIncomeCategories();

    Flowable<TransactionCategory> updateTransactionIncomeCategory(TransactionCategory transactionCategory);

    Flowable<Boolean> deleteTransactionIncomeCategory(int id);

    Flowable<Boolean> setAllTransactionIncomeCategories(List<TransactionCategory> transactionCategoryList);


    // Expense

    Flowable<TransactionCategory> addNewTransactionExpenseCategory(TransactionCategory transactionCategory);

    Flowable<List<TransactionCategory>> getAllTransactionExpenseCategories();

    Flowable<TransactionCategory> updateTransactionExpenseCategory(TransactionCategory transactionCategory);

    Flowable<Boolean> deleteTransactionExpenseCategory(int id);

    Flowable<Boolean> setAllTransactionExpenseCategories(List<TransactionCategory> transactionCategoryList);
}