package com.androidcollider.easyfin.home

import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieData
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
interface HomeMVP {
    interface Model {
        fun getBalanceAndStatistic(statisticPosition: Int):
                Single<Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>>

        val balance: Single<Map<String, DoubleArray>>
        fun getStatistic(statisticPosition: Int): Single<Map<String, DoubleArray>>
    }

    interface View {
        fun setBalanceAndStatistic()

        fun updateBalanceAndStatistic()

        fun updateBalanceAndStatisticAfterDBImport()

        fun updateBalance()
        fun updateStatistic()
        val isNeedToConvert: Boolean
        val balanceCurrencyPosition: Int
    }

    interface Presenter {
        fun setView(view: View?)
        fun loadBalanceAndStatistic(statisticPosition: Int)
        fun updateBalanceAndStatistic(statisticPosition: Int)
        fun updateBalanceAndStatisticAfterDBImport(statisticPosition: Int)
        fun updateBalance()
        fun updateStatistic(statisticPosition: Int)
        val isStatisticEmpty: Boolean
        fun isBalanceEmpty(balance: DoubleArray): Boolean
        fun getFormattedBalance(balance: DoubleArray): String
        val formattedStatistic: String
        fun getCurrentBalance(posCurrency: Int): DoubleArray
        fun updateTransactionStatisticArray(posCurrency: Int)
        fun getDataSetMainBalanceHorizontalBarChart(
            balance: DoubleArray,
            chart: HorizontalBarChart
        ): BarData

        fun getDataSetMainStatisticHorizontalBarChart(chart: HorizontalBarChart): BarData
        fun getDataSetMainStatisticPieChart(chart: PieChart): PieData
        fun updateRates()
    }
}