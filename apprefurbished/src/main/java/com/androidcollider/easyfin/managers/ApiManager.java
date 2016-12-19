package com.androidcollider.easyfin.managers;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Ihor Bilous <ibilous@grossum.com>
 * @copyright (c) Grossum. (http://www.grossum.com)
 * @package net.dressbox.app.filters
 */
public class ApiManager {

    private static final String BASE_URL = "http://api.minfin.com.ua";

    private Retrofit mRetrofit;


    public ApiManager() {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        /*httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        });*/

        mRetrofit = retrofitBuilder
                .client(httpClient.build())
                .build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}
