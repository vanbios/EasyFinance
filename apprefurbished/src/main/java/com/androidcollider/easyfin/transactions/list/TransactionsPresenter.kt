package com.androidcollider.easyfin.transactions.list

import com.androidcollider.easyfin.common.models.Transaction
import com.androidcollider.easyfin.common.models.TransactionCategory

/**
 * @author Ihor Bilous
 */
class TransactionsPresenter(private val model: TransactionsMVP.Model) : TransactionsMVP.Presenter {

    private var view: TransactionsMVP.View? = null
    override fun setView(view: TransactionsMVP.View?) {
        this.view = view
    }

    override fun loadData() {
        model.transactionAndTransactionCategoriesLists
            .subscribe({ pair: Pair<List<TransactionViewModel>,
                    Pair<List<TransactionCategory>,
                            List<TransactionCategory>>> ->
                view?.setTransactionAndTransactionCategoriesLists(
                    pair.first,
                    pair.second.first,
                    pair.second.second
                )
            },
                { obj: Throwable -> obj.printStackTrace() })
    }

    override fun getTransactionById(id: Int) {
        model.getTransactionById(id)
            .subscribe(
                { transaction: Transaction ->
                    view?.goToEditTransaction(transaction)
                })
            { obj: Throwable -> obj.printStackTrace() }
    }

    override fun deleteTransactionById(id: Int) {
        model.deleteTransactionById(id)
            .subscribe(
                { aBoolean: Boolean ->
                    if (aBoolean) {
                        view?.deleteTransaction()
                    }
                })
            { obj: Throwable -> obj.printStackTrace() }
    }
}