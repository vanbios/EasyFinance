package com.androidcollider.easyfin.debts.add_edit;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.Debt;

import java.util.Calendar;
import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

interface AddDebtMVP {

    interface Model {

        Observable<List<Account>> getAllAccounts();

        Observable<Debt> addNewDebt(Debt debt);

        Observable<Debt> updateDebt(Debt debt);

        Observable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId);

        String prepareStringToParse(String value);

        long getMillisFromString(String date);

        String formatAmount(double amount);
    }

    interface View {

        void showAmount(String amount);

        void showName(String name);

        void setupSpinner();

        void showAccount(int position);

        void showMessage(String message);

        void highlightNameField();

        void setupDateTimeField(Calendar calendar, long initTime);

        void openNumericDialog();

        void notifyNotEnoughAccounts();

        void setAmountTextColor(int color);

        void setAccounts(List<Account> accountList);

        void performLastActionsAfterSaveAndClose();

        String getAmount();

        Account getAccount();

        String getDate();

        String getName();

        List<Account> getAccounts();
    }

    interface Presenter {

        void setView(@Nullable AddDebtMVP.View view);

        void setArguments(Bundle args);

        void loadAccounts();

        void save();
    }
}