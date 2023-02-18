package com.androidcollider.easyfin.accounts.add_edit

import android.os.Bundle
import com.androidcollider.easyfin.common.models.Account
import io.reactivex.rxjava3.core.Flowable

/**
 * @author Ihor Bilous
 */
interface AddAccountMVP {
    interface Model {
        fun addAccount(
            name: String?,
            amount: String?,
            type: Int,
            currency: String?
        ): Flowable<Account>?

        fun updateAccount(
            name: String?,
            amount: String?,
            type: Int,
            currency: String?
        ): Flowable<Account>?

        fun setAccountForUpdate(account: Account?)
        val accountForUpdateName: String?
        val accountForUpdateAmount: String?
        val accountForUpdateType: Int
        val accountForUpdateCurrencyPosition: Int
        fun validateAccountName(name: String?): Boolean
    }

    interface View {
        fun showAmount(amount: String?)
        fun showName(name: String?)
        fun showType(type: Int)
        fun showCurrency(position: Int)
        fun highlightNameField()
        fun showMessage(message: String?)
        fun openNumericDialog()
        fun performLastActionsAfterSaveAndClose()
        val accountName: String?
        val accountAmount: String?
        val accountCurrency: String?
        val accountType: Int
    }

    interface Presenter {
        fun setView(view: View?)
        fun setArguments(args: Bundle?)
        fun save()
    }
}