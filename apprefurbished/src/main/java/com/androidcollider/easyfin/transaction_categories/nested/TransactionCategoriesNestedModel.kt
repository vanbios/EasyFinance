package com.androidcollider.easyfin.transaction_categories.nested

import com.androidcollider.easyfin.common.models.TransactionCategory
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class TransactionCategoriesNestedModel(private val repository: Repository) :
    TransactionCategoriesNestedMVP.Model {

    override fun getTransactionCategories(isExpense: Boolean): Single<List<TransactionCategory>> {
        return if (isExpense)
            repository.allTransactionExpenseCategories!!
        else repository.allTransactionIncomeCategories!!
    }

    override fun updateTransactionCategory(
        transactionCategory: TransactionCategory,
        isExpense: Boolean
    ): Single<TransactionCategory> {
        return if (isExpense)
            repository.updateTransactionExpenseCategory(transactionCategory)
        else repository.updateTransactionIncomeCategory(transactionCategory)
    }

    override fun deleteTransactionCategory(id: Int, isExpense: Boolean): Single<Boolean> {
        return if (isExpense)
            repository.deleteTransactionExpenseCategory(id)
        else repository.deleteTransactionIncomeCategory(id)
    }
}