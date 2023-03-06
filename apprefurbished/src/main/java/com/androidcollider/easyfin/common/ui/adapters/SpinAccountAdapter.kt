package com.androidcollider.easyfin.common.ui.adapters

import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel

/**
 * @author Ihor Bilous
 */
abstract class SpinAccountAdapter(
    mContext: Context,
    headLayout: Int,
    val accountList: List<SpinAccountViewModel>,
    resourcesManager: ResourcesManager
) : ArrayAdapter<SpinAccountViewModel>(mContext, headLayout, accountList) {

    val typeIconsArray: TypedArray
    private val curArray: Array<String>
    private val curLangArray: Array<String>
    val inflater: LayoutInflater

    init {
        typeIconsArray = resourcesManager.getIconArray(ResourcesManager.ICON_ACCOUNT_TYPE)
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
        curLangArray =
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG)
        inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup): View {
        return getCustomDropView(position, parent)
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        return getCustomHeadView(position, parent)
    }

    private fun getCustomDropView(position: Int, parent: ViewGroup): View {
        val dropSpinner = inflater.inflate(
            R.layout.spin_account_for_trans_dropdown, parent, false
        )
        val account = getItem(position)
        val name = dropSpinner.findViewById<TextView>(R.id.tvSpinDropdownAccountName)
        name.text = account.name
        val icon = dropSpinner.findViewById<ImageView>(R.id.ivSpinDropdownAccountType)
        icon.setImageResource(typeIconsArray.getResourceId(account.type, 0))
        val amountText = dropSpinner.findViewById<TextView>(R.id.tvSpinDropdownAccountAmount)
        amountText.text = account.amountString
        return dropSpinner
    }

    abstract fun getCustomHeadView(position: Int, parent: ViewGroup?): View
    override fun getItem(position: Int): SpinAccountViewModel {
        return accountList[position]
    }
}