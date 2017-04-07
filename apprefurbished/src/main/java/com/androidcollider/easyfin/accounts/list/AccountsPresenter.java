package com.androidcollider.easyfin.accounts.list;

import android.support.annotation.Nullable;

/**
 * @author Ihor Bilous
 */

public class AccountsPresenter implements AccountsMVP.Presenter {

    @Nullable
    private AccountsMVP.View view;
    private AccountsMVP.Model model;

    public AccountsPresenter(AccountsMVP.Model model) {
        this.model = model;
    }

    @Override
    public void setView(@Nullable AccountsMVP.View view) {
        this.view = view;
    }

    @Override
    public void loadData() {
        model.getAccountList()
                .subscribe(
                        accountList -> {
                            if (view != null) {
                                view.setAccountList(accountList);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    @Override
    public void getAccountById(int id) {
        model.getAccountById(id)
                .subscribe(
                        account -> {
                            if (view != null) {
                                view.goToEditAccount(account);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    @Override
    public void deleteAccountById(int id) {
        model.deleteAccountById(id)
                .subscribe(
                        aBoolean -> {
                            if (aBoolean && view != null) {
                                view.deleteAccount();
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}