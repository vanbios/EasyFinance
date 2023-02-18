package com.androidcollider.easyfin.common.app

import androidx.multidex.MultiDexApplication

/**
 * @author Ihor Bilous
 */
class App : MultiDexApplication() {
    var component: AppComponent? = null
        private set

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}