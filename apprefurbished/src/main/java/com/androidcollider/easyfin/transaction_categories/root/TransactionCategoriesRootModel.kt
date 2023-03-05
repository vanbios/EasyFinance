package com.androidcollider.easyfin.transaction_categories.root

import com.androidcollider.easyfin.common.models.TransactionCategory
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class TransactionCategoriesRootModel(private val repository: Repository) :
    TransactionCategoriesRootMVP.Model {

    override val allTransactionCategories:
            Single<Pair<List<TransactionCategory>, List<TransactionCategory>>>
        get() = Single.zip(
            repository.allTransactionIncomeCategories!!,
            repository.allTransactionExpenseCategories!!
        ) { first: List<TransactionCategory>, second: List<TransactionCategory> ->
            Pair(first, second)
        }

    override fun addNewTransactionCategory(
        transactionCategory: TransactionCategory,
        isExpense: Boolean
    ): Single<TransactionCategory> {
        return if (isExpense) repository.addNewTransactionExpenseCategory(transactionCategory)
        else repository.addNewTransactionIncomeCategory(transactionCategory)
    }
}