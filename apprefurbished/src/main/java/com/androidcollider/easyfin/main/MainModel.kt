package com.androidcollider.easyfin.main

import com.androidcollider.easyfin.common.managers.accounts.accounts_info.AccountsInfoManager
import io.reactivex.rxjava3.core.Flowable

/**
 * @author Ihor Bilous
 */
internal class MainModel(private val accountsInfoManager: AccountsInfoManager) : MainMVP.Model {
    override val accountsCountObservable: Flowable<Int>
        get() = accountsInfoManager.accountsCountObservable
}