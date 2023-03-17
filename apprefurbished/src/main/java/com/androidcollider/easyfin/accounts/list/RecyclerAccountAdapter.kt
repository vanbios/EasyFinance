package com.androidcollider.easyfin.accounts.list

import android.content.res.TypedArray
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.OnCreateContextMenuListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager

/**
 * @author Ihor Bilous
 */
internal class RecyclerAccountAdapter(resourcesManager: ResourcesManager) :
    RecyclerView.Adapter<RecyclerAccountAdapter.ViewHolder>() {

    var currentId = 0
        private set
    private val accountList: MutableList<AccountViewModel>
    private val typeIconsArray: TypedArray

    init {
        accountList = ArrayList()
        typeIconsArray = resourcesManager.getIconArray(ResourcesManager.ICON_ACCOUNT_TYPE)
    }

    fun setItems(items: List<AccountViewModel>) {
        accountList.clear()
        accountList.addAll(items)
        notifyItemRangeInserted(0, itemCount)
    }

    fun deleteItem(position: Int) {
        accountList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return accountList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun getAccount(position: Int): AccountViewModel {
        return accountList[position]
    }

    fun getPositionById(id: Int): Int {
        for (i in accountList.indices) {
            if (accountList[i].id == id) return i
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_frg_account, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (id, name, amount, type) = getAccount(position)
        holder.tvAccountName.text = name
        holder.tvAccountAmount.text = amount
        holder.ivAccountType.setImageDrawable(typeIconsArray.getDrawable(type))
        holder.mView.setOnLongClickListener {
            currentId = id
            false
        }
    }

    internal class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView),
        OnCreateContextMenuListener {
        val ivAccountType: ImageView = mView.findViewById(R.id.ivItemFragmentAccountType)
        val tvAccountName: TextView = mView.findViewById(R.id.tvItemFragmentAccountName)
        val tvAccountAmount: TextView = mView.findViewById(R.id.tvItemFragmentAccountAmount)

        init {
            mView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
            menu.add(Menu.NONE, R.id.ctx_menu_edit_account, 1, R.string.edit)
            menu.add(Menu.NONE, R.id.ctx_menu_delete_account, 2, R.string.delete)
        }
    }
}