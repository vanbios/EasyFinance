package com.androidcollider.easyfin.transactions.add_edit.income_expense

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.models.Transaction
import com.androidcollider.easyfin.common.models.TransactionCategory
import com.androidcollider.easyfin.common.repository.Repository
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import io.reactivex.rxjava3.core.Single
import kotlin.math.abs

/**
 * @author Ihor Bilous
 */
internal class AddTransactionIncomeExpenseModel(
    private val repository: Repository,
    private val numberFormatManager: NumberFormatManager,
    private val dateFormatManager: DateFormatManager,
    private val accountsToSpinViewModelManager: AccountsToSpinViewModelManager
) : AddTransactionIncomeExpenseMVP.Model {

    override fun getAccountsAndTransactionCategories(isExpense: Boolean):
            Single<Pair<List<SpinAccountViewModel>, List<TransactionCategory>>> {
        return Single.zip(
            accountsToSpinViewModelManager.getSpinAccountViewModelList(repository.allAccounts!!),
            getTransactionCategories(isExpense)
        ) { first: List<SpinAccountViewModel>,
            second: List<TransactionCategory> ->
            Pair(first, second)
        }
    }

    override fun getTransactionCategories(isExpense: Boolean): Single<List<TransactionCategory>> {
        return if (isExpense) repository.allTransactionExpenseCategories!!
        else repository.allTransactionIncomeCategories!!
    }

    override fun addNewTransaction(transaction: Transaction): Single<Transaction> {
        return repository.addNewTransaction(transaction)
    }

    override fun updateTransaction(transaction: Transaction): Single<Transaction> {
        return repository.updateTransaction(transaction)
    }

    override fun updateTransactionDifferentAccounts(
        transaction: Transaction,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Single<Boolean> {
        return repository.updateTransactionDifferentAccounts(
            transaction,
            oldAccountAmount,
            oldAccountId
        )
    }

    override fun addNewTransactionCategory(
        transactionCategory: TransactionCategory,
        isExpense: Boolean
    ): Single<TransactionCategory> {
        return if (isExpense) repository.addNewTransactionExpenseCategory(transactionCategory)
        else repository.addNewTransactionIncomeCategory(transactionCategory)
    }

    override fun prepareStringToParse(value: String): String {
        return numberFormatManager.prepareStringToParse(value)
    }

    override fun getMillisFromString(date: String): Long {
        return dateFormatManager.stringToDate(date, DateFormatManager.DAY_MONTH_YEAR_SPACED).time
    }

    override fun isDoubleNegative(d: Double): Boolean {
        return numberFormatManager.isDoubleNegative(d)
    }

    override fun getTransactionForEditAmount(type: Int, amount: Double): String {
        return numberFormatManager.doubleToStringFormatterForEdit(
            if (type == 1) amount else abs(amount),
            NumberFormatManager.FORMAT_1,
            NumberFormatManager.PRECISE_1
        )
    }
}