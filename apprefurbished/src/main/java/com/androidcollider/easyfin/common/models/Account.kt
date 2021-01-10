package com.androidcollider.easyfin.common.models

import java.io.Serializable

/**
 * @author Ihor Bilous
 */

class Account : Serializable {
    var id = 0
    var name: String? = null
    var amount = 0.0
    var type = 0
    var currency: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}