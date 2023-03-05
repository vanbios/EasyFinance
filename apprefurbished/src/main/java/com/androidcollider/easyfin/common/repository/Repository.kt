package com.androidcollider.easyfin.common.repository

import com.androidcollider.easyfin.common.models.*
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
interface Repository {
    // Accounts
    fun addNewAccount(account: Account): Single<Account>
    val allAccounts: Single<List<Account>>?
    fun updateAccount(account: Account): Single<Account>
    fun deleteAccount(id: Int): Single<Boolean>
    fun transferBTWAccounts(
        idAccount1: Int,
        accountAmount1: Double,
        idAccount2: Int,
        accountAmount2: Double
    ): Single<Boolean>

    fun setAllAccounts(accountList: List<Account>): Single<Boolean>

    // Transactions
    fun addNewTransaction(transaction: Transaction): Single<Transaction>
    val allTransactions: Single<List<Transaction>>?
    fun updateTransaction(transaction: Transaction): Single<Transaction>
    fun updateTransactionDifferentAccounts(
        transaction: Transaction,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Single<Boolean>

    fun deleteTransaction(
        idAccount: Int,
        idTransaction: Int,
        amount: Double
    ): Single<Boolean>

    fun setAllTransactions(transactionList: List<Transaction>): Single<Boolean>

    // Debts
    fun addNewDebt(debt: Debt): Single<Debt>
    val allDebts: Single<List<Debt>>?
    fun updateDebt(debt: Debt): Single<Debt>
    fun updateDebtDifferentAccounts(
        debt: Debt,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Single<Boolean>

    fun deleteDebt(
        idAccount: Int,
        idDebt: Int,
        amount: Double,
        type: Int
    ): Single<Boolean>

    fun payFullDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int
    ): Single<Boolean>

    fun payPartOfDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double
    ): Single<Boolean>

    fun takeMoreDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double,
        debtAllAmount: Double
    ): Single<Boolean>

    fun setAllDebts(debtList: List<Debt>): Single<Boolean>

    // Statistic
    fun getTransactionsStatistic(position: Int): Single<Map<String, DoubleArray>>?
    val accountsAmountSumGroupByTypeAndCurrency: Single<Map<String, DoubleArray>>?

    // Rates
    fun updateRates(ratesList: List<Rates>): Single<Boolean>
    val rates: Single<DoubleArray>?
    fun setRates(rates: DoubleArray): Single<Boolean>

    // Transaction Category
    // Income
    fun addNewTransactionIncomeCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory>

    val allTransactionIncomeCategories: Single<List<TransactionCategory>>?
    fun updateTransactionIncomeCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory>

    fun deleteTransactionIncomeCategory(id: Int): Single<Boolean>
    fun setAllTransactionIncomeCategories(transactionCategoryList: List<TransactionCategory>):
            Single<Boolean>

    // Expense
    fun addNewTransactionExpenseCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory>

    val allTransactionExpenseCategories: Single<List<TransactionCategory>>?
    fun updateTransactionExpenseCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory>

    fun deleteTransactionExpenseCategory(id: Int): Single<Boolean>
    fun setAllTransactionExpenseCategories(transactionCategoryList: List<TransactionCategory>):
            Single<Boolean>
}