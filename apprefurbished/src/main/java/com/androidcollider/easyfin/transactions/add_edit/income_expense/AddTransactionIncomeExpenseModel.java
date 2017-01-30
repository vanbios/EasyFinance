package com.androidcollider.easyfin.transactions.add_edit.income_expense;

import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.repository.Repository;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

class AddTransactionIncomeExpenseModel implements AddTransactionIncomeExpenseMVP.Model {

    private Repository repository;
    private NumberFormatManager numberFormatManager;
    private DateFormatManager dateFormatManager;
    private final String[] curArray, curLangArray;


    AddTransactionIncomeExpenseModel(Repository repository,
                                     NumberFormatManager numberFormatManager,
                                     DateFormatManager dateFormatManager,
                                     ResourcesManager resourcesManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        this.dateFormatManager = dateFormatManager;
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
    }

    @Override
    public Observable<List<SpinAccountViewModel>> getAllAccounts() {
        return repository.getAllAccounts()
                .map(this::transformAccountListToViewModelList);
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

    private SpinAccountViewModel transformTAccountToViewModel(Account account) {
        SpinAccountViewModel.SpinAccountViewModelBuilder builder = SpinAccountViewModel.builder();

        builder.id(account.getId());
        builder.name(account.getName());
        builder.amount(account.getAmount());
        builder.type(account.getType());
        builder.currency(account.getCurrency());

        String amount = numberFormatManager.doubleToStringFormatter(
                account.getAmount(),
                NumberFormatManager.FORMAT_2,
                NumberFormatManager.PRECISE_1
        );
        String cur = account.getCurrency();
        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (cur.equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        builder.amountString(String.format("%1$s %2$s", amount, curLang));

        return builder.build();
    }

    private List<SpinAccountViewModel> transformAccountListToViewModelList(List<Account> accountList) {
        return Stream.of(accountList).map(this::transformTAccountToViewModel).collect(Collectors.toList());
    }
}