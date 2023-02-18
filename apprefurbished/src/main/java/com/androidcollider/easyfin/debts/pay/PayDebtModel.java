package com.androidcollider.easyfin.debts.pay;

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.repository.Repository;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

class PayDebtModel implements PayDebtMVP.Model {

    private final Repository repository;
    private final NumberFormatManager numberFormatManager;
    private final AccountsToSpinViewModelManager accountsToSpinViewModelManager;


    PayDebtModel(Repository repository,
                 NumberFormatManager numberFormatManager,
                 AccountsToSpinViewModelManager accountsToSpinViewModelManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        this.accountsToSpinViewModelManager = accountsToSpinViewModelManager;
    }

    @Override
    public Flowable<List<SpinAccountViewModel>> getAllAccounts() {
        return accountsToSpinViewModelManager.getSpinAccountViewModelList(repository.getAllAccounts());
    }

    @Override
    public Flowable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return repository.payFullDebt(idAccount, accountAmount, idDebt);
    }

    @Override
    public Flowable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return repository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount);
    }

    @Override
    public Flowable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
        return repository.takeMoreDebt(idAccount, accountAmount, idDebt, debtAmount, debtAllAmount);
    }

    @Override
    public String prepareStringToParse(String value) {
        return numberFormatManager.prepareStringToParse(value);
    }

    @Override
    public String formatAmount(double amount) {
        return numberFormatManager.doubleToStringFormatterForEdit(
                amount,
                NumberFormatManager.FORMAT_1,
                NumberFormatManager.PRECISE_1
        );
    }
}