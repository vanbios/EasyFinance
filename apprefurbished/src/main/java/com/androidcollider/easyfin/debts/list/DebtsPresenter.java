package com.androidcollider.easyfin.debts.list;

import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Debt;

import java.util.List;

import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

class DebtsPresenter implements DebtsMVP.Presenter {

    @Nullable
    private DebtsMVP.View view;
    private DebtsMVP.Model model;


    DebtsPresenter(DebtsMVP.Model model) {
        this.model = model;
    }

    @Override
    public void setView(@Nullable DebtsMVP.View view) {
        this.view = view;
    }

    @Override
    public void loadData() {
        model.getDebtList()
                .subscribe(new Subscriber<List<DebtViewModel>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<DebtViewModel> debtList) {
                        if (view != null) {
                            view.setDebtList(debtList);
                        }
                    }
                });
    }

    @Override
    public void getDebtById(int id, int mode, int actionType) {
        model.getDebtById(id)
                .subscribe(new Subscriber<Debt>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Debt debt) {
                        if (view != null) {
                            switch (actionType) {
                                case DebtsFragment.ACTION_EDIT:
                                    view.goToEditDebt(debt, mode);
                                    break;
                                case DebtsFragment.ACTION_PAY:
                                    view.goToPayDebt(debt, mode);
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    public void deleteDebtById(int id) {
        model.deleteDebtById(id)
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
                            view.deleteDebt();
                        }
                    }
                });
    }
}