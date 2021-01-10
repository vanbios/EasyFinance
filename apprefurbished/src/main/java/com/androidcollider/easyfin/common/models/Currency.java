package com.androidcollider.easyfin.common.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Ihor Bilous
 */

public class Currency {
    @SerializedName("bid")
    @Expose
    public double bid;
    @SerializedName("ask")
    @Expose
    public double ask;
    @SerializedName("trendAsk")
    @Expose
    public double trendAsk;
    @SerializedName("trendBid")
    @Expose
    public double trendBid;

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }

    public double getTrendAsk() {
        return trendAsk;
    }

    public double getTrendBid() {
        return trendBid;
    }
}