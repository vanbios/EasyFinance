package com.androidcollider.easyfin.accounts.list;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.repository.Repository;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

class AccountsModel implements AccountsMVP.Model {

    private Repository repository;
    private NumberFormatManager numberFormatManager;
    private final String[] curArray, curLangArray;


    AccountsModel(Repository repository, NumberFormatManager numberFormatManager, ResourcesManager resourcesManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
    }

    @Override
    public Observable<List<AccountViewModel>> getAccountList() {
        return repository.getAllAccounts()
                .map(this::transformAccountListToViewModelList);
    }

    @Override
    public Observable<Account> getAccountById(int id) {
        return repository.getAllAccounts()
                .flatMap(Observable::from)
                .filter(account -> account.getId() == id);
    }

    @Override
    public Observable<Boolean> deleteAccountById(int id) {
        return repository.deleteAccount(id);
    }

    private AccountViewModel transformAccountToViewModel(Account account) {
        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (account.getCurrency().equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        return new AccountViewModel(
                account.getId(),
                account.getName(),
                String.format("%1$s %2$s",
                        numberFormatManager.doubleToStringFormatter(
                                account.getAmount(),
                                NumberFormatManager.FORMAT_1,
                                NumberFormatManager.PRECISE_1
                        ),
                        curLang),
                account.getType()
        );
    }

    private List<AccountViewModel> transformAccountListToViewModelList(List<Account> accountList) {
        return Stream.of(accountList).map(this::transformAccountToViewModel).collect(Collectors.toList());
    }
}