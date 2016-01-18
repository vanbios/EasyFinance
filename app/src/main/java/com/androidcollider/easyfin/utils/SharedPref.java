package com.androidcollider.easyfin.utils;

import android.content.Context;
import android.content.SharedPreferences;



public class SharedPref {

    private SharedPreferences sharedPreferences;
    private final static String APP_PREFERENCES = "FinUPref";

    public SharedPref(Context context) {
        this.sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void disableSnackBarAccount() {
        sharedPreferences.edit().putBoolean("snackAccountDisabled", true).apply();
    }

    public boolean isSnackBarAccountDisable() {
        return sharedPreferences.contains("snackAccountDisabled");
    }


    public void setMainBalanceSettingsConvertCheck(boolean b) {
        sharedPreferences.edit().putBoolean("mainBalanceSettingsConvertChecked", b).apply();
    }

    public boolean getMainBalanceSettingsConvertCheck() {
        return sharedPreferences.getBoolean("mainBalanceSettingsConvertChecked", false);
    }


    public void setMainBalanceSettingsShowOnlyIntegersCheck(boolean b) {
        sharedPreferences.edit().putBoolean("mainBalanceSettingsShowOnlyIntegersChecked", b).apply();
    }

    public boolean getMainBalanceSettingsShowOnlyIntegersCheck() {
        return sharedPreferences.getBoolean("mainBalanceSettingsShowOnlyIntegersChecked", true);
    }


    public void setRatesUpdateTime() {
        sharedPreferences.edit().putLong("ratesUpdateTime", System.currentTimeMillis()).apply();
    }

    public long getRatesUpdateTime() {
        return sharedPreferences.getLong("ratesUpdateTime", 0);
    }

    public void setRatesInsertFirstTimeStatus(boolean b) {
        sharedPreferences.edit().putBoolean("ratesInsertFirstTimeStatus", b).apply();
    }

    public boolean getRatesInsertFirstTimeStatus() {
        return sharedPreferences.getBoolean("ratesInsertFirstTimeStatus", false);
    }

}
