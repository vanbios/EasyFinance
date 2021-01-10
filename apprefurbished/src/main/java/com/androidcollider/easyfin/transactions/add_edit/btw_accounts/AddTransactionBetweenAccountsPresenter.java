package com.androidcollider.easyfin.transactions.add_edit.btw_accounts;

import android.content.Context;

import androidx.annotation.Nullable;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

/**
 * @author Ihor Bilous
 */

class AddTransactionBetweenAccountsPresenter implements AddTransactionBetweenAccountsMVP.Presenter {

    @Nullable
    private AddTransactionBetweenAccountsMVP.View view;
    private AddTransactionBetweenAccountsMVP.Model model;
    private Context context;


    AddTransactionBetweenAccountsPresenter(Context context,
                                           AddTransactionBetweenAccountsMVP.Model model) {
        this.context = context;
        this.model = model;
    }

    @Override
    public void setView(@Nullable AddTransactionBetweenAccountsMVP.View view) {
        this.view = view;
    }

    @Override
    public void save() {
        if (view != null) {
            double amount = Double.parseDouble(model.prepareStringToParse(view.getAmount()));

            SpinAccountViewModel accountFrom = view.getAccountFrom();
            double accountAmountFrom = accountFrom.getAmount();

            if (amount > accountAmountFrom) {
                view.showMessage(context.getString(R.string.not_enough_costs));
            } else {
                int accountIdFrom = accountFrom.getId();

                SpinAccountViewModel accountTo = view.getAccountTo();

                int accountIdTo = accountTo.getId();
                double accountAmountTo = accountTo.getAmount();

                if (view.isMultiCurrencyTransaction()) {
                    if (isExchangeRateValid()) {
                        double exchange = Double.parseDouble(model.prepareStringToParse(view.getExchangeRate()));
                        double amountTo = amount / exchange;
                        lastActions(amount, amountTo, accountIdFrom, accountIdTo, accountAmountFrom, accountAmountTo);
                    }
                } else {
                    lastActions(amount, amount, accountIdFrom, accountIdTo, accountAmountFrom, accountAmountTo);
                }
            }
        }
    }

    @Override
    public void loadAccounts() {
        model.getAllAccounts()
                .subscribe(
                        accountList -> {
                            if (view != null) {
                                if (accountList.size() < 2) {
                                    view.notifyNotEnoughAccounts();
                                } else {
                                    view.setAccounts(accountList);
                                }
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    @Override
    public void setCurrencyMode() {
        if (view != null) {
            String currencyFrom = view.getAccountFrom().getCurrency();
            String currencyTo = view.getAccountTo().getCurrency();
            if (checkForMultiCurrency(currencyFrom, currencyTo)) {
                view.showExchangeRate(model.getExchangeRate(currencyFrom, currencyTo));
            } else {
                view.hideExchangeRate();
            }
        }
    }

    private boolean checkForMultiCurrency(String currencyFrom, String currencyTo) {
        return !currencyFrom.equals(currencyTo);
    }

    private void lastActions(double amount, double amountTo,
                             int idFrom, int idTo,
                             double accAmountFrom, double accAmountTo) {
        double accountAmountFrom = accAmountFrom - amount;
        double accountAmountTo = accAmountTo + amountTo;

        model.transferBTWAccounts(idFrom, accountAmountFrom, idTo, accountAmountTo)
                .subscribe(
                        aBoolean -> {
                            if (aBoolean && view != null) {
                                view.performLastActionsAfterSaveAndClose();
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private boolean isExchangeRateValid() {
        if (view != null) {
            String s = model.prepareStringToParse(view.getExchangeRate());
            if (!s.matches(".*\\d.*") || Double.parseDouble(s) == 0) {
                view.highlightExchangeRateField();
                view.showMessage(context.getString(R.string.empty_exchange_field));
                return false;
            }
        }
        return true;
    }
}