package com.androidcollider.easyfin.common.api

import com.androidcollider.easyfin.common.models.RatesRemote
import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.GET
import retrofit2.http.QueryName

/**
 * @author Ihor Bilous
 */
interface RatesApi {
    @GET("exchange")
    fun getRates(@QueryName responseType: String): Flowable<List<RatesRemote>>
}