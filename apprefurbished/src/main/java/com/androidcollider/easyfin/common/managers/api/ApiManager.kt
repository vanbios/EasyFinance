package com.androidcollider.easyfin.common.managers.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Ihor Bilous
 */
class ApiManager internal constructor() {
    val retrofit: Retrofit

    init {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        httpClient.cache(null)
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
        retrofit = retrofitBuilder
            .client(httpClient.build())
            .build()
    }

    companion object {
        private const val BASE_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/"
    }
}