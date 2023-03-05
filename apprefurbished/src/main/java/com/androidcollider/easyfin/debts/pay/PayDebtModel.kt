package com.androidcollider.easyfin.debts.pay

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.repository.Repository
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class PayDebtModel(
    private val repository: Repository,
    private val numberFormatManager: NumberFormatManager,
    private val accountsToSpinViewModelManager: AccountsToSpinViewModelManager
) : PayDebtMVP.Model {
    override val allAccounts: Single<List<SpinAccountViewModel>>
        get() = accountsToSpinViewModelManager.getSpinAccountViewModelList(repository.allAccounts!!)

    override fun payFullDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int
    ): Single<Boolean> {
        return repository.payFullDebt(idAccount, accountAmount, idDebt)
    }

    override fun payPartOfDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double
    ): Single<Boolean> {
        return repository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount)
    }

    override fun takeMoreDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double,
        debtAllAmount: Double
    ): Single<Boolean> {
        return repository.takeMoreDebt(idAccount, accountAmount, idDebt, debtAmount, debtAllAmount)
    }

    override fun prepareStringToParse(value: String): String {
        return numberFormatManager.prepareStringToParse(value)
    }

    override fun formatAmount(amount: Double): String {
        return numberFormatManager.doubleToStringFormatterForEdit(
            amount,
            NumberFormatManager.FORMAT_1,
            NumberFormatManager.PRECISE_1
        )
    }
}