package com.androidcollider.easyfin.common.models

/**
 * @author Ihor Bilous
 */
class Data(
    val accountList: List<Account>?,
    val transactionList: List<Transaction>?,
    val debtList: List<Debt>?,
    val ratesArray: DoubleArray,
    val transactionCategoryIncomeList: List<TransactionCategory>?,
    val transactionCategoryExpenseList: List<TransactionCategory>?
)