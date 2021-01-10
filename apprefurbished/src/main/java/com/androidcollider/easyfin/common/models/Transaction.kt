package com.androidcollider.easyfin.common.models

import java.io.Serializable

/**
 * @author Ihor Bilous
 */
class Transaction : Serializable {
    var id = 0
    var idAccount = 0
    var category = 0
    var accountType = 0
    var date: Long = 0
    var amount = 0.0
    var accountAmount = 0.0
    var currency: String? = null
    var accountName: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Transaction
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id
    }
}