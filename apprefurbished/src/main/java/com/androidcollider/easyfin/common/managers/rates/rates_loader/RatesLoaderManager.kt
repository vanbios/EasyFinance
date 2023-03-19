package com.androidcollider.easyfin.common.managers.rates.rates_loader

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.api.RatesApi
import com.androidcollider.easyfin.common.events.UpdateFrgHomeNewRates
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager
import com.androidcollider.easyfin.common.models.Rates
import com.androidcollider.easyfin.common.models.RatesRemote
import com.androidcollider.easyfin.common.repository.Repository
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * @author Ihor Bilous
 */
class RatesLoaderManager internal constructor(
    private val context: Context,
    private val ratesApi: RatesApi,
    private val repository: Repository,
    private val sharedPrefManager: SharedPrefManager,
    private val resourcesManager: ResourcesManager
) {
    fun updateRatesForExchange() {
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.update_rates_automatically), true)
            && (!sharedPrefManager.ratesInsertFirstTimeStatus || !checkForTodayUpdate()))
        { loadRates() }
    }

    private fun checkForTodayUpdate(): Boolean {
        val oldCalendar = Calendar.getInstance()
        oldCalendar.timeInMillis = sharedPrefManager.ratesUpdateTime
        return Calendar.getInstance()[Calendar.DAY_OF_YEAR] == oldCalendar[Calendar.DAY_OF_YEAR]
    }

    private fun loadRates() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ratesApi.getRates("json")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val ratesRemoteList = response.body()
                    ratesRemoteList?.let { list ->
                        val idArray = intArrayOf(3, 7, 11, 15)
                        val currencyArray =
                            resourcesManager.getStringArray(ResourcesManager.STRING_JSON_RATES)
                        val ratesList = ArrayList<Rates>()
                        for (i in idArray.indices) {
                            val id = idArray[i]
                            val cur = currencyArray[i]
                            ratesList.add(generateNewRates(id, cur, list))
                        }
                        Log.d(TAG, "rates $ratesList")

                        repository.updateRates(ratesList)
                            .subscribe({ EventBus.getDefault().post(UpdateFrgHomeNewRates()) })
                            { obj: Throwable -> obj.printStackTrace() }
                    }
                } else {
                    Log.d(TAG, "Error : ${response.message()} ")
                }
            }
        }
    }

    private fun generateNewRates(
        id: Int,
        cur: String,
        ratesRemoteList: List<RatesRemote>
    ): Rates {
        val date = System.currentTimeMillis()
        var rate = 1.0
        for (ratesRemote in ratesRemoteList) {
            if (cur == ratesRemote.cc!!.lowercase(Locale.getDefault())) {
                rate = ratesRemote.rate
                break
            }
        }
        return Rates(id, date, cur, "bank", rate, rate)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(TAG, "Exception handled: ${throwable.localizedMessage}")
    }

    companion object {
        private val TAG = RatesLoaderManager::class.java.simpleName
    }
}