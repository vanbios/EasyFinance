package com.androidcollider.easyfin.common.ui.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel

/**
 * @author Ihor Bilous
 */
class SpinAccountForTransHeadIconAdapter(
    context: Context,
    private val headLayout: Int,
    accountsList: List<SpinAccountViewModel>,
    resourcesManager: ResourcesManager
) : SpinAccountAdapter(context, headLayout, accountsList, resourcesManager) {

    override fun getCustomHeadView(position: Int, parent: ViewGroup?): View {
        val headSpinner = inflater.inflate(headLayout, parent, false)
        val account = getItem(position)
        val headText = headSpinner.findViewById<TextView>(R.id.tvSpinHeadIconText)
        headText.text = account.name
        val headIcon = headSpinner.findViewById<ImageView>(R.id.ivSpinHeadIconText)
        headIcon.setImageResource(typeIconsArray.getResourceId(account.type, 0))
        return headSpinner
    }
}