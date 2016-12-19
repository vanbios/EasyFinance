package com.androidcollider.easyfin.modules;

import com.androidcollider.easyfin.api.RatesApi;
import com.androidcollider.easyfin.managers.ApiManager;

/**
 * @author Ihor Bilous <ibilous@grossum.com>
 * @copyright (c) Grossum. (http://www.grossum.com)
 * @package net.dressbox.app.filters
 */
public class RatesApiModule {

    public RatesApi getRatesApi(ApiManager apiManager) {
        return apiManager.getRetrofit().create(RatesApi.class);
    }
}
