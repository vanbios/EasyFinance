package com.androidcollider.easyfin.common.api

import com.androidcollider.easyfin.common.models.RatesRemote
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryName

/**
 * @author Ihor Bilous
 */
interface RatesApi {
    @GET("exchange")
    suspend fun getRates(@QueryName responseType: String): Response<List<RatesRemote>>
}