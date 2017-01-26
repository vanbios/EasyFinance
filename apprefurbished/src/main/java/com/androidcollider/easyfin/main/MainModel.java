package com.androidcollider.easyfin.main;

import com.androidcollider.easyfin.common.managers.accounts_info.AccountsInfoManager;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

class MainModel implements MainMVP.Model {

    private AccountsInfoManager accountsInfoManager;

    MainModel(AccountsInfoManager accountsInfoManager) {
        this.accountsInfoManager = accountsInfoManager;
    }

    @Override
    public Observable<Integer> getAccountsCountObservable() {
        return accountsInfoManager.getAccountsCountObservable();
    }
}