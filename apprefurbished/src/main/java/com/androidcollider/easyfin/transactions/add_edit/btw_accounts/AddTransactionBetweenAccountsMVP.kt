package com.androidcollider.easyfin.transactions.add_edit.btw_accounts

import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
interface AddTransactionBetweenAccountsMVP {
    interface Model {
        val allAccounts: Single<List<SpinAccountViewModel>>?
        fun transferBTWAccounts(
            idFrom: Int,
            accountAmountFrom: Double,
            idTo: Int,
            accountAmountTo: Double
        ): Single<Boolean>

        fun getExchangeRate(currencyFrom: String, currencyTo: String): String
        fun prepareStringToParse(value: String): String
    }

    interface View {
        fun showAmount(amount: String?)
        fun showExchangeRate(rate: String?)
        fun hideExchangeRate()
        fun highlightExchangeRateField()
        fun showMessage(message: String)
        fun openNumericDialog()
        fun notifyNotEnoughAccounts()
        fun setAccounts(accountList: List<SpinAccountViewModel>?)
        fun performLastActionsAfterSaveAndClose()
        val amount: String?
        val exchangeRate: String?
        val accountFrom: SpinAccountViewModel?
        val accountTo: SpinAccountViewModel?
        val isMultiCurrencyTransaction: Boolean
    }

    interface Presenter {
        fun setView(view: View?)
        fun loadAccounts()
        fun save()
        fun setCurrencyMode()
    }
}