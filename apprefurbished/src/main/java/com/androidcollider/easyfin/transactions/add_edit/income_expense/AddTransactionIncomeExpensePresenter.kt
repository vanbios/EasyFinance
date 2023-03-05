package com.androidcollider.easyfin.transactions.add_edit.income_expense

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.models.Transaction
import com.androidcollider.easyfin.common.models.TransactionCategory
import com.androidcollider.easyfin.common.utils.serializable
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import com.androidcollider.easyfin.transactions.list.TransactionsFragment
import io.reactivex.rxjava3.core.Single
import java.util.*
import kotlin.math.abs

/**
 * @author Ihor Bilous
 */
internal class AddTransactionIncomeExpensePresenter(
    private val context: Context,
    private val model: AddTransactionIncomeExpenseMVP.Model,
    private val resourcesManager: ResourcesManager
) : AddTransactionIncomeExpenseMVP.Presenter {

    private var view: AddTransactionIncomeExpenseMVP.View? = null
    private var mode = 0
    override var transactionType = 0
        private set
    private var transFromIntent: Transaction? = null
    private val transactionCategoryList: MutableList<TransactionCategory>

    init {
        transactionCategoryList = ArrayList()
    }

    override fun setView(view: AddTransactionIncomeExpenseMVP.View?) {
        this.view = view
    }

    override fun setArguments(args: Bundle?) {
        args?.let { bundle ->
            mode = bundle.getInt(TransactionsFragment.MODE, 0)
            when (mode) {
                TransactionsFragment.MODE_ADD -> {
                    transactionType = bundle.getInt(TransactionsFragment.TYPE, 0)
                }
                TransactionsFragment.MODE_EDIT -> {
                    transFromIntent =
                        bundle.serializable(TransactionsFragment.TRANSACTION) as Transaction?
                    transFromIntent?.let {
                        val amount = it.amount
                        transactionType =
                            if (model.isDoubleNegative(amount)) TransactionsFragment.TYPE_EXPENSE
                            else TransactionsFragment.TYPE_INCOME
                    }
                }
                else -> {}
            }
        }
    }

    override fun loadAccountsAndCategories() {
        model.getAccountsAndTransactionCategories(checkTransactionIsExpense())
            .subscribe(this::setupView)
            { obj: Throwable -> obj.printStackTrace() }
    }

    override fun save() {
        when (mode) {
            TransactionsFragment.MODE_ADD -> addTransaction()
            TransactionsFragment.MODE_EDIT -> editTransaction()
        }
    }

    override fun addNewCategory(name: String) {
        view?.let {
            if (name.isEmpty()) {
                handleNewTransactionCategoryNameIsNotValid(
                    context.getString(R.string.empty_name_field)
                )
                return
            }
            if (!isNewTransactionCategoryNameUnique(name)) {
                handleNewTransactionCategoryNameIsNotValid(
                    context.getString(R.string.category_name_exist)
                )
                return
            }
            val id = idForNewTransactionCategory
            val category = TransactionCategory(id, name, 1)
            model.addNewTransactionCategory(category, checkTransactionIsExpense())
                .flatMap {
                    model.getTransactionCategories(checkTransactionIsExpense())
                }
                .subscribe({ transactionCategoryList1: List<TransactionCategory> ->
                    view?.let { view1 ->
                        val categoriesPair = getTransactionCategoriesData(transactionCategoryList1)
                        view1.setupCategorySpinner(
                            categoriesPair.first,
                            categoriesPair.second,
                            categoriesPair.first.size - 1
                        )
                        view1.handleNewTransactionCategoryAdded()
                        view1.dismissDialogNewTransactionCategory()
                    }
                }) { obj: Throwable -> obj.printStackTrace() }
        }
    }

    private fun checkTransactionIsExpense(): Boolean {
        return transactionType == TransactionsFragment.TYPE_EXPENSE
    }

    private fun handleNewTransactionCategoryNameIsNotValid(message: String) {
        view?.let {
            it.showMessage(message)
            it.shakeDialogNewTransactionCategoryField()
        }
    }

    private fun isNewTransactionCategoryNameUnique(name: String?): Boolean {
        for (category in transactionCategoryList) {
            if (name.equals(category.name, ignoreCase = true)) return false
        }
        return true
    }

    private val idForNewTransactionCategory: Int
        get() =
            if (transactionCategoryList.isEmpty()) 0
            else transactionCategoryList[transactionCategoryList.size - 1].id + 1

    private fun getTransactionCategoriesData(categoryList: List<TransactionCategory>):
            Pair<List<TransactionCategory>, TypedArray> {
        transactionCategoryList.clear()
        transactionCategoryList.addAll(categoryList)
        val categoryIcons = resourcesManager.getIconArray(
            if (transactionType == TransactionsFragment.TYPE_INCOME)
                ResourcesManager.ICON_TRANSACTION_CATEGORY_INCOME
            else ResourcesManager.ICON_TRANSACTION_CATEGORY_EXPENSE
        )
        val actualTransactionCategoryList = transactionCategoryList
            .filter { t: TransactionCategory -> t.visibility == 1 }
        return Pair(actualTransactionCategoryList, categoryIcons)
    }

    private fun addTransaction() {
        view?.let {
            val sum = model.prepareStringToParse(it.amount)
            if (checkSumField(sum)) {
                var amount = sum.toDouble()
                val isExpense = transactionType == TransactionsFragment.TYPE_EXPENSE
                if (isExpense) amount *= -1.0
                val account = it.account
                var accountAmount = account.amount
                if (checkIsEnoughCosts(isExpense, amount, accountAmount)) {
                    accountAmount += amount
                    val transaction = Transaction()
                    transaction.date = model.getMillisFromString(it.date)
                    transaction.amount = amount
                    transaction.category = it.category
                    transaction.idAccount = account.id
                    transaction.accountAmount = accountAmount
                    transaction.accountName = account.name
                    transaction.accountType = account.type
                    transaction.currency = account.currency
                    handleActionWithTransaction(model.addNewTransaction(transaction))
                }
            }
        }
    }

    private fun editTransaction() {
        view?.let {
            val sum = model.prepareStringToParse(it.amount)
            if (checkSumField(sum)) {
                var amount = sum.toDouble()
                val isExpense = transactionType == TransactionsFragment.TYPE_EXPENSE
                if (isExpense) amount *= -1.0
                val account = it.account
                var accountAmount = account.amount
                val accountId = account.id
                val oldAccountId = transFromIntent!!.idAccount
                val isAccountTheSame = accountId == oldAccountId
                val oldAmount = transFromIntent!!.amount
                var oldAccountAmount = 0.0
                if (isAccountTheSame) accountAmount -= oldAmount else {
                    for (account1 in it.accounts) {
                        if (oldAccountId == account1.id) {
                            oldAccountAmount = account1.amount - oldAmount
                            break
                        }
                    }
                }
                if (checkIsEnoughCosts(isExpense, amount, accountAmount)) {
                    accountAmount += amount
                    val transaction = Transaction()
                    transaction.date = model.getMillisFromString(it.date)
                    transaction.amount = amount
                    transaction.category = it.category
                    transaction.idAccount = account.id
                    transaction.accountAmount = accountAmount
                    transaction.id = transFromIntent!!.id
                    transaction.currency = account.currency
                    transaction.accountType = account.type
                    transaction.accountName = account.name
                    if (isAccountTheSame) {
                        handleActionWithTransaction(model.updateTransaction(transaction))
                    } else {
                        handleActionWithTransaction(
                            model.updateTransactionDifferentAccounts(
                                transaction,
                                oldAccountAmount,
                                oldAccountId
                            )
                        )
                    }
                }
            }
        }
    }

    private fun checkSumField(sum: String): Boolean {
        if (!sum.matches(".*\\d.*".toRegex()) || sum.toDouble().compareTo(0) == 0) {
            view?.showMessage(context.getString(R.string.empty_amount_field))
            return false
        }
        return true
    }

    private fun checkIsEnoughCosts(
        isExpense: Boolean,
        amount: Double,
        accountAmount: Double
    ): Boolean {
        if (isExpense && abs(amount) > accountAmount) {
            view?.showMessage(context.getString(R.string.not_enough_costs))
            return false
        }
        return true
    }

    private fun handleActionWithTransaction(observable: Single<*>) {
        observable.subscribe(
            { view?.performLastActionsAfterSaveAndClose() })
        { obj: Throwable -> obj.printStackTrace() }
    }

    private fun setupView(pair: Pair<List<SpinAccountViewModel>, List<TransactionCategory>>) {
        view?.let { view1 ->
            val accountList = pair.first
            if (accountList.isEmpty()) {
                view1.notifyNotEnoughAccounts()
            } else {
                view1.accounts = accountList
                when (mode) {
                    TransactionsFragment.MODE_ADD -> {
                        view1.showAmount("0,00", transactionType)
                        view1.openNumericDialog()
                    }
                    TransactionsFragment.MODE_EDIT -> {
                        transFromIntent?.let { transaction ->
                            val amount = transaction.amount
                            transactionType =
                                if (model.isDoubleNegative(amount))
                                    TransactionsFragment.TYPE_EXPENSE
                                else TransactionsFragment.TYPE_INCOME
                            view1.showAmount(
                                model.getTransactionForEditAmount(transactionType, amount),
                                transactionType
                            )
                        }
                    }
                }
                view1.setAmountTextColor(
                    ContextCompat.getColor(
                        context,
                        if (transactionType == TransactionsFragment.TYPE_INCOME)
                            R.color.custom_green
                        else R.color.custom_red
                    )
                )
                val categoriesPair = getTransactionCategoriesData(pair.second)
                view1.setupSpinners(categoriesPair.first, categoriesPair.second)
                if (mode == TransactionsFragment.MODE_EDIT) {
                    transFromIntent?.let {
                        val accountName = it.accountName
                        for (i in accountList.indices) {
                            if (accountList[i].name == accountName) {
                                view1.showAccount(i)
                                break
                            }
                        }
                        view1.showCategory(it.category)
                    }
                }
                val calendar = Calendar.getInstance()
                if (mode == TransactionsFragment.MODE_EDIT) {
                    transFromIntent?.let {
                        calendar.time = Date(it.date)
                    }
                }
                view1.setupDateTimeField(calendar)
            }
        }
    }
}