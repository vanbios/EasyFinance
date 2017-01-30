package com.androidcollider.easyfin.transactions.add_edit.btw_accounts;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.repository.Repository;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

class AddTransactionBetweenAccountsModel implements AddTransactionBetweenAccountsMVP.Model {

    private Repository repository;
    private NumberFormatManager numberFormatManager;
    private ExchangeManager exchangeManager;
    private final String[] curArray, curLangArray;


    AddTransactionBetweenAccountsModel(Repository repository,
                                       NumberFormatManager numberFormatManager,
                                       ExchangeManager exchangeManager,
                                       ResourcesManager resourcesManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        this.exchangeManager = exchangeManager;
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
    }

    @Override
    public Observable<List<SpinAccountViewModel>> getAllAccounts() {
        return repository.getAllAccounts()
                .map(this::transformAccountListToViewModelList);
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

    private SpinAccountViewModel transformTAccountToViewModel(Account account) {
        SpinAccountViewModel.SpinAccountViewModelBuilder builder = SpinAccountViewModel.builder();

        builder.id(account.getId());
        builder.name(account.getName());
        builder.amount(account.getAmount());
        builder.type(account.getType());
        builder.currency(account.getCurrency());

        String amount = numberFormatManager.doubleToStringFormatter(
                account.getAmount(),
                NumberFormatManager.FORMAT_2,
                NumberFormatManager.PRECISE_1
        );
        String cur = account.getCurrency();
        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (cur.equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        builder.amountString(String.format("%1$s %2$s", amount, curLang));

        return builder.build();
    }

    private List<SpinAccountViewModel> transformAccountListToViewModelList(List<Account> accountList) {
        return Stream.of(accountList).map(this::transformTAccountToViewModel).collect(Collectors.toList());
    }
}