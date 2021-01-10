package com.androidcollider.easyfin.common.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Ihor Bilous
 */

public class RatesRemote {

    @SerializedName("usd")
    @Expose
    public Currency usd;
    @SerializedName("eur")
    @Expose
    public Currency eur;
    @SerializedName("rub")
    @Expose
    public Currency rub;
    @SerializedName("gbp")
    @Expose
    public Currency gbp;

    public Currency getUsd() {
        return usd;
    }

    public Currency getEur() {
        return eur;
    }

    public Currency getRub() {
        return rub;
    }

    public Currency getGbp() {
        return gbp;
    }
}