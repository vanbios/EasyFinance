package com.androidcollider.easyfin.common.managers.rates.rates_loader

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.api.RatesApi
import com.androidcollider.easyfin.common.events.UpdateFrgHomeNewRates
import com.androidcollider.easyfin.common.managers.connection.ConnectionManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager
import com.androidcollider.easyfin.common.models.Rates
import com.androidcollider.easyfin.common.models.RatesRemote
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * @author Ihor Bilous
 */
class RatesLoaderManager internal constructor(
    private val context: Context,
    private val ratesApi: RatesApi,
    private val repository: Repository,
    private val connectionManager: ConnectionManager,
    private val sharedPrefManager: SharedPrefManager,
    private val resourcesManager: ResourcesManager
) {
    fun updateRatesForExchange() {
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.update_rates_automatically), true)
            && connectionManager.isConnectionEnabled()
            && (!sharedPrefManager.ratesInsertFirstTimeStatus
                    || !checkForTodayUpdate())
        ) {
            loadRates()
        }
    }

    private fun checkForTodayUpdate(): Boolean {
        val oldCalendar = Calendar.getInstance()
        oldCalendar.timeInMillis = sharedPrefManager.ratesUpdateTime
        return Calendar.getInstance()[Calendar.DAY_OF_YEAR] == oldCalendar[Calendar.DAY_OF_YEAR]
    }

    private fun loadRates() {
        ratesApi.getRates("json")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { ratesRemoteList: List<RatesRemote> ->
                    val idArray = intArrayOf(3, 7, 11, 15)
                    val currencyArray =
                        resourcesManager.getStringArray(ResourcesManager.STRING_JSON_RATES)
                    val ratesList = ArrayList<Rates>()
                    for (i in idArray.indices) {
                        val id = idArray[i]
                        val cur = currencyArray[i]
                        ratesList.add(generateNewRates(id, cur, ratesRemoteList))
                    }
                    Log.d(TAG, "rates $ratesList")
                    repository.updateRates(ratesList)
                        .subscribe(
                            {
                                EventBus.getDefault().post(UpdateFrgHomeNewRates())
                            }) { obj: Throwable -> obj.printStackTrace() }
                }) { obj: Throwable -> obj.printStackTrace() }
    }

    private fun generateNewRates(id: Int, cur: String, ratesRemoteList: List<RatesRemote>): Rates {
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

    companion object {
        private val TAG = RatesLoaderManager::class.java.simpleName
    }
}