package com.androidcollider.easyfin.accounts.list;

import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Account;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

public interface AccountsMVP {

    interface Model {

        Flowable<List<AccountViewModel>> getAccountList();

        Flowable<Account> getAccountById(int id);

        Flowable<Boolean> deleteAccountById(int id);
    }

    interface View {

        void setAccountList(List<AccountViewModel> accountList);

        void goToEditAccount(Account account);

        void deleteAccount();
    }

    interface Presenter {

        void setView(@Nullable AccountsMVP.View view);

        void loadData();

        void getAccountById(int id);

        void deleteAccountById(int id);
    }
}