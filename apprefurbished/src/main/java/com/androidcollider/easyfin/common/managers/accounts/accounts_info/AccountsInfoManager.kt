package com.androidcollider.easyfin.common.managers.accounts.accounts_info;

import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.repository.Repository;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author Ihor Bilous
 */

public class AccountsInfoManager {

    private final List<Account> accountList;
    private final Repository repository;


    AccountsInfoManager(Repository repository) {
        this.repository = repository;
        accountList = new ArrayList<>();
        loadAccountList();
    }

    private List<String> getAccountNames() {
        return Stream.of(accountList)
                .map(Account::getName)
                .collect(Collectors.toList());
    }

    public boolean checkForAccountNameMatches(String name) {
        List<String> accountNames = getAccountNames();
        for (String account : accountNames) {
            if (account.equals(name)) return true;
        }
        return false;
    }

    /*public int getAccountsCount() {
        return accountList.size();
    }*/

    public Flowable<Integer> getAccountsCountObservable() {
        return repository.getAllAccounts()
                .map(List::size)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void loadAccountList() {
        repository.getAllAccounts()
                .subscribe(
                        accounts -> {
                            accountList.clear();
                            accountList.addAll(accounts);
                        },
                        Throwable::printStackTrace
                );
    }
}