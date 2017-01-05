package com.androidcollider.easyfin.utils;

import android.content.Context;
import android.content.SharedPreferences;



public class SharedPref {

    private SharedPreferences sharedPref;
    private final static String APP_PREFERENCES = "FinUPref";

    public SharedPref(Context context) {
        this.sharedPref = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void disableSnackBarAccount() {
        sharedPref.edit().putBoolean("snackAccountDisabled", true).apply();
    }

    public boolean isSnackBarAccountDisable() {
        return sharedPref.contains("snackAccountDisabled");
    }


    public void setMainBalanceSettingsConvertCheck(boolean b) {
        sharedPref.edit().putBoolean("mainBalanceSettingsConvertChecked", b).apply();
    }

    public boolean getMainBalanceSettingsConvertCheck() {
        return sharedPref.getBoolean("mainBalanceSettingsConvertChecked", false);
    }


    public void setMainBalanceSettingsShowOnlyIntegersCheck(boolean b) {
        sharedPref.edit().putBoolean("mainBalanceSettingsShowOnlyIntegersChecked", b).apply();
    }

    public boolean getMainBalanceSettingsShowOnlyIntegersCheck() {
        return sharedPref.getBoolean("mainBalanceSettingsShowOnlyIntegersChecked", true);
    }


    public void setRatesUpdateTime() {
        sharedPref.edit().putLong("ratesUpdateTime", System.currentTimeMillis()).apply();
    }

    public long getRatesUpdateTime() {
        return sharedPref.getLong("ratesUpdateTime", 0);
    }

    public void setRatesInsertFirstTimeStatus(boolean b) {
        sharedPref.edit().putBoolean("ratesInsertFirstTimeStatus", b).apply();
    }

    public boolean getRatesInsertFirstTimeStatus() {
        return sharedPref.getBoolean("ratesInsertFirstTimeStatus", false);
    }


    public void setHomeBalanceCurrencyPos(int pos) {
        sharedPref.edit().putInt("homeBalanceCurrencyPos", pos).apply();
    }

    public int getHomeBalanceCurrencyPos() {
        return sharedPref.getInt("homeBalanceCurrencyPos", 0);
    }

    public void setHomePeriodPos(int pos) {
        sharedPref.edit().putInt("homePeriodPos", pos).apply();
    }

    public int getHomePeriodPos() {
        return sharedPref.getInt("homePeriodPos", 1);
    }

    public void setHomeChartTypePos(int pos) {
        sharedPref.edit().putInt("homeChartTypePos", pos).apply();
    }

    public int getHomeChartTypePos() {
        return sharedPref.getInt("homeChartTypePos", 0);
    }
}