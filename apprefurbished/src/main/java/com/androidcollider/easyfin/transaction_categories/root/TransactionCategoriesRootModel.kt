package com.androidcollider.easyfin.transaction_categories.root

import com.androidcollider.easyfin.common.models.TransactionCategory
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Flowable

/**
 * @author Ihor Bilous
 */
internal class TransactionCategoriesRootModel(private val repository: Repository) :
    TransactionCategoriesRootMVP.Model {

    override val allTransactionCategories:
            Flowable<Pair<List<TransactionCategory>, List<TransactionCategory>>>
        get() = Flowable.combineLatest(
            repository.allTransactionIncomeCategories,
            repository.allTransactionExpenseCategories
        ) { first: List<TransactionCategory>, second: List<TransactionCategory> ->
            Pair(first, second)
        }

    override fun addNewTransactionCategory(
        transactionCategory: TransactionCategory,
        isExpense: Boolean
    ): Flowable<TransactionCategory> {
        return if (isExpense) repository.addNewTransactionExpenseCategory(transactionCategory)
        else repository.addNewTransactionIncomeCategory(transactionCategory)
    }
}