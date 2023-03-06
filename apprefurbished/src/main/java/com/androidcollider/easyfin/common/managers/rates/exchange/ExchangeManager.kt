package com.androidcollider.easyfin.common.managers.rates.exchange

import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.repository.Repository

/**
 * @author Ihor Bilous
 */
class ExchangeManager internal constructor(
    private val repository: Repository,
    private val resourcesManager: ResourcesManager
) {

    val rates = doubleArrayOf(1.0, 28.2847, 34.909, 0.384, 38.5238)

    init {
        updateRates()
    }

    fun updateRates() {
        repository.rates?.subscribe(
            { newRates: DoubleArray ->
                for (i in 1 until rates.size) {
                    if (newRates[i - 1] > 0) rates[i] = newRates[i - 1]
                }
            }, { obj: Throwable -> obj.printStackTrace() })
    }

    fun getExchangeRate(currFrom: String, currTo: String): Double {
        val currencyArray =
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
        var posFrom = 0
        var posTo = 0
        for (i in currencyArray.indices) {
            if (currencyArray[i] == currFrom) posFrom = i
            if (currencyArray[i] == currTo) posTo = i
        }
        return rates[posTo] / rates[posFrom]
    }
}