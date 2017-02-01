package com.androidcollider.easyfin.main;

import android.support.annotation.Nullable;

import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

class MainPresenter implements MainMVP.Presenter {

    @Nullable
    private MainMVP.View view;
    private MainMVP.Model model;


    MainPresenter(MainMVP.Model model) {
        this.model = model;
    }

    @Override
    public void setView(@Nullable MainMVP.View view) {
        this.view = view;
    }

    @Override
    public void checkIsAccountsExists() {
        model.getAccountsCountObservable()
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer count) {
                        if (count == 0 && view != null) {
                            view.informNoAccounts();
                        }
                    }
                });
    }
}