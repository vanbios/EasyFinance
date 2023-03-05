package com.androidcollider.easyfin.debts.add_edit

import android.os.Bundle
import com.androidcollider.easyfin.common.models.Debt
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import io.reactivex.rxjava3.core.Single
import java.util.*

/**
 * @author Ihor Bilous
 */
interface AddDebtMVP {
    interface Model {
        val allAccounts: Single<List<SpinAccountViewModel>>
        fun addNewDebt(debt: Debt): Single<Debt>
        fun updateDebt(debt: Debt): Single<Debt>
        fun updateDebtDifferentAccounts(
            debt: Debt,
            oldAccountAmount: Double,
            oldAccountId: Int
        ): Single<Boolean>

        fun prepareStringToParse(value: String?): String
        fun getMillisFromString(date: String?): Long
        fun formatAmount(amount: Double): String
    }

    interface View {
        fun showAmount(amount: String?)
        fun showName(name: String?)
        fun setupSpinner()
        fun showAccount(position: Int)
        fun showMessage(message: String?)
        fun highlightNameField()
        fun setupDateTimeField(calendar: Calendar?, initTime: Long)
        fun openNumericDialog()
        fun notifyNotEnoughAccounts()
        fun setAmountTextColor(color: Int)
        fun performLastActionsAfterSaveAndClose()
        val amount: String?
        val account: SpinAccountViewModel?
        val date: String?
        val name: String?
        var accounts: List<SpinAccountViewModel>?
    }

    interface Presenter {
        fun setView(view: View?)
        fun setArguments(args: Bundle?)
        fun loadAccounts()
        fun save()
    }
}