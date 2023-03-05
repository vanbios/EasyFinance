package com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.models.Account
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
class AccountsToSpinViewModelManager internal constructor(
    private val numberFormatManager: NumberFormatManager,
    resourcesManager: ResourcesManager
) {
    private val curArray: Array<String>
    private val curLangArray: Array<String>

    init {
        curArray =
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
        curLangArray =
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG)
    }

    fun getSpinAccountViewModelList(accountObservable: Single<List<Account>>):
            Single<List<SpinAccountViewModel>> {
        return accountObservable
            .map { accountList: List<Account> ->
                transformAccountListToViewModelList(
                    accountList
                )
            }
    }

    private fun transformTAccountToViewModel(account: Account): SpinAccountViewModel {
        val spinAccountViewModel = SpinAccountViewModel()
        spinAccountViewModel.id = account.id
        spinAccountViewModel.name = account.name
        spinAccountViewModel.amount = account.amount
        spinAccountViewModel.type = account.type
        spinAccountViewModel.currency = account.currency
        val amount = numberFormatManager.doubleToStringFormatter(
            account.amount,
            NumberFormatManager.FORMAT_2,
            NumberFormatManager.PRECISE_1
        )
        val cur = account.currency
        var curLang: String? = null
        for (i in curArray.indices) {
            if (cur == curArray[i]) {
                curLang = curLangArray[i]
                break
            }
        }
        spinAccountViewModel.amountString = String.format("%1\$s %2\$s", amount, curLang)
        return spinAccountViewModel
    }

    private fun transformAccountListToViewModelList(accountList: List<Account>):
            List<SpinAccountViewModel> {
        return accountList.map { account: Account -> transformTAccountToViewModel(account) }
    }
}