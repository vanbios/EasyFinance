package com.androidcollider.easyfin.main;

import androidx.annotation.Nullable;

/**
 * @author Ihor Bilous
 */

public class MainPresenter implements MainMVP.Presenter {

    @Nullable
    private MainMVP.View view;
    private MainMVP.Model model;


    public MainPresenter(MainMVP.Model model) {
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