package com.androidcollider.easyfin.common.models

import java.io.Serializable

/**
 * @author Ihor Bilous
 */

class Account : Serializable {
    var id = 0
    lateinit var name: String
    var amount = 0.0
    var type = 0
    lateinit var currency: String

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