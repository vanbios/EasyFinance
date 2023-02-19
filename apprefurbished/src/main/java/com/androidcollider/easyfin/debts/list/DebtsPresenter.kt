package com.androidcollider.easyfin.debts.list

import com.androidcollider.easyfin.common.models.Debt

/**
 * @author Ihor Bilous
 */
internal class DebtsPresenter(private val model: DebtsMVP.Model) : DebtsMVP.Presenter {
    private var view: DebtsMVP.View? = null
    override fun setView(view: DebtsMVP.View?) {
        this.view = view
    }

    override fun loadData() {
        model.debtList
            .subscribe(
                { debtList: List<DebtViewModel> -> view?.setDebtList(debtList) },
                { obj: Throwable -> obj.printStackTrace() })
    }

    override fun getDebtById(id: Int, mode: Int, actionType: Int) {
        model.getDebtById(id)
            .subscribe(
                { debt: Debt ->
                    when (actionType) {
                        DebtsFragment.ACTION_EDIT -> view?.goToEditDebt(debt, mode)
                        DebtsFragment.ACTION_PAY -> view?.goToPayDebt(debt, mode)
                    }
                },
                { obj: Throwable -> obj.printStackTrace() })
    }

    override fun deleteDebtById(id: Int) {
        model.deleteDebtById(id)
            .subscribe(
                { aBoolean: Boolean ->
                    if (aBoolean) {
                        view?.deleteDebt()
                    }
                },
                { obj: Throwable -> obj.printStackTrace() })
    }
}