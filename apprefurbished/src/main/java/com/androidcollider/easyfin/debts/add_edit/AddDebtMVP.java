package com.androidcollider.easyfin.debts.add_edit;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

interface AddDebtMVP {

    interface Model {

        Flowable<List<SpinAccountViewModel>> getAllAccounts();

        Flowable<Debt> addNewDebt(Debt debt);

        Flowable<Debt> updateDebt(Debt debt);

        Flowable<Boolean> updateDebtDifferentAccounts(Debt debt, double oldAccountAmount, int oldAccountId);

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

        void setAccounts(List<SpinAccountViewModel> accountList);

        void performLastActionsAfterSaveAndClose();

        String getAmount();

        SpinAccountViewModel getAccount();

        String getDate();

        String getName();

        List<SpinAccountViewModel> getAccounts();
    }

    interface Presenter {

        void setView(@Nullable AddDebtMVP.View view);

        void setArguments(Bundle args);

        void loadAccounts();

        void save();
    }
}