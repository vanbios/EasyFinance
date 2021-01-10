package com.androidcollider.easyfin.common.models

/**
 * @author Ihor Bilous
 */

class TransactionCategory(var id: Int, var name: String, var visibility: Int = 1) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TransactionCategory

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}