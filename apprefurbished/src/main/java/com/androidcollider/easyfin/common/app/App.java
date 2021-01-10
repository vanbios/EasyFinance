package com.androidcollider.easyfin.common.app;

import androidx.multidex.MultiDexApplication;

/**
 * @author Ihor Bilous
 */

public class App extends MultiDexApplication {

    private AppComponent component;


    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getComponent() {
        return component;
    }
}