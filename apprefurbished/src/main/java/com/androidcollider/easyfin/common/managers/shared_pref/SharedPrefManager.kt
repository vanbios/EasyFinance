package com.androidcollider.easyfin.common.managers.shared_pref

import android.content.Context
import android.content.SharedPreferences

/**
 * @author Ihor Bilous
 */
class SharedPrefManager internal constructor(context: Context) {

    private val sharedPref: SharedPreferences

    init {
        sharedPref = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun disableSnackBarAccount() {
        sharedPref.edit().putBoolean("snackAccountDisabled", true).apply()
    }

    val isSnackBarAccountDisable: Boolean
        get() = sharedPref.contains("snackAccountDisabled")

    var mainBalanceSettingsConvertCheck: Boolean
        get() = sharedPref.getBoolean("mainBalanceSettingsConvertChecked", false)
        set(b) {
            sharedPref.edit().putBoolean("mainBalanceSettingsConvertChecked", b).apply()
        }

    var mainBalanceSettingsShowOnlyIntegersCheck: Boolean
        get() = sharedPref.getBoolean("mainBalanceSettingsShowOnlyIntegersChecked", true)
        set(b) {
            sharedPref.edit().putBoolean("mainBalanceSettingsShowOnlyIntegersChecked", b).apply()
        }

    fun setRatesUpdateTime() {
        sharedPref.edit().putLong("ratesUpdateTime", System.currentTimeMillis()).apply()
    }

    val ratesUpdateTime: Long
        get() = sharedPref.getLong("ratesUpdateTime", 0)

    var ratesInsertFirstTimeStatus: Boolean
        get() = sharedPref.getBoolean("ratesInsertFirstTimeStatus", false)
        set(b) {
            sharedPref.edit().putBoolean("ratesInsertFirstTimeStatus", b).apply()
        }

    var homeBalanceCurrencyPos: Int
        get() = sharedPref.getInt("homeBalanceCurrencyPos", 0)
        set(pos) {
            sharedPref.edit().putInt("homeBalanceCurrencyPos", pos).apply()
        }

    var homePeriodPos: Int
        get() = sharedPref.getInt("homePeriodPos", 1)
        set(pos) {
            sharedPref.edit().putInt("homePeriodPos", pos).apply()
        }

    var homeChartTypePos: Int
        get() = sharedPref.getInt("homeChartTypePos", 0)
        set(pos) {
            sharedPref.edit().putInt("homeChartTypePos", pos).apply()
        }

    companion object {
        private const val APP_PREFERENCES = "FinUPref"
    }
}