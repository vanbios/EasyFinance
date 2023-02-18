package com.androidcollider.easyfin.debts.add_edit;

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.repository.Repository;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

class AddDebtModel implements AddDebtMVP.Model {

    private final Repository repository;
    private final NumberFormatManager numberFormatManager;
    private final DateFormatManager dateFormatManager;
    private final AccountsToSpinViewModelManager accountsToSpinViewModelManager;


    AddDebtModel(Repository repository,
                 NumberFormatManager numberFormatManager,
                 DateFormatManager dateFormatManager,
                 AccountsToSpinViewModelManager accountsToSpinViewModelManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        this.dateFormatManager = dateFormatManager;
        this.accountsToSpinViewModelManager = accountsToSpinViewModelManager;
    }

    @Override
    public Flowable<List<SpinAccountViewModel>> getAllAccounts() {
        return accountsToSpinViewModelManager.getSpinAccountViewModelList(repository.getAllAccounts());
    }

    @Override
    public Flowable<Debt> addNewDebt(Debt debt) {
        return repository.addNewDebt(debt);
    }

    @Override
    public Flowable<Debt> updateDebt(Debt debt) {
        return repository.updateDebt(debt);
    }

    @Override
    public Flowable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
        return repository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId);
    }

    @Override
    public String prepareStringToParse(String value) {
        return numberFormatManager.prepareStringToParse(value);
    }

    @Override
    public long getMillisFromString(String date) {
        return dateFormatManager.stringToDate(date, DateFormatManager.DAY_MONTH_YEAR_SPACED).getTime();
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