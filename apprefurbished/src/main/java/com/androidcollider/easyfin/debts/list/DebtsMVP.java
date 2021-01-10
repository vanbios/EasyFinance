package com.androidcollider.easyfin.debts.list;

import com.androidcollider.easyfin.common.models.Debt;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

public interface DebtsMVP {

    interface Model {

        Flowable<List<DebtViewModel>> getDebtList();

        Flowable<Debt> getDebtById(int id);

        Flowable<Boolean> deleteDebtById(int id);
    }

    interface View {

        void setDebtList(List<DebtViewModel> debtList);

        void goToEditDebt(Debt debt, int mode);

        void goToPayDebt(Debt debt, int mode);

        void deleteDebt();
    }

    interface Presenter {

        void setView(DebtsMVP.View view);

        void loadData();

        void getDebtById(int id, int mode, int actionType);

        void deleteDebtById(int id);
    }
}