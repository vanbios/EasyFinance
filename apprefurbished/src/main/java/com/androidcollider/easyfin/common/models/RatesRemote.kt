package com.androidcollider.easyfin.common.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Ihor Bilous
 */
class RatesRemote {

    @SerializedName("r030")
    @Expose
    var r030 = 0

    @SerializedName("txt")
    @Expose
    var txt: String? = null

    @SerializedName("rate")
    @Expose
    var rate = 0.0

    @SerializedName("cc")
    @Expose
    var cc: String? = null

    @SerializedName("exchangedate")
    @Expose
    var exchangeDate: String? = null
}