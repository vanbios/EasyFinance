package com.androidcollider.easyfin.debts.add_edit;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;
import com.androidcollider.easyfin.debts.list.DebtsFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

class AddDebtPresenter implements AddDebtMVP.Presenter {

    @Nullable
    private AddDebtMVP.View view;
    private final AddDebtMVP.Model model;
    private final Context context;

    private int mode, debtType;
    private Debt debtFrIntent;


    AddDebtPresenter(Context context,
                     AddDebtMVP.Model model) {
        this.context = context;
        this.model = model;
    }

    @Override
    public void setView(@Nullable AddDebtMVP.View view) {
        this.view = view;
    }

    @Override
    public void setArguments(Bundle args) {
        mode = args.getInt(DebtsFragment.MODE, 0);
        if (mode == DebtsFragment.EDIT) {
            debtFrIntent = (Debt) args.getSerializable(DebtsFragment.DEBT);
        } else {
            debtType = args.getInt(DebtsFragment.TYPE, 0);
        }
    }

    @Override
    public void loadAccounts() {
        model.getAllAccounts()
                .subscribe(
                        this::setupView,
                        Throwable::printStackTrace
                );
    }

    @Override
    public void save() {
        switch (mode) {
            case DebtsFragment.ADD:
                addDebt();
                break;
            case DebtsFragment.EDIT:
                editDebt();
                break;
        }
    }

    private void addDebt() {
        if (view != null) {
            if (validateName(view.getName())) {
                SpinAccountViewModel account = view.getAccount();
                double accountAmount = account.getAmount();
                double amount = Double.parseDouble(model.prepareStringToParse(view.getAmount()));

                if (checkIsEnoughCosts(debtType, amount, accountAmount)) {
                    switch (debtType) {
                        case DebtsFragment.TYPE_GIVE:
                            accountAmount -= amount;
                            break;
                        case DebtsFragment.TYPE_TAKE:
                            accountAmount += amount;
                            break;
                    }

                    Debt debt = buildDebt(account, amount, debtType, view.getName(), view.getDate(), accountAmount);
                    /*Debt debt = Debt.builder()
                            .name(view.getName())
                            .amountCurrent(amount)
                            .type(debtType)
                            .idAccount(account.getId())
                            .date(model.getMillisFromString(view.getDate()))
                            .accountAmount(accountAmount)
                            .currency(account.getCurrency())
                            .accountName(account.getName())
                            .amountAll(amount)
                            .build();*/

                    handleActionWithDebt(
                            model.addNewDebt(debt)
                    );
                }
            }
        }
    }

    private void editDebt() {
        if (view != null) {
            if (validateName(view.getName())) {
                SpinAccountViewModel account = view.getAccount();
                double accountAmount = account.getAmount();
                int type = debtType;
                double amount = Double.parseDouble(model.prepareStringToParse(view.getAmount()));

                int accountId = account.getId();
                int oldAccountId = debtFrIntent.getIdAccount();

                boolean isAccountsTheSame = accountId == oldAccountId;

                double oldAmount = debtFrIntent.getAmountCurrent();
                double oldAccountAmount = 0;
                int oldType = debtFrIntent.getType();

                if (isAccountsTheSame) {
                    switch (oldType) {
                        case DebtsFragment.TYPE_GIVE:
                            accountAmount += oldAmount;
                            break;
                        case DebtsFragment.TYPE_TAKE:
                            accountAmount -= oldAmount;
                            break;
                    }
                } else {
                    List<SpinAccountViewModel> accountList = view.getAccounts();
                    for (int i = 0; i < accountList.size(); i++) {
                        if (oldAccountId == accountList.get(i).getId()) {
                            oldAccountAmount = accountList.get(i).getAmount();
                            break;
                        }
                    }

                    switch (oldType) {
                        case DebtsFragment.TYPE_GIVE:
                            oldAccountAmount += oldAmount;
                            break;
                        case DebtsFragment.TYPE_TAKE:
                            oldAccountAmount -= oldAmount;
                            break;
                    }
                }

                if (checkIsEnoughCosts(type, amount, accountAmount)) {
                    switch (type) {
                        case DebtsFragment.TYPE_GIVE:
                            accountAmount -= amount;
                            break;
                        case DebtsFragment.TYPE_TAKE:
                            accountAmount += amount;
                            break;
                    }

                    Debt debt = buildDebt(account, amount, type, view.getName(), view.getDate(), accountAmount);

                    if (isAccountsTheSame) {
                        handleActionWithDebt(
                                model.updateDebt(debt)
                        );
                    } else {
                        handleActionWithDebt(
                                model.updateDebtDifferentAccounts(
                                        debt,
                                        oldAccountAmount,
                                        oldAccountId
                                )
                        );
                    }
                }
            }
        }
    }

    private boolean checkIsEnoughCosts(int type, double amount, double accountAmount) {
        if (type == DebtsFragment.TYPE_GIVE && Math.abs(amount) > accountAmount) {
            if (view != null) {
                view.showMessage(context.getString(R.string.not_enough_costs));
            }
            return false;
        }
        return true;
    }

    private boolean validateName(String name) {
        if (name.replaceAll("\\s+", "").isEmpty()) {
            if (view != null) {
                view.highlightNameField();
                view.showMessage(context.getString(R.string.empty_name_field));
            }
            return false;
        }
        return true;
    }

    private void handleActionWithDebt(Flowable<?> observable) {
        observable.subscribe(
                o -> {
                    if (view != null) {
                        view.performLastActionsAfterSaveAndClose();
                    }
                },
                Throwable::printStackTrace
        );
    }

    private Debt buildDebt(SpinAccountViewModel account,
                           double amount,
                           int type,
                           String name,
                           String date,
                           double accountAmount) {
        Debt debt = new Debt();
        debt.setName(name);
        debt.setAmountCurrent(amount);
                debt.setType(type);
                debt.setIdAccount(account.getId());
                debt.setDate(model.getMillisFromString(date));
                debt.setAccountAmount(accountAmount);
                debt.setId(debtFrIntent != null ? debtFrIntent.getId() : 0);
                debt.setCurrency(account.getCurrency());
                debt.setAccountName(account.getName());
                debt.setAmountAll(amount);
        return debt;
    }

    private void setupView(List<SpinAccountViewModel> accountList) {
        if (view != null) {
            if (accountList.isEmpty()) {
                view.notifyNotEnoughAccounts();
            } else {
                view.setAccounts(accountList);

                if (mode == DebtsFragment.ADD) {
                    view.showAmount("0,00");
                    view.openNumericDialog();
                }

                final Calendar calendar = Calendar.getInstance();
                final long initTime = System.currentTimeMillis();
                if (mode == DebtsFragment.EDIT) {
                    calendar.setTime(new Date(debtFrIntent.getDate()));
                }
                view.setupDateTimeField(calendar, initTime);

                view.setupSpinner();

                if (mode == DebtsFragment.EDIT) {
                    view.showName(debtFrIntent.getName());
                    view.showAmount(model.formatAmount(debtFrIntent.getAmountCurrent()));

                    debtType = debtFrIntent.getType();

                    int pos = 0;
                    for (int i = 0; i < accountList.size(); i++) {
                        if (debtFrIntent.getIdAccount() == accountList.get(i).getId()) {
                            pos = i;
                            break;
                        }
                    }
                    view.showAccount(pos);
                }

                view.setAmountTextColor(ContextCompat.getColor(context,
                        debtType == DebtsFragment.TYPE_TAKE ?
                                R.color.custom_red : R.color.custom_green
                ));
            }
        }
    }
}