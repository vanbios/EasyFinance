package com.androidcollider.easyfin.transactions.list

import android.content.Context
import androidx.core.content.ContextCompat
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.models.Transaction
import com.androidcollider.easyfin.common.models.TransactionCategory
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class TransactionsModel(
    private val repository: Repository,
    private val dateFormatManager: DateFormatManager,
    private val numberFormatManager: NumberFormatManager,
    resourcesManager: ResourcesManager,
    private val context: Context
) : TransactionsMVP.Model {

    private val curArray: Array<String>
    private val curLangArray: Array<String>

    init {
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
        curLangArray =
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG)
    }

    override val transactionAndTransactionCategoriesLists:
            Single<Pair<List<TransactionViewModel>,
                    Pair<List<TransactionCategory>, List<TransactionCategory>>>>
        get() = Single.zip(
            repository.allTransactions!!.map { transactionList: List<Transaction> ->
                transformTransactionListToViewModelList(
                    transactionList
                )
            },
            repository.allTransactionIncomeCategories!!,
            repository.allTransactionExpenseCategories!!
        ) { transactionViewModels: List<TransactionViewModel>,
            transactionCategoryIncomeList: List<TransactionCategory>,
            transactionCategoryExpenseList: List<TransactionCategory> ->
            Pair(
                transactionViewModels,
                Pair(
                    transactionCategoryIncomeList,
                    transactionCategoryExpenseList
                )
            )
        }

    override fun getTransactionById(id: Int): Single<Transaction> {
        return repository.allTransactions!!.flatMap { source: List<Transaction> ->
            Single.just(source.first { transaction: Transaction -> transaction.id == id })
        }
    }

    override fun deleteTransactionById(id: Int): Single<Boolean> {
        return getTransactionById(id)
            .flatMap { transaction: Transaction ->
                repository.deleteTransaction(
                    transaction.idAccount,
                    transaction.id,
                    transaction.amount
                )
            }
    }

    private fun transformTransactionToViewModel(transaction: Transaction): TransactionViewModel {
        val model = TransactionViewModel()
        model.id = transaction.id
        model.accountName = transaction.accountName
        model.date = dateFormatManager.longToDateString(
            transaction.date,
            DateFormatManager.DAY_MONTH_YEAR_DOTS
        )
        val amount = numberFormatManager.doubleToStringFormatter(
            transaction.amount,
            NumberFormatManager.FORMAT_1,
            NumberFormatManager.PRECISE_1
        )
        var curLang: String? = null
        for (i in curArray.indices) {
            if (transaction.currency == curArray[i]) {
                curLang = curLangArray[i]
                break
            }
        }
        val isExpense = amount.contains("-")
        model.isExpense = isExpense
        if (isExpense) {
            model.amount = String.format("- %1\$s %2\$s", amount.substring(1), curLang)
            model.colorRes = ContextCompat.getColor(context, R.color.custom_red)
        } else {
            model.amount = String.format("+ %1\$s %2\$s", amount, curLang)
            model.colorRes = ContextCompat.getColor(context, R.color.custom_green)
        }
        model.category = transaction.category
        model.accountType = transaction.accountType
        return model
    }

    private fun transformTransactionListToViewModelList(transactionList: List<Transaction>)
            : List<TransactionViewModel> {
        return transactionList
            .map { transaction: Transaction -> transformTransactionToViewModel(transaction) }
    }
}