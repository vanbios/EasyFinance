package com.androidcollider.easyfin.debts.add_edit

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.models.Debt
import com.androidcollider.easyfin.common.repository.Repository
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class AddDebtModel(
    private val repository: Repository,
    private val numberFormatManager: NumberFormatManager,
    private val dateFormatManager: DateFormatManager,
    private val accountsToSpinViewModelManager: AccountsToSpinViewModelManager
) : AddDebtMVP.Model {
    override val allAccounts: Single<List<SpinAccountViewModel>>
        get() = accountsToSpinViewModelManager.getSpinAccountViewModelList(repository.allAccounts!!)

    override fun addNewDebt(debt: Debt): Single<Debt> {
        return repository.addNewDebt(debt)
    }

    override fun updateDebt(debt: Debt): Single<Debt> {
        return repository.updateDebt(debt)
    }

    override fun updateDebtDifferentAccounts(
        debt: Debt,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Single<Boolean> {
        return repository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId)
    }

    override fun prepareStringToParse(value: String?): String {
        return numberFormatManager.prepareStringToParse(value)
    }

    override fun getMillisFromString(date: String?): Long {
        return dateFormatManager.stringToDate(date, DateFormatManager.DAY_MONTH_YEAR_SPACED).time
    }

    override fun formatAmount(amount: Double): String {
        return numberFormatManager.doubleToStringFormatterForEdit(
            amount,
            NumberFormatManager.FORMAT_1,
            NumberFormatManager.PRECISE_1
        )
    }
}