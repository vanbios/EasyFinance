package com.androidcollider.easyfin.common.managers.chart.data

import android.content.Context
import androidx.core.content.ContextCompat
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlin.math.abs

/**
 * @author Ihor Bilous
 */
class ChartDataManager internal constructor(
    private val numberFormatManager: NumberFormatManager,
    private val resourcesManager: ResourcesManager,
    private val context: Context
) {

    private val textColor: Int = ContextCompat.getColor(context, R.color.custom_text_gray_dark)

    fun getDataSetMainBalanceHorizontalBarChart(values: DoubleArray): BarData {
        val cash = values[0].toFloat()
        val card = values[1].toFloat()
        val deposit = values[2].toFloat()
        val debt = abs(values[3]).toFloat()
        val accountType = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_TYPE)
        val valueSet1 = listOf(BarEntry(4f, cash))
        val valueSet2 = listOf(BarEntry(3f, card))
        val valueSet3 = listOf(BarEntry(2f, deposit))
        val valueSet4 = listOf(BarEntry(1f, debt))
        val barDataSet1 = BarDataSet(valueSet1, accountType[0])
        barDataSet1.color = ContextCompat.getColor(context, R.color.custom_teal_dark)
        val barDataSet2 = BarDataSet(valueSet2, accountType[1])
        barDataSet2.color = ContextCompat.getColor(context, R.color.custom_amber_dark)
        val barDataSet3 = BarDataSet(valueSet3, accountType[2])
        barDataSet3.color = ContextCompat.getColor(context, R.color.custom_blue_dark)
        val barDataSet4 = BarDataSet(valueSet4, context.resources.getString(R.string.debts))
        barDataSet4.color =
            if (numberFormatManager.isDoubleNegative(values[3]))
                ContextCompat.getColor(context, R.color.custom_red)
            else ContextCompat.getColor(context, R.color.custom_green)
        return getBarDataFromDataSets(
            listOf<IBarDataSet>(barDataSet4, barDataSet3, barDataSet2, barDataSet1)
        )
    }

    fun getDataSetMainStatisticHorizontalBarChart(values: DoubleArray): BarData {
        val cost = abs(values[0]).toFloat()
        val income = values[1].toFloat()
        val valueSet1 = listOf(BarEntry(2f, income))
        val valueSet2 = listOf(BarEntry(1f, cost))
        val barDataSet1 = BarDataSet(valueSet1, "income")
        barDataSet1.color = ContextCompat.getColor(context, R.color.custom_green)
        val barDataSet2 = BarDataSet(valueSet2, "cost")
        barDataSet2.color = ContextCompat.getColor(context, R.color.custom_red)
        return getBarDataFromDataSets(listOf<IBarDataSet>(barDataSet2, barDataSet1))
    }

    private fun getBarDataFromDataSets(dataSets: List<IBarDataSet>): BarData {
        val data = BarData(dataSets)
        data.setValueTextSize(12f)
        data.setValueTextColor(textColor)
        return data
    }

    fun getDataSetMainStatisticPieChart(values: DoubleArray): PieData {
        val cost = abs(values[0]).toFloat()
        val income = values[1].toFloat()
        val valueSet = listOf(PieEntry(income), PieEntry(cost))
        val pieDataSet = PieDataSet(valueSet, "statistic")
        val colors = intArrayOf(
            ContextCompat.getColor(context, R.color.custom_green),
            ContextCompat.getColor(context, R.color.custom_red)
        )
        pieDataSet.setColors(*colors)
        pieDataSet.sliceSpace = 3f
        pieDataSet.selectionShift = 5f
        val data = PieData(pieDataSet)
        data.setValueTextSize(12f)
        data.setValueTextColor(textColor)
        return data
    }
}