package com.androidcollider.easyfin.common.models

import java.io.Serializable

/**
 * @author Ihor Bilous
 */
class Debt : Serializable {
    var id = 0
    var type = 0
    var idAccount = 0
    var name: String? = null
    var accountName: String? = null
    var currency: String? = null
    var amountCurrent = 0.0
    var amountAll = 0.0
    var accountAmount = 0.0
    var date: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Debt
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id
    }
}