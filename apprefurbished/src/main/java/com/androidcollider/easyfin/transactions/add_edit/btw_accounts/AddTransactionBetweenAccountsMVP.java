package com.androidcollider.easyfin.transactions.add_edit.btw_accounts;

import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Account;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

interface AddTransactionBetweenAccountsMVP {

    interface Model {

        Observable<List<Account>> getAllAccounts();

        Observable<Boolean> transferBTWAccounts(int idFrom, double accountAmountFrom, int idTo, double accountAmountTo);

        String getExchangeRate(String currencyFrom, String currencyTo);

        String prepareStringToParse(String value);
    }

    interface View {

        void showAmount(String amount);

        void showExchangeRate(String rate);

        void hideExchangeRate();

        void highlightExchangeRateField();

        void showMessage(String message);

        void openNumericDialog();

        void notifyNotEnoughAccounts();

        void setAccounts(List<Account> accountList);

        void performLastActionsAfterSaveAndClose();

        String getAmount();

        String getExchangeRate();

        Account getAccountFrom();

        Account getAccountTo();

        boolean isMultiCurrencyTransaction();
    }

    interface Presenter {

        void setView(@Nullable AddTransactionBetweenAccountsMVP.View view);

        void loadAccounts();

        void save();

        void setCurrencyMode();
    }
}