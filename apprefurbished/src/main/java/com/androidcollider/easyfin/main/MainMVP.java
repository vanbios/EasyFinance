package com.androidcollider.easyfin.main;

import android.support.annotation.Nullable;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

interface MainMVP {

    interface Model {

        Observable<Integer> getAccountsCountObservable();
    }

    interface View {

        void informNoAccounts();
    }

    interface Presenter {

        void setView(@Nullable MainMVP.View view);

        void checkIsAccountsExists();
    }
}