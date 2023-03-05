package com.androidcollider.easyfin.transactions.add_edit.btw_accounts

import android.content.Context
import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class AddTransactionBetweenAccountsModule {
    @Provides
    fun provideAddTransactionBetweenAccountsMVPModel(
        repository: Repository,
        numberFormatManager: NumberFormatManager,
        exchangeManager: ExchangeManager,
        accountsToSpinViewModelManager: AccountsToSpinViewModelManager
    ): AddTransactionBetweenAccountsMVP.Model {
        return AddTransactionBetweenAccountsModel(
            repository,
            numberFormatManager,
            exchangeManager,
            accountsToSpinViewModelManager
        )
    }

    @Provides
    fun provideAddTransactionBetweenAccountsMVPPresenter(
        context: Context,
        model: AddTransactionBetweenAccountsMVP.Model
    ): AddTransactionBetweenAccountsMVP.Presenter {
        return AddTransactionBetweenAccountsPresenter(context, model)
    }
}