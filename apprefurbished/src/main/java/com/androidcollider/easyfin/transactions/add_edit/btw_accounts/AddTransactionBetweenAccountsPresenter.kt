package com.androidcollider.easyfin.transactions.add_edit.btw_accounts

import android.content.Context
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel

/**
 * @author Ihor Bilous
 */
internal class AddTransactionBetweenAccountsPresenter(
    private val context: Context,
    private val model: AddTransactionBetweenAccountsMVP.Model
) : AddTransactionBetweenAccountsMVP.Presenter {

    private var view: AddTransactionBetweenAccountsMVP.View? = null
    override fun setView(view: AddTransactionBetweenAccountsMVP.View?) {
        this.view = view
    }

    override fun save() {
        view?.let {
            val amount = model.prepareStringToParse(it.amount!!).toDouble()
            val accountFrom = it.accountFrom
            val accountAmountFrom = accountFrom!!.amount
            if (amount > accountAmountFrom) {
                it.showMessage(context.getString(R.string.not_enough_costs))
            } else {
                val accountIdFrom = accountFrom.id
                val accountTo = it.accountTo
                val accountIdTo = accountTo!!.id
                val accountAmountTo = accountTo.amount
                if (it.isMultiCurrencyTransaction) {
                    if (isExchangeRateValid) {
                        val exchange = model.prepareStringToParse(it.exchangeRate!!).toDouble()
                        val amountTo = amount / exchange
                        lastActions(
                            amount,
                            amountTo,
                            accountIdFrom,
                            accountIdTo,
                            accountAmountFrom,
                            accountAmountTo
                        )
                    }
                } else {
                    lastActions(
                        amount,
                        amount,
                        accountIdFrom,
                        accountIdTo,
                        accountAmountFrom,
                        accountAmountTo
                    )
                }
            }
        }
    }

    override fun loadAccounts() {
        model.allAccounts!!
            .subscribe(
                { accountList: List<SpinAccountViewModel> ->
                    view?.let {
                        if (accountList.size < 2) {
                            it.notifyNotEnoughAccounts()
                        } else {
                            it.setAccounts(accountList)
                        }
                    }
                }, { obj: Throwable -> obj.printStackTrace() })
    }

    override fun setCurrencyMode() {
        view?.let {
            val currencyFrom = it.accountFrom!!.currency
            val currencyTo = it.accountTo!!.currency
            if (checkForMultiCurrency(currencyFrom, currencyTo)) {
                it.showExchangeRate(model.getExchangeRate(currencyFrom!!, currencyTo!!))
            } else {
                it.hideExchangeRate()
            }
        }
    }

    private fun checkForMultiCurrency(currencyFrom: String?, currencyTo: String?): Boolean {
        return currencyFrom != currencyTo
    }

    private fun lastActions(
        amount: Double, amountTo: Double,
        idFrom: Int, idTo: Int,
        accAmountFrom: Double, accAmountTo: Double
    ) {
        val accountAmountFrom = accAmountFrom - amount
        val accountAmountTo = accAmountTo + amountTo
        model.transferBTWAccounts(idFrom, accountAmountFrom, idTo, accountAmountTo)
            .subscribe(
                { aBoolean: Boolean ->
                    if (aBoolean) {
                        view?.performLastActionsAfterSaveAndClose()
                    }
                }, { obj: Throwable -> obj.printStackTrace() })
    }

    private val isExchangeRateValid: Boolean
        get() {
            view?.let {
                val s = model.prepareStringToParse(it.exchangeRate!!)
                if (!s.matches(Regex(".*\\d.*")) || s.toDouble() == 0.0) {
                    it.highlightExchangeRateField()
                    it.showMessage(context.getString(R.string.empty_exchange_field))
                    return false
                }
            }
            return true
        }
}