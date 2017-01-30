package com.androidcollider.easyfin.debts.pay;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Account;

import java.util.Calendar;
import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

interface PayDebtMVP {

    interface Model {

        Observable<List<Account>> getAllAccounts();

        Observable<Boolean> payFullDebt(int idAccount,
                                        double accountAmount,
                                        int idDebt);

        Observable<Boolean> payPartOfDebt(int idAccount,
                                          double accountAmount,
                                          int idDebt,
                                          double debtAmount);

        Observable<Boolean> takeMoreDebt(int idAccount,
                                         double accountAmount,
                                         int idDebt,
                                         double debtAmount,
                                         double debtAllAmount);

        String prepareStringToParse(String value);

        String formatAmount(double amount);
    }

    interface View {

        void showAmount(String amount);

        void showName(String name);

        void setupSpinner();

        void showAccount(int position);

        void showMessage(String message);

        void openNumericDialog();

        void notifyNotEnoughAccounts();

        void disableAmountField();

        void setAccounts(List<Account> accountList);

        void performLastActionsAfterSaveAndClose();

        String getAmount();

        Account getAccount();

        List<Account> getAccounts();
    }

    interface Presenter {

        void setView(@Nullable PayDebtMVP.View view);

        void setArguments(Bundle args);

        void loadAccounts();

        void save();
    }
}