package com.androidcollider.easyfin.transactions.add_edit.btw_accounts;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.repository.Repository;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

class AddTransactionBetweenAccountsModel implements AddTransactionBetweenAccountsMVP.Model {

    private Repository repository;
    private NumberFormatManager numberFormatManager;
    private ExchangeManager exchangeManager;


    AddTransactionBetweenAccountsModel(Repository repository,
                                       NumberFormatManager numberFormatManager,
                                       ExchangeManager exchangeManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        this.exchangeManager = exchangeManager;
    }

    @Override
    public Observable<List<Account>> getAllAccounts() {
        return repository.getAllAccounts();
    }

    @Override
    public Observable<Boolean> transferBTWAccounts(int idFrom, double accountAmountFrom, int idTo, double accountAmountTo) {
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