package com.androidcollider.easyfin.transactions.list

import android.content.res.TypedArray
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.OnCreateContextMenuListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager
import com.androidcollider.easyfin.common.models.TransactionCategory
import com.androidcollider.easyfin.transactions.list.RecyclerTransactionAdapter.MainViewHolder

/**
 * @author Ihor Bilous
 */
internal class RecyclerTransactionAdapter(
    resourcesManager: ResourcesManager,
    letterTileManager: LetterTileManager
) : RecyclerView.Adapter<MainViewHolder>() {

    var currentId = 0
        private set

    private val transactionList: MutableList<TransactionViewModel>
    private val transactionCategoryIncomeList: MutableList<TransactionCategory>
    private val transactionCategoryExpenseList: MutableList<TransactionCategory>
    private val catExpenseIconsArray: TypedArray
    private val catIncomeIconsArray: TypedArray
    private val typeIconsArray: TypedArray
    private val letterTileManager: LetterTileManager

    init {
        transactionList = ArrayList()
        transactionCategoryIncomeList = ArrayList()
        transactionCategoryExpenseList = ArrayList()
        catExpenseIconsArray =
            resourcesManager.getIconArray(ResourcesManager.ICON_TRANSACTION_CATEGORY_EXPENSE)
        catIncomeIconsArray =
            resourcesManager.getIconArray(ResourcesManager.ICON_TRANSACTION_CATEGORY_INCOME)
        typeIconsArray = resourcesManager.getIconArray(ResourcesManager.ICON_ACCOUNT_TYPE)
        this.letterTileManager = letterTileManager
    }

    fun setItems(items: List<TransactionViewModel>) {
        transactionList.clear()
        transactionList.addAll(items)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        transactionList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setTransactionCategories(
        transactionCategoryIncomeList: List<TransactionCategory>,
        transactionCategoryExpenseList: List<TransactionCategory>
    ) {
        this.transactionCategoryIncomeList.clear()
        this.transactionCategoryIncomeList.addAll(transactionCategoryIncomeList)
        this.transactionCategoryExpenseList.clear()
        this.transactionCategoryExpenseList.addAll(transactionCategoryExpenseList)
    }

    override fun getItemViewType(position: Int): Int {
        return if (showButton && position == Companion.itemCount) BUTTON_TYPE else CONTENT_TYPE
    }

    override fun getItemCount(): Int {
        val arraySize = transactionList.size
        showButton = arraySize > maxCount
        Companion.itemCount = if (showButton) maxCount else arraySize
        return if (showButton) Companion.itemCount + 1 else Companion.itemCount
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun getTransaction(position: Int): TransactionViewModel {
        return transactionList[position]
    }

    fun getPositionById(id: Int): Int {
        for (i in transactionList.indices) {
            if (transactionList[i].id == id) return i
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return if (viewType == BUTTON_TYPE) ViewHolderButton(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_button_show_more, parent, false)
        )
        else ViewHolderItem(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_frg_transaction, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        if (getItemViewType(position) == CONTENT_TYPE) {
            val holderItem = holder as ViewHolderItem
            val transaction = getTransaction(position)
            holderItem.tvTransAccountName.text = transaction.accountName
            holderItem.tvTransDate.text = transaction.date
            holderItem.tvTransAmount.text = transaction.amount
            holderItem.tvTransAmount.setTextColor(transaction.colorRes)
            val categoryId = transaction.category
            if (transaction.isExpense) {
                if (categoryId < catExpenseIconsArray.length()) {
                    holderItem.ivTransCategory.setImageDrawable(
                        catExpenseIconsArray.getDrawable(
                            categoryId
                        )
                    )
                } else {
                    holderItem.ivTransCategory.setImageBitmap(
                        letterTileManager.getLetterTile(
                            getCategoryNameById(categoryId, true)
                        )
                    )
                }
            } else {
                if (categoryId < catIncomeIconsArray.length()) {
                    holderItem.ivTransCategory.setImageDrawable(
                        catIncomeIconsArray.getDrawable(
                            categoryId
                        )
                    )
                } else {
                    holderItem.ivTransCategory.setImageBitmap(
                        letterTileManager.getLetterTile(
                            getCategoryNameById(categoryId, false)
                        )
                    )
                }
            }
            holderItem.ivTransAccountType.setImageDrawable(typeIconsArray.getDrawable(transaction.accountType))
            holderItem.mView.setOnLongClickListener {
                currentId = transaction.id
                false
            }
        } else if (getItemViewType(position) == BUTTON_TYPE) {
            val holderButton = holder as ViewHolderButton
            holderButton.tvShowMore.setOnClickListener {
                maxCount += 30
                notifyDataSetChanged()
            }
        }
    }

    private fun getCategoryNameById(id: Int, isExpense: Boolean): String {
        for (category in if (isExpense)
            transactionCategoryExpenseList else transactionCategoryIncomeList) {
            if (id == category.id) return category.name
        }
        return ""
    }

    internal open class MainViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private class ViewHolderItem constructor(val mView: View) : MainViewHolder(mView),
        OnCreateContextMenuListener {
        val tvTransAmount: TextView = mView.findViewById(R.id.tvItemTransactionAmount)
        val tvTransAccountName: TextView = mView.findViewById(R.id.tvItemTransactionAccountName)
        val tvTransDate: TextView = mView.findViewById(R.id.tvItemTransactionDate)
        val ivTransCategory: ImageView = mView.findViewById(R.id.ivItemTransactionCategory)
        val ivTransAccountType: ImageView = mView.findViewById(R.id.ivItemTransactionAccountType)

        init {
            mView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
            menu.add(Menu.NONE, R.id.ctx_menu_edit_transaction, 1, R.string.edit)
            menu.add(Menu.NONE, R.id.ctx_menu_delete_transaction, 2, R.string.delete)
        }
    }

    private class ViewHolderButton constructor(view: View) : MainViewHolder(view) {
        val tvShowMore: TextView = view.findViewById(R.id.tvItemShowMore)
    }

    companion object {
        private var itemCount = 0
        private var maxCount = 30
        private var showButton = false
        private const val CONTENT_TYPE = 1
        private const val BUTTON_TYPE = 2
    }
}