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
public class RatesNew {

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
}
