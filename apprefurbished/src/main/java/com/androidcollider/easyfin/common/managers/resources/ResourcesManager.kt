package com.androidcollider.easyfin.common.managers.resources

import android.content.Context
import android.content.res.TypedArray
import com.androidcollider.easyfin.R

/**
 * @author Ihor Bilous
 */
class ResourcesManager internal constructor(private val context: Context) {

    private val stringArrayResMap: MutableMap<Int, Array<String>>
    private val iconArrayResMap: MutableMap<Int, TypedArray>

    init {
        stringArrayResMap = HashMap()
        iconArrayResMap = HashMap()
    }

    fun getStringArray(resId: Int): Array<String> {
        if (!stringArrayResMap.containsKey(resId)) {
            stringArrayResMap[resId] = context.resources.getStringArray(resId)
        }
        return stringArrayResMap[resId]!!
    }

    fun getIconArray(resId: Int): TypedArray {
        if (!iconArrayResMap.containsKey(resId)) {
            iconArrayResMap[resId] = context.resources.obtainTypedArray(resId)
        }
        return iconArrayResMap[resId]!!
    }

    companion object {
        const val STRING_JSON_RATES = R.array.json_rates_array
        const val STRING_CHART_TYPE = R.array.chart_type_array
        const val STRING_MAIN_STATISTIC_PERIOD = R.array.main_statistic_period_array
        const val STRING_ACCOUNT_CURRENCY = R.array.account_currency_array
        const val STRING_ACCOUNT_CURRENCY_LANG = R.array.account_currency_array_language
        const val STRING_ACCOUNT_TYPE = R.array.account_type_array
        const val STRING_TRANSACTION_CATEGORY_INCOME = R.array.transaction_category_income_array
        const val STRING_TRANSACTION_CATEGORY_EXPENSE = R.array.transaction_category_expense_array
        const val ICON_TRANSACTION_CATEGORY_EXPENSE = R.array.transaction_category_expense_icons
        const val ICON_TRANSACTION_CATEGORY_INCOME = R.array.transaction_category_income_icons
        const val ICON_CURRENCY = R.array.currency_icons
        const val ICON_ACCOUNT_TYPE = R.array.account_type_icons
        const val ICON_CHART_TYPE = R.array.charts_main_icons
    }
}