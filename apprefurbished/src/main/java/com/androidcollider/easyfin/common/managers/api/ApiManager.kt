package com.androidcollider.easyfin.common.managers.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Ihor Bilous
 */

public class ApiManager {

    private static final String BASE_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/";

    private final Retrofit mRetrofit;


    ApiManager() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.cache(null);

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        mRetrofit = retrofitBuilder
                .client(httpClient.build())
                .build();
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}