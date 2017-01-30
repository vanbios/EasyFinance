package com.androidcollider.easyfin.debts.pay;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.repository.Repository;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

class PayDebtModel implements PayDebtMVP.Model {

    private Repository repository;
    private NumberFormatManager numberFormatManager;
    private final String[] curArray, curLangArray;


    PayDebtModel(Repository repository,
                 NumberFormatManager numberFormatManager,
                 ResourcesManager resourcesManager) {
        this.repository = repository;
        this.numberFormatManager = numberFormatManager;
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
    }

    @Override
    public Observable<List<SpinAccountViewModel>> getAllAccounts() {
        return repository.getAllAccounts()
                .map(this::transformAccountListToViewModelList);
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