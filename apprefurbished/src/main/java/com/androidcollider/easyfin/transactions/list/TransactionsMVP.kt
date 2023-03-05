package com.androidcollider.easyfin.transactions.list

import com.androidcollider.easyfin.common.models.Transaction
import com.androidcollider.easyfin.common.models.TransactionCategory
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
interface TransactionsMVP {
    interface Model {
        val transactionAndTransactionCategoriesLists:
                Single<Pair<List<TransactionViewModel>,
                        Pair<List<TransactionCategory>, List<TransactionCategory>>>>

        fun getTransactionById(id: Int): Single<Transaction>
        fun deleteTransactionById(id: Int): Single<Boolean>
    }

    interface View {
        fun setTransactionAndTransactionCategoriesLists(
            transactionList: List<TransactionViewModel>,
            transactionCategoryIncomeList: List<TransactionCategory>,
            transactionCategoryExpenseList: List<TransactionCategory>
        )

        fun goToEditTransaction(transaction: Transaction)
        fun deleteTransaction()
    }

    interface Presenter {
        fun setView(view: View?)
        fun loadData()
        fun getTransactionById(id: Int)
        fun deleteTransactionById(id: Int)
    }
}