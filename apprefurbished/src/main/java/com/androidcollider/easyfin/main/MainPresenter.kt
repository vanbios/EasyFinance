package com.androidcollider.easyfin.main

/**
 * @author Ihor Bilous
 */
class MainPresenter(private val model: MainMVP.Model) : MainMVP.Presenter {
    private var view: MainMVP.View? = null
    override fun setView(view: MainMVP.View?) {
        this.view = view
    }

    override fun checkIsAccountsExists() {
        model.accountsCountObservable?.subscribe(
            { count: Int ->
                if (count == 0) {
                    view?.informNoAccounts()
                }
            }, { obj: Throwable -> obj.printStackTrace() })
    }
}