package com.androidcollider.easyfin.main

import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
interface MainMVP {
    interface Model {
        val accountsCountObservable: Single<Int>?
    }

    interface View {
        fun informNoAccounts()
    }

    interface Presenter {
        fun setView(view: View?)
        fun checkIsAccountsExists()
    }
}