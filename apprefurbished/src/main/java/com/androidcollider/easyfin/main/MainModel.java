package com.androidcollider.easyfin.main;

import com.androidcollider.easyfin.common.managers.accounts.accounts_info.AccountsInfoManager;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

class MainModel implements MainMVP.Model {

    private AccountsInfoManager accountsInfoManager;

    MainModel(AccountsInfoManager accountsInfoManager) {
        this.accountsInfoManager = accountsInfoManager;
    }

    @Override
    public Flowable<Integer> getAccountsCountObservable() {
        return accountsInfoManager.getAccountsCountObservable();
    }
}