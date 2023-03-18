package com.androidcollider.easyfin.home

import com.androidcollider.easyfin.common.managers.chart.data.ChartDataManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieData

/**
 * @author Ihor Bilous
 */
internal class HomePresenter(
    private val model: HomeMVP.Model,
    resourcesManager: ResourcesManager,
    private val numberFormatManager: NumberFormatManager,
    private val exchangeManager: ExchangeManager,
    private val chartDataManager: ChartDataManager
) : HomeMVP.Presenter {

    private var view: HomeMVP.View? = null
    private val currencyArray: Array<String>
    private val currencyLangArray: Array<String>
    private val statistic: DoubleArray = DoubleArray(2)
    private val balanceMap: MutableMap<String, DoubleArray>
    private val statisticMap: MutableMap<String, DoubleArray>

    init {
        balanceMap = HashMap()
        statisticMap = HashMap()
        currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
        currencyLangArray =
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG)
    }

    override fun setView(view: HomeMVP.View?) {
        this.view = view
    }

    override fun loadBalanceAndStatistic(statisticPosition: Int) {
        model.getBalanceAndStatistic(statisticPosition)
            .subscribe(
                { (first, second): Pair<Map<String, DoubleArray>, Map<String, DoubleArray>> ->
                    balanceMap.clear()
                    balanceMap.putAll(first)
                    statisticMap.clear()
                    statisticMap.putAll(second)
                    view?.let {
                        updateTransactionStatisticArray(it.balanceCurrencyPosition)
                        it.setBalanceAndStatistic()
                    }
                }) { obj: Throwable -> obj.printStackTrace() }
    }

    override fun updateBalanceAndStatistic(statisticPosition: Int) {
        model.getBalanceAndStatistic(statisticPosition)
            .subscribe(
                { (first, second): Pair<Map<String, DoubleArray>, Map<String, DoubleArray>> ->
                    updateRates()
                    balanceMap.clear()
                    balanceMap.putAll(first)
                    statisticMap.clear()
                    statisticMap.putAll(second)
                    view?.let {
                        updateTransactionStatisticArray(it.balanceCurrencyPosition)
                        it.updateBalanceAndStatistic()
                    }
                }) { obj: Throwable -> obj.printStackTrace() }
    }

    override fun updateBalanceAndStatisticAfterDBImport(statisticPosition: Int) {
        model.getBalanceAndStatistic(statisticPosition)
            .subscribe(
                { (first, second): Pair<Map<String, DoubleArray>, Map<String, DoubleArray>> ->
                    updateRates()
                    balanceMap.clear()
                    balanceMap.putAll(first)
                    statisticMap.clear()
                    statisticMap.putAll(second)
                    view?.let {
                        updateTransactionStatisticArray(it.balanceCurrencyPosition)
                        it.updateBalanceAndStatisticAfterDBImport()
                    }
                }) { obj: Throwable -> obj.printStackTrace() }
    }

    override fun updateBalance() {
        model.balance
            .subscribe(
                { map: Map<String, DoubleArray> ->
                    updateRates()
                    balanceMap.clear()
                    balanceMap.putAll(map)
                    view?.let {
                        updateTransactionStatisticArray(it.balanceCurrencyPosition)
                        it.updateBalance()
                    }
                }) { obj: Throwable -> obj.printStackTrace() }
    }

    override fun updateStatistic(statisticPosition: Int) {
        model.getStatistic(statisticPosition)
            .subscribe(
                { map: Map<String, DoubleArray> ->
                    statisticMap.clear()
                    statisticMap.putAll(map)
                    view?.let {
                        updateTransactionStatisticArray(it.balanceCurrencyPosition)
                        it.updateStatistic()
                    }
                }) { obj: Throwable -> obj.printStackTrace() }
    }

    override val isStatisticEmpty: Boolean
        get() = statistic[0] == 0.0 && statistic[1] == 0.0

    override fun getFormattedBalance(balance: DoubleArray): String {
        var sum = 0.0
        for (d in balance) sum += d
        return String.format(
            "%1\$s %2\$s",
            numberFormatManager.doubleToStringFormatter(
                sum,
                NumberFormatManager.FORMAT_1,
                NumberFormatManager.PRECISE_1
            ),
            currencyLang
        )
    }

    override val formattedStatistic: String
        get() = String.format(
            "%1\$s %2\$s",
            numberFormatManager.doubleToStringFormatter(
                statistic[0] + statistic[1],
                NumberFormatManager.FORMAT_1,
                NumberFormatManager.PRECISE_1
            ),
            currencyLang
        )

    override fun getDataSetMainBalanceHorizontalBarChart(balance: DoubleArray): BarData {
        return chartDataManager.getDataSetMainBalanceHorizontalBarChart(balance)
    }

    override val dataSetMainStatisticHorizontalBarChart: BarData
        get() = chartDataManager.getDataSetMainStatisticHorizontalBarChart(statistic)
    override val dataSetMainStatisticPieChart: PieData
        get() = chartDataManager.getDataSetMainStatisticPieChart(statistic)

    override fun updateRates() {
        exchangeManager.updateRates()
    }

    override fun getCurrentBalance(posCurrency: Int): DoubleArray {
        view?.let {
            if (it.isNeedToConvert)
                return convertAllCurrencyToOne(posCurrency, balanceMap, 4)
        }
        for ((key, value) in balanceMap) {
            if (currencyArray[posCurrency] == key)
                return value
        }
        return doubleArrayOf(0.0, 0.0, 0.0, 0.0)
    }

    override fun isBalanceEmpty(balance: DoubleArray): Boolean {
        balance.forEach { if (it != 0.0) return false }
        return true
    }

    private fun convertAllCurrencyToOne(
        posCurrency: Int,
        map: Map<String, DoubleArray>,
        arrSize: Int
    ): DoubleArray {
        val arr = Array(currencyArray.size) { DoubleArray(arrSize) }
        for (i in arr.indices) {
            val value = map[currencyArray[i]]
            if (value != null) {
                System.arraycopy(value, 0, arr[i], 0, arr[i].size)
                arr[i] = convertArray(
                    arr[i],
                    exchangeManager.getExchangeRate(currencyArray[i], currencyArray[posCurrency])
                )
            }
        }
        val result = DoubleArray(arrSize)
        for (i in result.indices) {
            for (a in arr) {
                result[i] += a[i]
            }
        }
        return result
    }

    private fun convertArray(arr: DoubleArray, exc: Double): DoubleArray {
        for (i in arr.indices) arr[i] /= exc
        return arr
    }

    override fun updateTransactionStatisticArray(posCurrency: Int) {
        var isNeedConvert = false
        view?.let { isNeedConvert = it.isNeedToConvert }

        if (isNeedConvert) {
            System.arraycopy(
                convertAllCurrencyToOne(posCurrency, statisticMap, 2),
                0, statistic, 0, statistic.size
            )
        } else {
            statisticMap.entries
                .filter { (key): Map.Entry<String, DoubleArray> ->
                    currencyArray[posCurrency] == key
                }
                .forEach { (_, value): Map.Entry<String, DoubleArray> ->
                    System.arraycopy(value, 0, statistic, 0, statistic.size)
                }
        }
    }

    private val currencyLang: String
        get() {
            var position = 0
            view?.let { position = it.balanceCurrencyPosition }
            return currencyLangArray[position]
        }
}