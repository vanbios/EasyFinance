package com.androidcollider.easyfin.api;

import com.androidcollider.easyfin.objects.RatesNew;

import retrofit2.http.GET;
import rx.Observable;

/**
 * @author Ihor Bilous <ibilous@grossum.com>
 * @copyright (c) Grossum. (http://www.grossum.com)
 * @package net.dressbox.app.filters
 */
public interface RatesApi {

    @GET("/summary/f362f94f90fe9d841a98280b9098297ce4d574fa/")
    Observable<RatesNew> getRates();

}
