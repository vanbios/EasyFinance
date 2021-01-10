package com.androidcollider.easyfin.common.models

/**
 * @author Ihor Bilous
 */
data class Rates(val id: Int, val date: Long, val currency: String?,
                 val rateType: String?, val bid: Double, val ask: Double)