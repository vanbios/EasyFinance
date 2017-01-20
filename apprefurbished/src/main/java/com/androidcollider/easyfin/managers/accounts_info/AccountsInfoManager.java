package com.androidcollider.easyfin.managers.accounts_info;

import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.repository.Repository;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Ihor Bilous
 */

public class AccountsInfoManager {

    private List<Account> accountList;
    private Repository repository;


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

    public int getAccountsCount() {
        return accountList.size();
    }

    public Observable<Integer> getAccountsCountObservable() {
        return repository.getAllAccounts()
                .map(List::size)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void loadAccountList() {
        repository.getAllAccounts()
                .subscribe(new Subscriber<List<Account>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Account> accountList) {
                        AccountsInfoManager.this.accountList.clear();
                        AccountsInfoManager.this.accountList.addAll(accountList);
                    }
                });
    }
}