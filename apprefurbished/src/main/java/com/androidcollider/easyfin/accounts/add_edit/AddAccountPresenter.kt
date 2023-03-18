package com.androidcollider.easyfin.accounts.add_edit

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.accounts.list.AccountsFragment
import com.androidcollider.easyfin.common.models.Account
import com.androidcollider.easyfin.common.utils.serializable
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class AddAccountPresenter(
    private val model: AddAccountMVP.Model,
    private val context: Context
) : AddAccountMVP.Presenter {

    private var view: AddAccountMVP.View? = null
    private var mode = 0

    override fun setView(view: AddAccountMVP.View?) {
        this.view = view
    }

    override fun setArguments(args: Bundle?) {
        args?.let { bundle ->
            mode = bundle.getInt(AccountsFragment.MODE, 0)
            when (mode) {
                AccountsFragment.ADD -> view?.let {
                    it.showAmount("0,00")
                    it.openNumericDialog()
                }
                AccountsFragment.EDIT -> {
                    (bundle.serializable(AccountsFragment.ACCOUNT) as Account?)?.let {
                        model.setAccountForUpdate(it)
                    }
                    view?.let {
                        it.showName(model.accountForUpdateName)
                        it.showAmount(model.accountForUpdateAmount)
                        it.showType(model.accountForUpdateType)
                        it.showCurrency(model.accountForUpdateCurrencyPosition)
                    }
                }
                else -> {}
            }
        }
    }

    override fun save() {
        view?.let {
            val accountName = it.accountName
            if (checkForFillNameField(accountName) && isAccountNameIsValid(accountName)) {
                val name = it.accountName
                val amount = it.accountAmount
                val type = it.accountType
                val currency = it.accountCurrency
                Log.d("ACCOUNTTEST", "presenter save")
                getSaveAccountObservable(name, amount, type, currency)
                    .subscribe({
                        Log.d("ACCOUNTTEST", "presenter result emitted")
                        view?.performLastActionsAfterSaveAndClose()
                    })
                    { obj: Throwable -> obj.printStackTrace() }
            }
        }
    }

    private fun checkForFillNameField(accountName: String): Boolean {
        if (accountName.replace("\\s+".toRegex(), "").isEmpty()) {
            view?.let {
                it.highlightNameField()
                it.showMessage(context.getString(R.string.empty_name_field))
            }
            return false
        }
        return true
    }

    private fun isAccountNameIsValid(accountName: String): Boolean {
        if (model.validateAccountName(accountName)) {
            view?.let {
                it.highlightNameField()
                it.showMessage(context.getString(R.string.account_name_exist))
            }
            return false
        }
        return true
    }

    private fun getSaveAccountObservable(
        name: String,
        amount: String,
        type: Int,
        currency: String
    ): Single<Account> {
        return if (mode == AccountsFragment.EDIT)
            model.updateAccount(name, amount, type, currency) else
            model.addAccount(name, amount, type, currency)
    }
}