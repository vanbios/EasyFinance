package com.androidcollider.easyfin.common.ui.adapters

import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * @author Ihor Bilous
 */
class SpinIconTextHeadAdapter(
    mContext: Context,
    private val headLayout: Int, private val headTvId: Int, private val headIvId: Int,
    private val dropLayout: Int, private val dropTvId: Int, private val dropIvId: Int,
    private val textArray: Array<String>, private val iconsArray: TypedArray
) : ArrayAdapter<String>(mContext, headLayout, textArray) {

    private val inflater: LayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {
        return getCustomDropView(position, parent)
    }

    override fun getView(pos: Int, view: View?, parent: ViewGroup): View {
        return getCustomHeadView(pos, parent)
    }

    private fun getCustomDropView(position: Int, parent: ViewGroup): View {
        val dropSpinner = inflater.inflate(dropLayout, parent, false)
        val text = dropSpinner.findViewById<TextView>(dropTvId)
        text.text = textArray[position]
        val icon = dropSpinner.findViewById<ImageView>(
            dropIvId
        )
        icon.setImageResource(iconsArray.getResourceId(position, 0))
        return dropSpinner
    }

    private fun getCustomHeadView(position: Int, parent: ViewGroup): View {
        val headSpinner = inflater.inflate(headLayout, parent, false)
        val headText = headSpinner.findViewById<TextView>(headTvId)
        headText.text = textArray[position]
        val icon = headSpinner.findViewById<ImageView>(
            headIvId
        )
        icon.setImageResource(iconsArray.getResourceId(position, 0))
        return headSpinner
    }
}