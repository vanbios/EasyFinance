package com.androidcollider.easyfin.common.app

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.androidcollider.easyfin.common.utils.getSelectedThemeMode
import com.google.android.material.color.DynamicColors

/**
 * @author Ihor Bilous
 */
class App : MultiDexApplication() {
    var component: AppComponent? = null
        private set

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        AppCompatDelegate.setDefaultNightMode(getSelectedThemeMode(base))
    }
}