package com.androidcollider.easyfin.main;

import androidx.annotation.Nullable;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

public interface MainMVP {

    interface Model {

        Flowable<Integer> getAccountsCountObservable();
    }

    interface View {

        void informNoAccounts();
    }

    interface Presenter {

        void setView(@Nullable MainMVP.View view);

        void checkIsAccountsExists();
    }
}