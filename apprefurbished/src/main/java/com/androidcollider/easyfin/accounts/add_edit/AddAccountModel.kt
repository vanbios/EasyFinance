package com.androidcollider.easyfin.accounts.add_edit

import com.androidcollider.easyfin.common.managers.accounts.accounts_info.AccountsInfoManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.models.Account
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class AddAccountModel(
    private val repository: Repository,
    private val accountsInfoManager: AccountsInfoManager,
    private val numberFormatManager: NumberFormatManager,
    private val resourcesManager: ResourcesManager
) : AddAccountMVP.Model {
    private var accountForUpdate: Account? = null

    override fun addAccount(
        name: String,
        amount: String,
        type: Int,
        currency: String
    ): Single<Account> {
        val account = Account()
        account.name = name
        account.amount = numberFormatManager.prepareStringToParse(amount).toDouble()
        account.type = type
        account.currency = currency
        return repository.addNewAccount(account)
    }

    override fun updateAccount(
        name: String,
        amount: String,
        type: Int,
        currency: String
    ): Single<Account> {
        val account = Account()
        account.id = if (accountForUpdate != null) accountForUpdate!!.id else 0
        account.name = name
        account.amount = numberFormatManager.prepareStringToParse(amount).toDouble()
        account.type = type
        account.currency = currency
        return repository.updateAccount(account)
    }

    override fun setAccountForUpdate(account: Account) {
        accountForUpdate = account
    }

    override val accountForUpdateName: String
        get() = if (accountForUpdate != null) accountForUpdate!!.name else ""

    override val accountForUpdateAmount: String
        get() = if (accountForUpdate != null)
            numberFormatManager.doubleToStringFormatterForEdit(
                accountForUpdate!!.amount,
                NumberFormatManager.FORMAT_1,
                NumberFormatManager.PRECISE_1
            ) else "0,00"

    override val accountForUpdateType: Int
        get() = if (accountForUpdate != null) accountForUpdate!!.type else 0

    override val accountForUpdateCurrencyPosition: Int
        get() {
            if (accountForUpdate != null) {
                val currencyArray =
                    resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
                for (i in currencyArray.indices) {
                    if (currencyArray[i] == accountForUpdate!!.currency) {
                        return i
                    }
                }
            }
            return 0
        }

    override fun validateAccountName(name: String): Boolean {
        return if (accountForUpdate == null)
            accountsInfoManager.checkForAccountNameMatches(name)
        else accountsInfoManager.checkForAccountNameMatches(name)
                && name != accountForUpdate!!.name
    }
}