package com.androidcollider.easyfin.main;

import android.support.annotation.Nullable;

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
                .subscribe(
                        count -> {
                            if (count == 0 && view != null) {
                                view.informNoAccounts();
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}