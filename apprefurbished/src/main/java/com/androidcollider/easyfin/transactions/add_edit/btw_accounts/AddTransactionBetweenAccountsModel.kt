package com.androidcollider.easyfin.transactions.add_edit.btw_accounts

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager
import com.androidcollider.easyfin.common.repository.Repository
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class AddTransactionBetweenAccountsModel(
    private val repository: Repository,
    private val numberFormatManager: NumberFormatManager,
    private val exchangeManager: ExchangeManager,
    private val accountsToSpinViewModelManager: AccountsToSpinViewModelManager
) : AddTransactionBetweenAccountsMVP.Model {

    override val allAccounts: Single<List<SpinAccountViewModel>>
        get() = accountsToSpinViewModelManager.getSpinAccountViewModelList(repository.allAccounts!!)

    override fun transferBTWAccounts(
        idFrom: Int,
        accountAmountFrom: Double,
        idTo: Int,
        accountAmountTo: Double
    ): Single<Boolean> {
        return repository.transferBTWAccounts(idFrom, accountAmountFrom, idTo, accountAmountTo)
    }

    override fun getExchangeRate(currencyFrom: String, currencyTo: String): String {
        return numberFormatManager.doubleToStringFormatter(
            exchangeManager.getExchangeRate(
                currencyFrom,
                currencyTo
            ),
            NumberFormatManager.FORMAT_3,
            NumberFormatManager.PRECISE_2
        )
    }

    override fun prepareStringToParse(value: String): String {
        return numberFormatManager.prepareStringToParse(value)
    }
}