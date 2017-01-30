package com.androidcollider.easyfin.debts.add_edit;

import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.repository.Repository;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

class AddDebtModel implements AddDebtMVP.Model {

    private Repository repository;
    private NumberFormatManager numberFormatManager;
    private DateFormatManager dateFormatManager;


    AddDebtModel(Repository repository, NumberFormatManager numberFormatManager, DateFormatManager dateFormatManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        this.dateFormatManager = dateFormatManager;
    }

    @Override
    public Observable<List<Account>> getAllAccounts() {
        return repository.getAllAccounts();
    }

    @Override
    public Observable<Debt> addNewDebt(Debt debt) {
        return repository.addNewDebt(debt);
    }

    @Override
    public Observable<Debt> updateDebt(Debt debt) {
        return repository.updateDebt(debt);
    }

    @Override
    public Observable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId) {
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