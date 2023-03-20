package com.androidcollider.easyfin.common.utils

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.androidcollider.easyfin.R


@AppCompatDelegate.NightMode
fun getSelectedThemeMode(context: Context): Int {
    return getSelectedThemeMode(readPersistedTheme(context))
}

@AppCompatDelegate.NightMode
fun getSelectedThemeMode(isSelected: Boolean): Int {
    return if (isSelected)
        AppCompatDelegate.MODE_NIGHT_YES
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM else AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
}

fun readPersistedTheme(context: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getBoolean(context.getString(R.string.night_theme), false)
}

fun isDarkTheme(context: Context?): Boolean {
    return context?.resources?.getBoolean(R.bool.dark_theme) ?: false
}