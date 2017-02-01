package com.androidcollider.easyfin.debts.pay;

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.repository.Repository;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

class PayDebtModel implements PayDebtMVP.Model {

    private Repository repository;
    private NumberFormatManager numberFormatManager;
    private AccountsToSpinViewModelManager accountsToSpinViewModelManager;


    PayDebtModel(Repository repository,
                 NumberFormatManager numberFormatManager,
                 AccountsToSpinViewModelManager accountsToSpinViewModelManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        this.accountsToSpinViewModelManager = accountsToSpinViewModelManager;
    }

    @Override
    public Observable<List<SpinAccountViewModel>> getAllAccounts() {
        return accountsToSpinViewModelManager.getSpinAccountViewModelList(repository.getAllAccounts());
    }

    @Override
    public Observable<Boolean> payFullDebt(int idAccount, double accountAmount, int idDebt) {
        return repository.payFullDebt(idAccount, accountAmount, idDebt);
    }

    @Override
    public Observable<Boolean> payPartOfDebt(int idAccount, double accountAmount, int idDebt, double debtAmount) {
        return repository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount);
    }

    @Override
    public Observable<Boolean> takeMoreDebt(int idAccount, double accountAmount, int idDebt, double debtAmount, double debtAllAmount) {
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