package com.androidcollider.easyfin.transactions.add_edit.income_expense;

import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.repository.Repository;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

class AddTransactionIncomeExpenseModel implements AddTransactionIncomeExpenseMVP.Model {

    private Repository repository;
    private NumberFormatManager numberFormatManager;
    private DateFormatManager dateFormatManager;


    AddTransactionIncomeExpenseModel(Repository repository,
                                     NumberFormatManager numberFormatManager,
                                     DateFormatManager dateFormatManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        this.dateFormatManager = dateFormatManager;
    }

    @Override
    public Observable<List<Account>> getAllAccounts() {
        return repository.getAllAccounts();
    }

    @Override
    public Observable<Transaction> addNewTransaction(Transaction transaction) {
        return repository.addNewTransaction(transaction);
    }

    @Override
    public Observable<Transaction> updateTransaction(Transaction transaction) {
        return repository.updateTransaction(transaction);
    }

    @Override
    public Observable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId) {
        return repository.updateTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId);
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
    public boolean isDoubleNegative(double d) {
        return numberFormatManager.isDoubleNegative(d);
    }

    @Override
    public String getTransactionForEditAmount(int type, double amount) {
        return numberFormatManager.doubleToStringFormatterForEdit(
                type == 1 ? amount : Math.abs(amount),
                NumberFormatManager.FORMAT_1,
                NumberFormatManager.PRECISE_1
        );
    }
}