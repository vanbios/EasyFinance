package com.androidcollider.easyfin.accounts.list

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.models.Account
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class AccountsModel(
    private val repository: Repository,
    private val numberFormatManager: NumberFormatManager,
    resourcesManager: ResourcesManager
) : AccountsMVP.Model {
    private val curArray: Array<String>
    private val curLangArray: Array<String>

    init {
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
        curLangArray =
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG)
    }

    override val accountList: Single<List<AccountViewModel>>
        get() = repository.allAccounts!!.map { accountList: List<Account> ->
            transformAccountListToViewModelList(
                accountList
            )
        }

    override fun getAccountById(id: Int): Single<Account> {
        return repository.allAccounts!!.flatMap { source: List<Account> ->
            Single.just(source.first { account: Account -> account.id == id })
        }
    }

    override fun deleteAccountById(id: Int): Single<Boolean> {
        return repository.deleteAccount(id)
    }

    private fun transformAccountToViewModel(account: Account): AccountViewModel {
        var curLang: String? = null
        for (i in curArray.indices) {
            if (account.currency == curArray[i]) {
                curLang = curLangArray[i]
                break
            }
        }
        return AccountViewModel(
            account.id,
            account.name, String.format(
                "%1\$s %2\$s",
                numberFormatManager.doubleToStringFormatter(
                    account.amount,
                    NumberFormatManager.FORMAT_1,
                    NumberFormatManager.PRECISE_1
                ),
                curLang
            ),
            account.type
        )
    }

    private fun transformAccountListToViewModelList(accountList: List<Account>)
            : List<AccountViewModel> {
        return accountList.map { transformAccountToViewModel(it) }
    }
}