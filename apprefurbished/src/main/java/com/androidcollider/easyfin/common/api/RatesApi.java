package com.androidcollider.easyfin.common.api;

import com.androidcollider.easyfin.common.models.RatesRemote;

import retrofit2.http.GET;
import rx.Observable;

/**
 * @author Ihor Bilous
 */

public interface RatesApi {

    @GET("/summary/f362f94f90fe9d841a98280b9098297ce4d574fa/")
    Observable<RatesRemote> getRates();
}
