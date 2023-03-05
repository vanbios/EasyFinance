package com.androidcollider.easyfin.common.managers.accounts.accounts_info

import com.androidcollider.easyfin.common.models.Account
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author Ihor Bilous
 */
class AccountsInfoManager internal constructor(private val repository: Repository) {
    private val accountList: MutableList<Account>

    init {
        accountList = ArrayList()
        loadAccountList()
    }

    private val accountNames: List<String>
        get() = accountList.map(Account::name)

    fun checkForAccountNameMatches(name: String): Boolean {
        val accountNames = accountNames
        for (account in accountNames) {
            if (account == name) return true
        }
        return false
    }

    val accountsCountObservable: Single<Int>
        get() = repository.allAccounts!!.map { obj: List<Account> -> obj.size }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())

    private fun loadAccountList() {
        repository.allAccounts?.subscribe(
            { accounts: List<Account> ->
                accountList.clear()
                accountList.addAll(accounts)
            }) { obj: Throwable -> obj.printStackTrace() }
    }
}