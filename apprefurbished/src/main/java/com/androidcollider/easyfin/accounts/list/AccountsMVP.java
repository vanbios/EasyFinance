package com.androidcollider.easyfin.accounts.list;

import com.androidcollider.easyfin.common.models.Account;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

interface AccountsMVP {

    interface Model {

        Observable<List<AccountViewModel>> getAccountList();

        Observable<Account> getAccountById(int id);

        Observable<Boolean> deleteAccountById(int id);
    }

    interface View {

        void setAccountList(List<AccountViewModel> accountList);

        void goToEditAccount(Account account);

        void deleteAccount();
    }

    interface Presenter {

        void setView(AccountsMVP.View view);

        void loadData();

        void getAccountById(int id);

        void deleteAccountById(int id);
    }
}