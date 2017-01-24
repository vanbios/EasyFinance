package com.androidcollider.easyfin.common.managers.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Ihor Bilous
 */

public class ApiManager {

    private static final String BASE_URL = "http://api.minfin.com.ua";

    private Retrofit mRetrofit;


    ApiManager() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        mRetrofit = retrofitBuilder
                .client(new OkHttpClient.Builder().build())
                .build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}