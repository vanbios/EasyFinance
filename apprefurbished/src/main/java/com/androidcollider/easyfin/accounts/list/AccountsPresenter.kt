package com.androidcollider.easyfin.accounts.list

import com.androidcollider.easyfin.common.models.Account

/**
 * @author Ihor Bilous
 */
class AccountsPresenter(private val model: AccountsMVP.Model) : AccountsMVP.Presenter {
    private var view: AccountsMVP.View? = null
    override fun setView(view: AccountsMVP.View?) {
        this.view = view
    }

    override fun loadData() {
        model.accountList
            .subscribe(
                { accountList: List<AccountViewModel> ->
                    view?.setAccountList(accountList)
                })
            { obj: Throwable -> obj.printStackTrace() }
    }

    override fun getAccountById(id: Int) {
        model.getAccountById(id)
            .subscribe(
                { account: Account ->
                    view?.goToEditAccount(account)
                })
            { obj: Throwable -> obj.printStackTrace() }
    }

    override fun deleteAccountById(id: Int) {
        model.deleteAccountById(id)
            .subscribe(
                { isDeleted: Boolean ->
                    if (isDeleted) {
                        view?.deleteAccount()
                    }
                })
            { obj: Throwable -> obj.printStackTrace() }
    }
}