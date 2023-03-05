package com.androidcollider.easyfin.debts.pay

import android.os.Bundle
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
interface PayDebtMVP {
    interface Model {
        val allAccounts: Single<List<SpinAccountViewModel>>
        fun payFullDebt(
            idAccount: Int,
            accountAmount: Double,
            idDebt: Int
        ): Single<Boolean>

        fun payPartOfDebt(
            idAccount: Int,
            accountAmount: Double,
            idDebt: Int,
            debtAmount: Double
        ): Single<Boolean>

        fun takeMoreDebt(
            idAccount: Int,
            accountAmount: Double,
            idDebt: Int,
            debtAmount: Double,
            debtAllAmount: Double
        ): Single<Boolean>

        fun prepareStringToParse(value: String?): String
        fun formatAmount(amount: Double): String
    }

    interface View {
        fun showAmount(amount: String?)
        fun showName(name: String?)
        fun setupSpinner()
        fun showAccount(position: Int)
        fun showMessage(message: String?)
        fun openNumericDialog()
        fun notifyNotEnoughAccounts()
        fun disableAmountField()
        fun performLastActionsAfterSaveAndClose()
        val amount: String
        val account: SpinAccountViewModel
        var accounts: List<SpinAccountViewModel>
    }

    interface Presenter {
        fun setView(view: View?)
        fun setArguments(args: Bundle?)
        fun loadAccounts()
        fun save()
    }
}