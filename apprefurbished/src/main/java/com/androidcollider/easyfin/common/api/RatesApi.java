package com.androidcollider.easyfin.common.api;

import com.androidcollider.easyfin.common.models.RatesRemote;

import io.reactivex.Flowable;
import retrofit2.http.GET;

/**
 * @author Ihor Bilous
 */

public interface RatesApi {

    @GET("/summary/f362f94f90fe9d841a98280b9098297ce4d574fa/")
    Flowable<RatesRemote> getRates();
}