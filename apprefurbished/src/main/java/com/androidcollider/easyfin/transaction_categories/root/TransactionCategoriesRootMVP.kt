package com.androidcollider.easyfin.transaction_categories.root

import com.androidcollider.easyfin.common.models.TransactionCategory
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
interface TransactionCategoriesRootMVP {
    interface Model {
        val allTransactionCategories:
                Single<Pair<List<TransactionCategory>, List<TransactionCategory>>>

        fun addNewTransactionCategory(
            transactionCategory: TransactionCategory,
            isExpense: Boolean
        ): Single<TransactionCategory>
    }

    interface View {
        fun showMessage(message: String)
        fun shakeDialogNewTransactionCategoryField()
        fun dismissDialogNewTransactionCategory()
        fun handleNewTransactionCategoryAdded()
    }

    interface Presenter {
        fun setView(view: View?)
        fun loadData()
        fun addNewCategory(name: String, isExpense: Boolean)
    }
}