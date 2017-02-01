package com.androidcollider.easyfin.transactions.add_edit.btw_accounts;

import android.content.Context;

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager;
import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class AddTransactionBetweenAccountsModule {

    @Provides
    AddTransactionBetweenAccountsMVP.Model provideAddTransactionBetweenAccountsMVPModel(Repository repository,
                                                                                        NumberFormatManager numberFormatManager,
                                                                                        ExchangeManager exchangeManager,
                                                                                        AccountsToSpinViewModelManager accountsToSpinViewModelManager) {
        return new AddTransactionBetweenAccountsModel(repository, numberFormatManager, exchangeManager, accountsToSpinViewModelManager);
    }

    @Provides
    AddTransactionBetweenAccountsMVP.Presenter provideAddTransactionBetweenAccountsMVPPresenter(Context context,
                                                                                                AddTransactionBetweenAccountsMVP.Model model) {
        return new AddTransactionBetweenAccountsPresenter(context, model);
    }
}