package com.androidcollider.easyfin.transactions.add_edit.income_expense

import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager
import com.androidcollider.easyfin.common.models.TransactionCategory

/**
 * @author Ihor Bilous
 */
internal class TransactionCategoryAdapter(
    context: Context,
    headLayout: Int, headTvId: Int, headIvId: Int,
    dropLayout: Int, dropTvId: Int, dropIvId: Int,
    transactionCategoryList: List<TransactionCategory>,
    icons: TypedArray,
    letterTileManager: LetterTileManager
) : ArrayAdapter<TransactionCategory>(context, headLayout, transactionCategoryList) {

    private val iconsArray: TypedArray
    private val transactionCategoryList: MutableList<TransactionCategory>
    private val headLayout: Int
    private val headTvId: Int
    private val headIvId: Int
    private val dropLayout: Int
    private val dropTvId: Int
    private val dropIvId: Int
    private val inflater: LayoutInflater
    private val letterTileManager: LetterTileManager

    init {
        this.transactionCategoryList = ArrayList()
        this.transactionCategoryList.addAll(transactionCategoryList)
        iconsArray = icons
        this.headLayout = headLayout
        this.headTvId = headTvId
        this.headIvId = headIvId
        this.dropLayout = dropLayout
        this.dropTvId = dropTvId
        this.dropIvId = dropIvId
        inflater = getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.letterTileManager = letterTileManager
    }

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {
        return getCustomDropView(position, parent)
    }

    override fun getView(pos: Int, view: View?, parent: ViewGroup): View {
        return getCustomHeadView(pos, parent)
    }

    private fun getCustomDropView(position: Int, parent: ViewGroup): View {
        val dropSpinner = inflater.inflate(dropLayout, parent, false)
        val text = dropSpinner.findViewById<TextView>(dropTvId)
        val name = transactionCategoryList[position].name
        text.text = name
        val icon = dropSpinner.findViewById<ImageView>(dropIvId)
        if (position < iconsArray.length()) {
            icon.setImageResource(iconsArray.getResourceId(position, 0))
        } else {
            icon.setImageBitmap(letterTileManager.getLetterTile(name))
        }
        return dropSpinner
    }

    private fun getCustomHeadView(position: Int, parent: ViewGroup): View {
        val headSpinner = inflater.inflate(headLayout, parent, false)
        val headText = headSpinner.findViewById<TextView>(headTvId)
        val name = transactionCategoryList[position].name
        headText.text = name
        val icon = headSpinner.findViewById<ImageView>(headIvId)
        if (position < iconsArray.length()) {
            icon.setImageResource(iconsArray.getResourceId(position, 0))
        } else {
            icon.setImageBitmap(letterTileManager.getLetterTile(name))
        }
        return headSpinner
    }
}