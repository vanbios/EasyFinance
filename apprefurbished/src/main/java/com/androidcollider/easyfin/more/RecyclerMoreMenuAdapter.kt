package com.androidcollider.easyfin.more

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R

class RecyclerMoreMenuAdapter : RecyclerView.Adapter<MenuViewHolder>() {

    private val items: MutableList<MoreMenuItem> = ArrayList()
    private var itemSelectedListener: MoreMenuItemSelectedListener? = null

    fun setData(itemList : List<MoreMenuItem>) {
        items.clear()
        items.addAll(itemList)
        notifyItemRangeInserted(0, itemCount)
    }

    fun setItemSelectedListener(listener: MoreMenuItemSelectedListener) {
        itemSelectedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.more_menu_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position], itemSelectedListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class MenuViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {
    private val tvItem: TextView = mView.findViewById(R.id.tvMoreMenuItem)
    private val ivItem: ImageView = mView.findViewById(R.id.ivMoreMenuItem)

    fun bind(
        item: MoreMenuItem,
        itemSelectedListener: MoreMenuItemSelectedListener?
    ) {
        tvItem.text = item.title
        ivItem.setImageResource(item.icon)
        mView.setOnClickListener { itemSelectedListener?.onItemSelected(item.id) }
    }
}

interface MoreMenuItemSelectedListener {
    fun onItemSelected(id: Int)
}