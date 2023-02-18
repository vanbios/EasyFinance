package com.androidcollider.easyfin.transactions.add_edit.btw_accounts;

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager;
import com.androidcollider.easyfin.common.repository.Repository;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

class AddTransactionBetweenAccountsModel implements AddTransactionBetweenAccountsMVP.Model {

    private final Repository repository;
    private final NumberFormatManager numberFormatManager;
    private final ExchangeManager exchangeManager;
    private final AccountsToSpinViewModelManager accountsToSpinViewModelManager;


    AddTransactionBetweenAccountsModel(Repository repository,
                                       NumberFormatManager numberFormatManager,
                                       ExchangeManager exchangeManager,
                                       AccountsToSpinViewModelManager accountsToSpinViewModelManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        this.exchangeManager = exchangeManager;
        this.accountsToSpinViewModelManager = accountsToSpinViewModelManager;
    }

    @Override
    public Flowable<List<SpinAccountViewModel>> getAllAccounts() {
        return accountsToSpinViewModelManager.getSpinAccountViewModelList(repository.getAllAccounts());
    }

    @Override
    public Flowable<Boolean> transferBTWAccounts(int idFrom, double accountAmountFrom, int idTo, double accountAmountTo) {
        return repository.transferBTWAccounts(idFrom, accountAmountFrom, idTo, accountAmountTo);
    }

    @Override
    public String getExchangeRate(String currencyFrom, String currencyTo) {
        return numberFormatManager.doubleToStringFormatter(
                exchangeManager.getExchangeRate(
                        currencyFrom,
                        currencyTo),
                NumberFormatManager.FORMAT_3,
                NumberFormatManager.PRECISE_2
        );
    }

    @Override
    public String prepareStringToParse(String value) {
        return numberFormatManager.prepareStringToParse(value);
    }
}