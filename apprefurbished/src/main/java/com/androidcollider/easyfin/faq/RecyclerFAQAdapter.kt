package com.androidcollider.easyfin.faq

import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.utils.setSafeOnClickListener

/**
 * @author Ihor Bilous
 */
internal class RecyclerFAQAdapter : RecyclerView.Adapter<RecyclerFAQAdapter.ViewHolder>() {

    private val items: MutableList<Pair<String, String>> = ArrayList()

    fun setData(itemList: List<Pair<String, String>>) {
        items.clear()
        items.addAll(itemList)
        notifyItemRangeInserted(0, itemCount)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_faq, parent, false)
        )
    }

    private fun getItem(position: Int): Pair<String, String> {
        return items[position]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pair = getItem(position)
        holder.tvHead.text = pair.first
        holder.tvBody.text = pair.second
        holder.card.setSafeOnClickListener {
            holder.tvBody.visibility =
                if (holder.tvBody.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView
        val tvHead: TextView
        val tvBody: TextView

        init {
            card = view.findViewById(R.id.cardFAQ)
            tvHead = view.findViewById(R.id.tvFAQHead)
            tvBody = view.findViewById(R.id.tvFAQBody)
        }
    }
}