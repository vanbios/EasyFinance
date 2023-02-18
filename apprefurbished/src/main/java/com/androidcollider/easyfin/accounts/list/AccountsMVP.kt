package com.androidcollider.easyfin.accounts.list

import com.androidcollider.easyfin.common.models.Account
import io.reactivex.rxjava3.core.Flowable

/**
 * @author Ihor Bilous
 */
interface AccountsMVP {
    interface Model {
        val accountList: Flowable<List<AccountViewModel>>?
        fun getAccountById(id: Int): Flowable<Account>?
        fun deleteAccountById(id: Int): Flowable<Boolean>?
    }

    interface View {
        fun setAccountList(accountList: List<AccountViewModel>?)
        fun goToEditAccount(account: Account?)
        fun deleteAccount()
    }

    interface Presenter {
        fun setView(view: View?)
        fun loadData()
        fun getAccountById(id: Int)
        fun deleteAccountById(id: Int)
    }
}