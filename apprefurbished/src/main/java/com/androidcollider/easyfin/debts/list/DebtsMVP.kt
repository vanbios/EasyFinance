package com.androidcollider.easyfin.debts.list

import com.androidcollider.easyfin.common.models.Debt
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
interface DebtsMVP {
    interface Model {
        val debtList: Single<List<DebtViewModel>>
        fun getDebtById(id: Int): Single<Debt>
        fun deleteDebtById(id: Int): Single<Boolean>
    }

    interface View {
        fun setDebtList(debtList: List<DebtViewModel>)
        fun goToEditDebt(debt: Debt?, mode: Int)
        fun goToPayDebt(debt: Debt?, mode: Int)
        fun deleteDebt()
    }

    interface Presenter {
        fun setView(view: View?)
        fun loadData()
        fun getDebtById(id: Int, mode: Int, actionType: Int)
        fun deleteDebtById(id: Int)
    }
}