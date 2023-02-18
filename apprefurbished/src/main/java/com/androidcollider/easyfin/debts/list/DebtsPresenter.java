package com.androidcollider.easyfin.debts.list;

import androidx.annotation.Nullable;

/**
 * @author Ihor Bilous
 */

class DebtsPresenter implements DebtsMVP.Presenter {

    @Nullable
    private DebtsMVP.View view;
    private final DebtsMVP.Model model;


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
                .subscribe(
                        debtList -> {
                            if (view != null) {
                                view.setDebtList(debtList);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    @Override
    public void getDebtById(int id, int mode, int actionType) {
        model.getDebtById(id)
                .subscribe(
                        debt -> {
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
                        },
                        Throwable::printStackTrace
                );
    }

    @Override
    public void deleteDebtById(int id) {
        model.deleteDebtById(id)
                .subscribe(
                        aBoolean -> {
                            if (aBoolean && view != null) {
                                view.deleteDebt();
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}