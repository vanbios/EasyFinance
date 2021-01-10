package com.androidcollider.easyfin.transactions.add_edit.btw_accounts;

import androidx.annotation.Nullable;

import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

interface AddTransactionBetweenAccountsMVP {

    interface Model {

        Flowable<List<SpinAccountViewModel>> getAllAccounts();

        Flowable<Boolean> transferBTWAccounts(int idFrom, double accountAmountFrom, int idTo, double accountAmountTo);

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

        void setAccounts(List<SpinAccountViewModel> accountList);

        void performLastActionsAfterSaveAndClose();

        String getAmount();

        String getExchangeRate();

        SpinAccountViewModel getAccountFrom();

        SpinAccountViewModel getAccountTo();

        boolean isMultiCurrencyTransaction();
    }

    interface Presenter {

        void setView(@Nullable AddTransactionBetweenAccountsMVP.View view);

        void loadAccounts();

        void save();

        void setCurrencyMode();
    }
}