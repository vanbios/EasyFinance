package com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

public class AccountsToSpinViewModelManager {

    private NumberFormatManager numberFormatManager;
    private final String[] curArray, curLangArray;


    AccountsToSpinViewModelManager(NumberFormatManager numberFormatManager, ResourcesManager resourcesManager) {
        this.numberFormatManager = numberFormatManager;
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
    }

    public Observable<List<SpinAccountViewModel>> getSpinAccountViewModelList(Observable<List<Account>> accountObservable) {
        return accountObservable
                .map(this::transformAccountListToViewModelList);
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