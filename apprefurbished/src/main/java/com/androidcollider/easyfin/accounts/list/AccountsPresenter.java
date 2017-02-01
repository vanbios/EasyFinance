package com.androidcollider.easyfin.accounts.list;

import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Account;

import java.util.List;

import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

class AccountsPresenter implements AccountsMVP.Presenter {

    @Nullable
    private AccountsMVP.View view;
    private AccountsMVP.Model model;

    AccountsPresenter(AccountsMVP.Model model) {
        this.model = model;
    }

    @Override
    public void setView(@Nullable AccountsMVP.View view) {
        this.view = view;
    }

    @Override
    public void loadData() {
        model.getAccountList()
                .subscribe(new Subscriber<List<AccountViewModel>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<AccountViewModel> accountList) {
                        if (view != null) {
                            view.setAccountList(accountList);
                        }
                    }
                });
    }

    @Override
    public void getAccountById(int id) {
        model.getAccountById(id)
                .subscribe(new Subscriber<Account>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Account account) {
                        if (view != null) {
                            view.goToEditAccount(account);
                        }
                    }
                });
    }

    @Override
    public void deleteAccountById(int id) {
        model.deleteAccountById(id)
                .subscribe(new Subscriber<Boolean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean && view != null) {
                            view.deleteAccount();
                        }
                    }
                });
    }
}