package com.androidcollider.easyfin.main.bottom_sheet_menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R

class MainBottomSheetMenuRecyclerAdapter(
    private val items: Array<MainBottomSheetMenuItem>,
    private val itemSelectedListener: MainBottomSheetMenuItemSelectedListener
) :
    RecyclerView.Adapter<MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.main_bottom_sheet_menu_item, parent, false)
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
    private val tvItem: TextView = mView.findViewById(R.id.tvMainBottomSheetMenuItem)
    private val ivItem: ImageView = mView.findViewById(R.id.ivMainBottomSheetMenuItem)

    fun bind(
        item: MainBottomSheetMenuItem,
        itemSelectedListener: MainBottomSheetMenuItemSelectedListener
    ) {
        tvItem.text = item.title
        ivItem.setImageResource(item.icon)
        mView.setOnClickListener { itemSelectedListener.onItemSelected(item.id) }
    }
}

interface MainBottomSheetMenuItemSelectedListener {
    fun onItemSelected(id: Int)
}