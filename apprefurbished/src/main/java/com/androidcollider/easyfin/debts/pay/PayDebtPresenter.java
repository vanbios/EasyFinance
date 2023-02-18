package com.androidcollider.easyfin.debts.pay;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;
import com.androidcollider.easyfin.debts.list.DebtsFragment;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

class PayDebtPresenter implements PayDebtMVP.Presenter {

    @Nullable
    private PayDebtMVP.View view;
    private final PayDebtMVP.Model model;
    private final Context context;

    private int mode;
    private Debt debt;

    PayDebtPresenter(Context context,
                     PayDebtMVP.Model model) {
        this.context = context;
        this.model = model;
    }

    @Override
    public void setView(@Nullable PayDebtMVP.View view) {
        this.view = view;
    }

    @Override
    public void setArguments(Bundle args) {
        mode = args.getInt(DebtsFragment.MODE, 0);
        debt = (Debt) args.getSerializable(DebtsFragment.DEBT);
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
            case DebtsFragment.PAY_ALL:
                payAllDebt();
                break;
            case DebtsFragment.PAY_PART:
                payPartDebt();
                break;
            case DebtsFragment.TAKE_MORE:
                takeMoreDebt();
                break;
        }
    }

    private void payAllDebt() {
        if (view != null) {
            double amountDebt = debt.getAmountCurrent();
            int type = debt.getType();

            SpinAccountViewModel account = view.getAccount();

            double amountAccount = account.getAmount();

            if (type == DebtsFragment.TYPE_TAKE) {
                amountAccount -= amountDebt;
            } else {
                amountAccount += amountDebt;
            }

            handleActionWithDebt(
                    model.payFullDebt(
                            account.getId(),
                            amountAccount,
                            debt.getId()
                    )
            );
        }
    }

    private void payPartDebt() {
        if (view != null) {
            String sum = model.prepareStringToParse(view.getAmount());
            if (checkForFillSumField(sum)) {
                double amountDebt = Double.parseDouble(sum);
                double amountAllDebt = debt.getAmountCurrent();

                if (amountDebt > amountAllDebt) {
                    view.showMessage(context.getString(R.string.debt_sum_more_then_amount));
                } else {
                    int type = debt.getType();
                    SpinAccountViewModel account = view.getAccount();

                    double amountAccount = account.getAmount();

                    if (type == DebtsFragment.TYPE_TAKE && amountDebt > amountAccount) {
                        view.showMessage(context.getString(R.string.not_enough_costs));
                    } else {
                        int idDebt = debt.getId();
                        int idAccount = account.getId();

                        if (type == DebtsFragment.TYPE_TAKE) {
                            amountAccount -= amountDebt;
                        } else {
                            amountAccount += amountDebt;
                        }

                        if (amountDebt == amountAllDebt) {
                            handleActionWithDebt(
                                    model.payFullDebt(
                                            idAccount,
                                            amountAccount,
                                            idDebt
                                    )
                            );
                        } else {
                            double newDebtAmount = amountAllDebt - amountDebt;
                            handleActionWithDebt(
                                    model.payPartOfDebt(
                                            idAccount,
                                            amountAccount,
                                            idDebt,
                                            newDebtAmount
                                    )
                            );
                        }
                    }
                }
            }
        }
    }

    private void takeMoreDebt() {
        if (view != null) {
            String sum = model.prepareStringToParse(view.getAmount());
            if (checkForFillSumField(sum)) {
                double amountDebt = Double.parseDouble(sum);
                double amountDebtCurrent = debt.getAmountCurrent();
                double amountDebtAll = debt.getAmountAll();

                int type = debt.getType();

                SpinAccountViewModel account = view.getAccount();

                double amountAccount = account.getAmount();

                if (type == DebtsFragment.TYPE_GIVE && amountDebt > amountAccount) {
                    view.showMessage(context.getString(R.string.not_enough_costs));
                } else {

                    switch (type) {
                        case DebtsFragment.TYPE_GIVE:
                            amountAccount -= amountDebt;
                            break;
                        case DebtsFragment.TYPE_TAKE:
                            amountAccount += amountDebt;
                            break;
                    }

                    double newDebtCurrentAmount = amountDebtCurrent + amountDebt;
                    double newDebtAllAmount = amountDebtAll + amountDebt;

                    handleActionWithDebt(
                            model.takeMoreDebt(account.getId(),
                                    amountAccount,
                                    debt.getId(),
                                    newDebtCurrentAmount,
                                    newDebtAllAmount
                            )
                    );
                }
            }
        }
    }

    private boolean checkForFillSumField(String s) {
        if (!s.matches(".*\\d.*") || Double.parseDouble(s) == 0) {
            if (view != null) {
                view.showMessage(context.getString(R.string.empty_amount_field));
            }
            return false;
        }
        return true;
    }

    private void handleActionWithDebt(Flowable<Boolean> observable) {
        observable.subscribe(
                aBoolean -> {
                    if (aBoolean && view != null) {
                        view.performLastActionsAfterSaveAndClose();
                    }
                },
                Throwable::printStackTrace
        );
    }

    private void setupView(List<SpinAccountViewModel> accountList) {
        if (view != null) {
            List<SpinAccountViewModel> accountsAvailableList = getAccountAvailableList(accountList);

            if (accountsAvailableList.isEmpty()) {
                view.notifyNotEnoughAccounts();
            } else {
                view.setAccounts(accountsAvailableList);
                view.showName(debt.getName());
                if (mode == DebtsFragment.PAY_ALL || mode == DebtsFragment.PAY_PART) {
                    view.showAmount(model.formatAmount(debt.getAmountCurrent()));
                } else {
                    view.showAmount("0,00");
                    view.openNumericDialog();
                }
                if (mode == DebtsFragment.PAY_ALL) view.disableAmountField();

                view.setupSpinner();

                int idAccount = debt.getIdAccount();
                int pos = 0;
                for (int i = 0; i < accountsAvailableList.size(); i++) {
                    if (idAccount == accountsAvailableList.get(i).getId()) {
                        pos = i;
                        break;
                    }
                }

                view.showAccount(pos);
            }
        }
    }

    private List<SpinAccountViewModel> getAccountAvailableList(List<SpinAccountViewModel> accountList) {
        List<SpinAccountViewModel> accountsAvailableList = new ArrayList<>();
        String currency = debt.getCurrency();
        double amount = debt.getAmountCurrent();
        int type = debt.getType();

        Stream.of(accountList)
                .filter(account ->
                        mode == DebtsFragment.PAY_ALL && type == DebtsFragment.TYPE_TAKE ?
                                account.getCurrency().equals(currency) && account.getAmount() >= amount :
                                account.getCurrency().equals(currency))
                .forEach(accountsAvailableList::add);

        return accountsAvailableList;
    }
}