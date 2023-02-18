package com.androidcollider.easyfin.home

import androidx.core.util.Pair
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieData
import io.reactivex.rxjava3.core.Flowable

/**
 * @author Ihor Bilous
 */
interface HomeMVP {
    interface Model {
        fun getBalanceAndStatistic(statisticPosition: Int): Flowable<Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>>?
        val balance: Flowable<Map<String, DoubleArray>>?
        fun getStatistic(statisticPosition: Int): Flowable<Map<String, DoubleArray>>?
    }

    interface View {
        fun setBalanceAndStatistic(mapPair: Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>)
        fun updateBalanceAndStatistic(mapPair: Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>)
        fun updateBalanceAndStatisticAfterDBImport(mapPair: Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>)
        fun updateBalance(map: Map<String, DoubleArray>)
        fun updateStatistic(map: Map<String, DoubleArray>)
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
        fun getFormattedBalance(balance: DoubleArray?): String
        val formattedStatistic: String
        fun getCurrentBalance(position: Int): DoubleArray
        fun updateTransactionStatisticArray(posCurrency: Int)
        fun getDataSetMainBalanceHorizontalBarChart(balance: DoubleArray): BarData
        val dataSetMainStatisticHorizontalBarChart: BarData
        val dataSetMainStatisticPieChart: PieData
        fun updateRates()
    }
}