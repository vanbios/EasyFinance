package com.androidcollider.easyfin.transactions.add_edit.income_expense

import android.content.res.TypedArray
import android.os.Bundle
import androidx.core.util.Pair
import com.androidcollider.easyfin.common.models.Transaction
import com.androidcollider.easyfin.common.models.TransactionCategory
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import io.reactivex.rxjava3.core.Flowable
import java.util.*

/**
 * @author Ihor Bilous
 */
interface AddTransactionIncomeExpenseMVP {
    interface Model {
        fun getAccountsAndTransactionCategories(isExpense: Boolean):
                Flowable<Pair<List<SpinAccountViewModel>, List<TransactionCategory>>>?

        fun getTransactionCategories(isExpense: Boolean): Flowable<List<TransactionCategory>>?
        fun addNewTransaction(transaction: Transaction): Flowable<Transaction>?
        fun updateTransaction(transaction: Transaction): Flowable<Transaction>?
        fun updateTransactionDifferentAccounts(
            transaction: Transaction?,
            oldAccountAmount: Double,
            oldAccountId: Int
        ): Flowable<Boolean>?

        fun addNewTransactionCategory(
            transactionCategory: TransactionCategory,
            isExpense: Boolean
        ): Flowable<TransactionCategory>?

        fun prepareStringToParse(value: String): String?
        fun getMillisFromString(date: String): Long
        fun isDoubleNegative(d: Double): Boolean
        fun getTransactionForEditAmount(type: Int, amount: Double): String?
    }

    interface View {
        fun showAmount(amount: String, type: Int)
        fun setupSpinners(categoryList: List<TransactionCategory>, categoryIcons: TypedArray)
        fun setupCategorySpinner(
            categoryList: List<TransactionCategory>,
            categoryIcons: TypedArray,
            selectedPos: Int
        )

        fun showCategory(position: Int)
        fun showAccount(position: Int)
        fun showMessage(message: String)
        fun setupDateTimeField(calendar: Calendar)
        fun openNumericDialog()
        fun notifyNotEnoughAccounts()
        fun setAmountTextColor(color: Int)
        fun performLastActionsAfterSaveAndClose()
        val amount: String
        val account: SpinAccountViewModel
        val date: String
        val category: Int
        var accounts: List<SpinAccountViewModel>
        fun shakeDialogNewTransactionCategoryField()
        fun dismissDialogNewTransactionCategory()
        fun handleNewTransactionCategoryAdded()
    }

    interface Presenter {
        fun setView(view: View?)
        fun setArguments(args: Bundle?)
        fun loadAccountsAndCategories()
        fun save()
        val transactionType: Int
        fun addNewCategory(name: String?)
    }
}