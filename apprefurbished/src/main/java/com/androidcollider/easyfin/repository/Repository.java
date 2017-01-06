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

public interface Repository {

    // Accounts

    Observable<Account> addNewAccount(Account account);

    Observable<List<Account>> getAllAccounts();

    Observable<Account> updateAccount(Account account);

    Observable<Boolean> deleteAccount(int id);

    Observable<Boolean> transferBTWAccounts(int idAccount1,
                                            double accountAmount1,
                                            int idAccount2,
                                            double accountAmount2);

    Observable<Boolean> setAllAccounts(List<Account> accountList);


    // Transactions

    Observable<Transaction> addNewTransaction(Transaction transaction);

    Observable<List<Transaction>> getAllTransactions();

    Observable<Transaction> updateTransaction(Transaction transaction);

    Observable<Boolean> updateTransactionDifferentAccounts(Transaction transaction,
                                                           double oldAccountAmount,
                                                           int oldAccountId);

    Observable<Boolean> deleteTransaction(int idAccount,
                                          int idTransaction,
                                          double amount);

    Observable<Boolean> setAllTransactions(List<Transaction> transactionList);


    // Debts

    Observable<Debt> addNewDebt(Debt debt);

    Observable<List<Debt>> getAllDebts();

    Observable<Debt> updateDebt(Debt debt);

    Observable<Boolean> updateDebtDifferentAccounts(Debt debt,
                                                    double oldAccountAmount,
                                                    int oldAccountId);

    Observable<Boolean> deleteDebt(int idAccount,
                                   int idDebt,
                                   double amount,
                                   int type);

    Observable<Boolean> payFullDebt(int idAccount,
                                    double accountAmount,
                                    int idDebt);

    Observable<Boolean> payPartOfDebt(int idAccount,
                                      double accountAmount,
                                      int idDebt,
                                      double debtAmount);

    Observable<Boolean> takeMoreDebt(int idAccount,
                                     double accountAmount,
                                     int idDebt,
                                     double debtAmount,
                                     double debtAllAmount);

    Observable<Boolean> setAllDebts(List<Debt> debtList);


    // Statistic

    Observable<Map<String, double[]>> getTransactionsStatistic(int position);

    Observable<Map<String, double[]>> getAccountsAmountSumGroupByTypeAndCurrency();


    // Rates

    Observable<Boolean> updateRates(List<Rates> ratesList);

    Observable<double[]> getRates();

    Observable<Boolean> setRates(double[] rates);
}