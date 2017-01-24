package com.androidcollider.easyfin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

/**
 * @author Ihor Bilous
 */

@Getter
@ToString
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
}
