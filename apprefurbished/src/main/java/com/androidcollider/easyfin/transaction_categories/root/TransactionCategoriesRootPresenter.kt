package com.androidcollider.easyfin.transaction_categories.root

import android.content.Context
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.models.TransactionCategory

/**
 * @author Ihor Bilous
 */
internal class TransactionCategoriesRootPresenter(
    private val context: Context,
    private val model: TransactionCategoriesRootMVP.Model
) : TransactionCategoriesRootMVP.Presenter {

    private var view: TransactionCategoriesRootMVP.View? = null
    private val transactionCategoryIncomeList: MutableList<TransactionCategory>
    private val transactionCategoryIncomeNotFilteredList: MutableList<TransactionCategory>
    private val transactionCategoryExpenseList: MutableList<TransactionCategory>
    private val transactionCategoryExpenseNotFilteredList: MutableList<TransactionCategory>

    init {
        transactionCategoryIncomeList = ArrayList()
        transactionCategoryExpenseList = ArrayList()
        transactionCategoryIncomeNotFilteredList = ArrayList()
        transactionCategoryExpenseNotFilteredList = ArrayList()
    }

    override fun setView(view: TransactionCategoriesRootMVP.View?) {
        this.view = view
    }

    override fun loadData() {
        model.allTransactionCategories
            .subscribe({ (first, second):
                         Pair<List<TransactionCategory>, List<TransactionCategory>> ->
                saveActualTransactionCategoryList(first, false)
                saveActualTransactionCategoryList(second, true)
            }) { obj: Throwable -> obj.printStackTrace() }
    }

    override fun addNewCategory(name: String, isExpense: Boolean) {
        if (view != null) {
            if (name.isEmpty()) {
                handleNewTransactionCategoryNameIsNotValid(
                    context.getString(R.string.empty_name_field)
                )
                return
            }
            if (!isNewTransactionCategoryNameUnique(name, isExpense)) {
                handleNewTransactionCategoryNameIsNotValid(
                    context.getString(R.string.category_name_exist)
                )
                return
            }
            val id = getIdForNewTransactionCategory(isExpense)
            val category = TransactionCategory(id, name, 1)
            model.addNewTransactionCategory(category, isExpense)
                .subscribe({ transactionCategory: TransactionCategory ->
                    view?.let {
                        addNewTransactionCategoryInLists(transactionCategory, isExpense)
                        it.handleNewTransactionCategoryAdded()
                        it.dismissDialogNewTransactionCategory()
                    }
                }) { obj: Throwable -> obj.printStackTrace() }
        }
    }

    private fun addNewTransactionCategoryInLists(
        transactionCategory: TransactionCategory,
        isExpense: Boolean
    ) {
        if (isExpense) {
            transactionCategoryExpenseList.add(transactionCategory)
            transactionCategoryExpenseNotFilteredList.add(transactionCategory)
        } else {
            transactionCategoryIncomeList.add(transactionCategory)
            transactionCategoryIncomeNotFilteredList.add(transactionCategory)
        }
    }

    private fun handleNewTransactionCategoryNameIsNotValid(message: String) {
        view?.let {
            it.showMessage(message)
            it.shakeDialogNewTransactionCategoryField()
        }
    }

    private fun isNewTransactionCategoryNameUnique(name: String, isExpense: Boolean): Boolean {
        for (category in if (isExpense) transactionCategoryExpenseList
        else transactionCategoryIncomeList) {
            if (name.equals(category.name, ignoreCase = true)) return false
        }
        return true
    }

    private fun getIdForNewTransactionCategory(isExpense: Boolean): Int {
        val list: List<TransactionCategory> =
            if (isExpense) transactionCategoryExpenseNotFilteredList
            else transactionCategoryIncomeNotFilteredList
        return if (list.isEmpty()) 0 else list[list.size - 1].id + 1
    }

    private fun saveActualTransactionCategoryList(
        categoryList: List<TransactionCategory>,
        isExpense: Boolean
    ) {
        val list =
            if (isExpense) transactionCategoryExpenseList
            else transactionCategoryIncomeList
        val notFilteredList =
            if (isExpense) transactionCategoryExpenseNotFilteredList
            else transactionCategoryIncomeNotFilteredList
        notFilteredList.clear()
        notFilteredList.addAll(categoryList)
        list.clear()
        list.addAll(getFilteredList(categoryList))
    }

    private fun getFilteredList(list: List<TransactionCategory>): List<TransactionCategory> {
        return list.filter { t: TransactionCategory -> t.visibility == 1 }
    }
}