package com.androidcollider.easyfin.common.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import lombok.Getter;

/**
 * @author Ihor Bilous
 */

public class App extends Application {

    @Getter
    private AppComponent component;


    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        Fabric.with(this, new Crashlytics());
    }
}