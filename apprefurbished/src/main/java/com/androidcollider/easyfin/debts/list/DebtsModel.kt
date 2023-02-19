package com.androidcollider.easyfin.debts.list;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.repository.Repository;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

class DebtsModel implements DebtsMVP.Model {

    private final Repository repository;
    private final DateFormatManager dateFormatManager;
    private final NumberFormatManager numberFormatManager;
    private final Context context;
    private final String[] curArray, curLangArray;


    DebtsModel(Repository repository,
               DateFormatManager dateFormatManager,
               NumberFormatManager numberFormatManager,
               ResourcesManager resourcesManager,
               Context context) {
        this.repository = repository;
        this.dateFormatManager = dateFormatManager;
        this.numberFormatManager = numberFormatManager;
        this.context = context;
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
    }

    @Override
    public Flowable<List<DebtViewModel>> getDebtList() {
        return repository.getAllDebts()
                .map(this::transformDebtListToViewModelList);
    }

    @Override
    public Flowable<Debt> getDebtById(int id) {
        return repository.getAllDebts()
                .flatMap(Flowable::fromIterable)
                .filter(debt -> debt.getId() == id);
    }

    @Override
    public Flowable<Boolean> deleteDebtById(int id) {
        return getDebtById(id)
                .flatMap(debt ->
                        repository.deleteDebt(
                                debt.getIdAccount(),
                                debt.getId(),
                                debt.getAmountCurrent(),
                                debt.getType()
                        )
                );
    }

    private DebtViewModel transformDebtToViewModel(Debt debt) {
        DebtViewModel model = new DebtViewModel();

        model.setId(debt.getId());
        model.setName(debt.getName());

        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (debt.getCurrency().equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        double amountCurrent = debt.getAmountCurrent();
        double amountAll = debt.getAmountAll();

        model.setAmount(
                String.format("%1$s %2$s",
                        numberFormatManager.doubleToStringFormatter(
                                amountCurrent,
                                NumberFormatManager.FORMAT_1,
                                NumberFormatManager.PRECISE_1
                        ),
                        curLang
                )
        );
        model.setAccountName(debt.getAccountName());
        model.setDate(dateFormatManager.longToDateString(debt.getDate(), DateFormatManager.DAY_MONTH_YEAR_DOTS));

        int progress = (int) (amountCurrent / amountAll * 100);
        model.setProgress(progress);
        model.setProgressPercents(String.format("%s%%", progress));

        model.setColorRes(ContextCompat.getColor(context,
                debt.getType() == 1 ? R.color.custom_red : R.color.custom_green));

        return model;
    }

    private List<DebtViewModel> transformDebtListToViewModelList(List<Debt> debtList) {
        return Stream.of(debtList).map(this::transformDebtToViewModel).collect(Collectors.toList());
    }
}