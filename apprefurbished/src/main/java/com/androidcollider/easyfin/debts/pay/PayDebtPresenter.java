package com.androidcollider.easyfin.debts.pay;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.debts.list.DebtsFragment;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

class PayDebtPresenter implements PayDebtMVP.Presenter {

    @Nullable
    private PayDebtMVP.View view;
    private PayDebtMVP.Model model;
    private Context context;

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
                .subscribe(new Subscriber<List<Account>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Account> accountList) {
                        if (view != null) {
                            List<Account> accountsAvailableList = new ArrayList<>();
                            String currency = debt.getCurrency();
                            double amount = debt.getAmountCurrent();
                            int type = debt.getType();

                            Stream.of(accountList)
                                    .filter(account ->
                                            mode == DebtsFragment.PAY_ALL && type == 1 ?
                                                    account.getCurrency().equals(currency) && account.getAmount() >= amount :
                                                    account.getCurrency().equals(currency))
                                    .forEach(accountsAvailableList::add);

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
                });
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

            Account account = view.getAccount();

            double amountAccount = account.getAmount();

            if (type == 1) {
                amountAccount -= amountDebt;
            } else {
                amountAccount += amountDebt;
            }

            model.payFullDebt(account.getId(), amountAccount, debt.getId())
                    .subscribe(new Subscriber<Boolean>() {

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (aBoolean && view != null) {
                                view.performLastActionsAfterSaveAndClose();
                            }
                        }
                    });
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
                    Account account = view.getAccount();

                    double amountAccount = account.getAmount();

                    if (type == 1 && amountDebt > amountAccount) {
                        view.showMessage(context.getString(R.string.not_enough_costs));
                    } else {
                        int idDebt = debt.getId();
                        int idAccount = account.getId();

                        if (type == 1) {
                            amountAccount -= amountDebt;
                        } else {
                            amountAccount += amountDebt;
                        }

                        if (amountDebt == amountAllDebt) {
                            model.payFullDebt(idAccount, amountAccount, idDebt)
                                    .subscribe(new Subscriber<Boolean>() {

                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(Boolean aBoolean) {
                                            if (aBoolean && view != null) {
                                                view.performLastActionsAfterSaveAndClose();
                                            }
                                        }
                                    });
                        } else {
                            double newDebtAmount = amountAllDebt - amountDebt;
                            model.payPartOfDebt(idAccount, amountAccount, idDebt, newDebtAmount)
                                    .subscribe(new Subscriber<Boolean>() {

                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(Boolean aBoolean) {
                                            if (aBoolean && view != null) {
                                                view.performLastActionsAfterSaveAndClose();
                                            }
                                        }
                                    });
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

                Account account = view.getAccount();

                double amountAccount = account.getAmount();

                if (type == 0 && amountDebt > amountAccount) {
                    view.showMessage(context.getString(R.string.not_enough_costs));
                } else {

                    switch (type) {
                        case 0:
                            amountAccount -= amountDebt;
                            break;
                        case 1:
                            amountAccount += amountDebt;
                            break;
                    }

                    double newDebtCurrentAmount = amountDebtCurrent + amountDebt;
                    double newDebtAllAmount = amountDebtAll + amountDebt;

                    model.takeMoreDebt(account.getId(), amountAccount,
                            debt.getId(), newDebtCurrentAmount, newDebtAllAmount)
                            .subscribe(new Subscriber<Boolean>() {

                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Boolean aBoolean) {
                                    if (aBoolean && view != null) {
                                        view.performLastActionsAfterSaveAndClose();
                                    }
                                }
                            });
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
}