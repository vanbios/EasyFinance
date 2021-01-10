package com.androidcollider.easyfin.common.api;

import com.androidcollider.easyfin.common.models.RatesRemote;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import retrofit2.http.GET;
import retrofit2.http.QueryName;

/**
 * @author Ihor Bilous
 */

public interface RatesApi {

    @GET("exchange")
    Flowable<List<RatesRemote>> getRates(@QueryName String responseType);
}