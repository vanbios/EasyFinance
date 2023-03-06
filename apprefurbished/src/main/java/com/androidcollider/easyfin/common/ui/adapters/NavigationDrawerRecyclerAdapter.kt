package com.androidcollider.easyfin.common.ui.adapters

import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager

/**
 * @author Ihor Bilous
 */
class NavigationDrawerRecyclerAdapter(resourcesManager: ResourcesManager) :
    RecyclerView.Adapter<NavigationDrawerRecyclerAdapter.ViewHolder>() {

    private val navTitles: Array<String>
    private val navIcons: TypedArray

    init {
        navTitles = resourcesManager.getStringArray(ResourcesManager.STRING_NAVIGATION_DRAWER_MENU)
        navIcons = resourcesManager.getIconArray(ResourcesManager.ICON_NAVIGATION_DRAWER_MENU)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        when (viewType) {
            TYPE_ITEM -> return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_nav_row, parent, false),
                TYPE_ITEM
            )
            TYPE_HEADER -> return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.nav_header, parent, false),
                TYPE_HEADER
            )
            TYPE_DIVIDER -> return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_nav_divider, parent, false), TYPE_DIVIDER
            )
        }
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_nav_row, parent, false),
            TYPE_ITEM
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.holderId == TYPE_ITEM) {
            var arrayPos = 0
            if (position < 5) arrayPos = position - 1 else if (position > 5) arrayPos = position - 2
            holder.tvNavItem?.text = navTitles[arrayPos]
            holder.ivNavItem?.setImageDrawable(navIcons.getDrawable(arrayPos))
        }
    }

    override fun getItemCount(): Int {
        return navTitles.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        if (isPositionHeader(position)) return TYPE_HEADER
        return if (isPositionDivider(position)) TYPE_DIVIDER else TYPE_ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    private fun isPositionDivider(position: Int): Boolean {
        return position == 5
    }

    class ViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        var holderId = 0
        var tvNavItem: TextView? = null
        var ivNavItem: ImageView? = null

        init {
            if (viewType == TYPE_ITEM) {
                tvNavItem = itemView.findViewById(R.id.tvItemNavRow)
                ivNavItem = itemView.findViewById(R.id.ivItemNavRow)
                holderId = TYPE_ITEM
            } else {
                holderId = if (viewType == TYPE_HEADER) TYPE_HEADER else TYPE_DIVIDER
            }
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_DIVIDER = 2
    }
}