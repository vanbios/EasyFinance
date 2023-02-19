package com.androidcollider.easyfin.transaction_categories.nested

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.models.TransactionCategory

/**
 * @author Ihor Bilous
 */
internal class TransactionCategoriesNestedPresenter(
    private val context: Context,
    private val model: TransactionCategoriesNestedMVP.Model,
    private val resourcesManager: ResourcesManager
) : TransactionCategoriesNestedMVP.Presenter {

    private var view: TransactionCategoriesNestedMVP.View? = null
    private val transactionCategoryList: MutableList<TransactionCategory>
    private lateinit var iconsArray: TypedArray
    private var isExpense = false

    init {
        transactionCategoryList = ArrayList()
    }

    override fun setView(view: TransactionCategoriesNestedMVP.View?) {
        this.view = view
    }

    override fun setArguments(args: Bundle?) {
        val type = args?.getInt(TransactionCategoriesNestedFragment.TYPE)
        isExpense = type == TransactionCategoriesNestedFragment.TYPE_EXPENSE
    }

    override fun loadData() {
        iconsArray = resourcesManager.getIconArray(
            if (isExpense)
                ResourcesManager.ICON_TRANSACTION_CATEGORY_EXPENSE
            else ResourcesManager.ICON_TRANSACTION_CATEGORY_INCOME
        )
        loadTransactionCategories()
    }

    override fun loadTransactionCategories() {
        model.getTransactionCategories(isExpense)
            .subscribe({ transactionCategories: List<TransactionCategory> ->
                val actualTransactionCategoryList =
                    getActualTransactionCategoryList(transactionCategories)
                view?.setTransactionCategoryList(actualTransactionCategoryList, iconsArray)
            })
            { obj: Throwable -> obj.printStackTrace() }
    }

    override fun updateTransactionCategory(id: Int, name: String) {
        view?.let {
            if (name.isEmpty()) {
                handleUpdatedTransactionCategoryNameIsNotValid(
                    context.getString(R.string.empty_name_field)
                )
                return
            }
            if (!isNewTransactionCategoryNameUnique(name, id)) {
                handleUpdatedTransactionCategoryNameIsNotValid(
                    context.getString(R.string.category_name_exist)
                )
                return
            }
            val category = TransactionCategory(id, name, 1)
            model.updateTransactionCategory(category, isExpense)
                .subscribe({
                    view?.let {
                        it.handleTransactionCategoryUpdated()
                        it.dismissDialogUpdateTransactionCategory()
                    }
                }) { obj: Throwable -> obj.printStackTrace() }
        }
    }

    override fun deleteTransactionCategoryById(id: Int) {
        model.deleteTransactionCategory(id, isExpense)
            .subscribe({
                deleteTransactionCategoryFromListById(id)
                view?.deleteTransactionCategory()
            })
            { obj: Throwable -> obj.printStackTrace() }
    }

    private fun handleUpdatedTransactionCategoryNameIsNotValid(message: String) {
        view?.let {
            it.showMessage(message)
            it.shakeDialogUpdateTransactionCategoryField()
        }
    }

    private fun isNewTransactionCategoryNameUnique(name: String, id: Int): Boolean {
        if (name.equals(getCategoryNameById(id), ignoreCase = true)) return true
        for (category in transactionCategoryList) {
            if (name.equals(category.name, ignoreCase = true)) return false
        }
        return true
    }

    override fun getCategoryNameById(id: Int): String {
        for (transactionCategory in transactionCategoryList) {
            if (transactionCategory.id == id) return transactionCategory.name
        }
        return ""
    }

    private fun deleteTransactionCategoryFromListById(id: Int) {
        var pos = -1
        for (i in transactionCategoryList.indices) {
            if (id == transactionCategoryList[i].id) {
                pos = i
                break
            }
        }
        if (pos >= 0) transactionCategoryList.removeAt(pos)
    }

    private fun getActualTransactionCategoryList(categoryList: List<TransactionCategory>)
            : List<TransactionCategory> {
        transactionCategoryList.clear()
        transactionCategoryList.addAll(categoryList)

        return transactionCategoryList
            .filter { t: TransactionCategory -> t.visibility == 1 }
    }
}