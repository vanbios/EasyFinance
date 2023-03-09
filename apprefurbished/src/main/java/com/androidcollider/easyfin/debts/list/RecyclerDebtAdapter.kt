package com.androidcollider.easyfin.debts.list

import android.graphics.LightingColorFilter
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.OnCreateContextMenuListener
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R

/**
 * @author Ihor Bilous
 */
internal class RecyclerDebtAdapter : RecyclerView.Adapter<RecyclerDebtAdapter.ViewHolder>() {

    var currentId = 0
        private set

    private val debtList: MutableList<DebtViewModel>

    init {
        debtList = ArrayList()
    }

    fun setItems(items: List<DebtViewModel>) {
        debtList.clear()
        debtList.addAll(items)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        debtList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return debtList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun getDebt(position: Int): DebtViewModel {
        return debtList[position]
    }

    fun getPositionById(id: Int): Int {
        for (i in debtList.indices) {
            if (debtList[i].id == id) return i
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_frg_debt, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val debt = getDebt(position)
        holder.tvDebtName.text = debt.name
        holder.tvAmount.text = debt.amount
        holder.tvAccountName.text = debt.accountName
        holder.tvDate.text = debt.date
        holder.prgBar.progress = debt.progress
        holder.tvProgress.text = debt.progressPercents
        val color = debt.colorRes
        holder.tvAmount.setTextColor(color)
        holder.prgBar.progressDrawable.colorFilter = LightingColorFilter(-0x1000000, color)
        holder.tvProgress.setTextColor(color)
        holder.mView.setOnLongClickListener {
            currentId = debt.id
            false
        }
    }

    internal class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView),
        OnCreateContextMenuListener {
        val tvDebtName: TextView = mView.findViewById(R.id.tvItemDebtName)
        val tvAmount: TextView = mView.findViewById(R.id.tvItemDebtAmount)
        val tvAccountName: TextView = mView.findViewById(R.id.tvItemDebtAccountName)
        val tvDate: TextView = mView.findViewById(R.id.tvItemDebtDate)
        val prgBar: ProgressBar = mView.findViewById(R.id.progressBarItemDebt)
        val tvProgress: TextView = mView.findViewById(R.id.tvItemDebtProgress)

        init {
            mView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
            menu.add(Menu.NONE, R.id.ctx_menu_pay_all_debt, 1, R.string.pay_all_debt)
            menu.add(Menu.NONE, R.id.ctx_menu_pay_part_debt, 2, R.string.pay_part_debt)
            menu.add(Menu.NONE, R.id.ctx_menu_take_more_debt, 3, R.string.take_more_debt)
            menu.add(Menu.NONE, R.id.ctx_menu_edit_debt, 4, R.string.edit)
            menu.add(Menu.NONE, R.id.ctx_menu_delete_debt, 5, R.string.delete)
        }
    }
}