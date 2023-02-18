package com.androidcollider.easyfin.transaction_categories.nested

import android.content.res.TypedArray
import android.os.Bundle
import com.androidcollider.easyfin.common.models.TransactionCategory
import io.reactivex.rxjava3.core.Flowable

/**
 * @author Ihor Bilous
 */
interface TransactionCategoriesNestedMVP {
    interface Model {
        fun getTransactionCategories(isExpense: Boolean): Flowable<List<TransactionCategory>>?
        fun updateTransactionCategory(
            transactionCategory: TransactionCategory,
            isExpense: Boolean
        ): Flowable<TransactionCategory>?

        fun deleteTransactionCategory(id: Int, isExpense: Boolean): Flowable<Boolean>?
    }

    interface View {
        fun setTransactionCategoryList(
            transactionCategoryList: List<TransactionCategory>,
            iconsArray: TypedArray
        )

        fun showMessage(message: String)
        fun shakeDialogUpdateTransactionCategoryField()
        fun dismissDialogUpdateTransactionCategory()
        fun handleTransactionCategoryUpdated()
        fun deleteTransactionCategory()
    }

    interface Presenter {
        fun setView(view: View?)
        fun loadData()
        fun loadTransactionCategories()
        fun setArguments(args: Bundle?)
        fun updateTransactionCategory(id: Int, name: String?)
        fun deleteTransactionCategoryById(id: Int)
        fun getCategoryNameById(id: Int): String?
    }
}