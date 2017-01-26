package com.androidcollider.easyfin.main;

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

        void setView(MainMVP.View view);

        void checkIsAccountsExists();
    }
}