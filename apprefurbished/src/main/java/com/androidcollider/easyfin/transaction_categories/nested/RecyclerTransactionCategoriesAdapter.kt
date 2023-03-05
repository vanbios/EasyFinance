package com.androidcollider.easyfin.transaction_categories.nested

import android.content.res.TypedArray
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.OnCreateContextMenuListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager
import com.androidcollider.easyfin.common.models.TransactionCategory

/**
 * @author Ihor Bilous
 */
internal class RecyclerTransactionCategoriesAdapter(letterTileManager: LetterTileManager) :
    RecyclerView.Adapter<RecyclerTransactionCategoriesAdapter.ViewHolderItem>() {

    var currentId = 0
        private set

    private val transactionList: MutableList<TransactionCategory>
    private lateinit var catIconsArray: TypedArray
    private val letterTileManager: LetterTileManager

    init {
        transactionList = ArrayList()
        this.letterTileManager = letterTileManager
    }

    fun setItems(items: List<TransactionCategory>, typedArray: TypedArray) {
        transactionList.clear()
        transactionList.addAll(items)
        catIconsArray = typedArray
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        transactionList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun getTransactionCategory(position: Int): TransactionCategory {
        return transactionList[position]
    }

    fun getPositionById(id: Int): Int {
        for (i in transactionList.indices) {
            if (transactionList[i].id == id) return i
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        return ViewHolderItem(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaction_categories, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        val transactionCategory = getTransactionCategory(position)
        val categoryId = transactionCategory.id
        val categoryName = transactionCategory.name
        holder.tvCategory.text = categoryName
        if (categoryId < catIconsArray.length()) {
            holder.ivCategory.setImageDrawable(catIconsArray.getDrawable(categoryId))
            holder.mView.setOnCreateContextMenuListener(null)
        } else {
            holder.ivCategory.setImageBitmap(letterTileManager.getLetterTile(categoryName))
        }
        holder.mView.setOnLongClickListener {
            currentId = transactionCategory.id
            false
        }
    }

    internal class ViewHolderItem(val mView: View) : RecyclerView.ViewHolder(mView),
        OnCreateContextMenuListener {
        val tvCategory: TextView = mView.findViewById(R.id.tv_category_name)
        val ivCategory: ImageView = mView.findViewById(R.id.iv_category_icon)

        init {
            mView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
            menu.add(Menu.NONE, R.id.ctx_menu_edit_transaction_category, 1, R.string.edit)
            menu.add(Menu.NONE, R.id.ctx_menu_delete_transaction_category, 2, R.string.delete)
        }
    }
}