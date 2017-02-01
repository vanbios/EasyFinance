package com.androidcollider.easyfin.transactions.add_edit.income_expense;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.Calendar;
import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

interface AddTransactionIncomeExpenseMVP {

    interface Model {

        Observable<List<SpinAccountViewModel>> getAllAccounts();

        Observable<Transaction> addNewTransaction(Transaction transaction);

        Observable<Transaction> updateTransaction(Transaction transaction);

        Observable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId);

        String prepareStringToParse(String value);

        long getMillisFromString(String date);

        boolean isDoubleNegative(double d);

        String getTransactionForEditAmount(int type, double amount);
    }

    interface View {

        void showAmount(String amount, int type);

        void setupSpinners(String[] categoryArray, TypedArray categoryIcons);

        void showCategory(int position);

        void showAccount(int position);

        void showMessage(String message);

        void setupDateTimeField(Calendar calendar);

        void openNumericDialog();

        void notifyNotEnoughAccounts();

        void setAmountTextColor(int color);

        void setAccounts(List<SpinAccountViewModel> accountList);

        void performLastActionsAfterSaveAndClose();

        String getAmount();

        SpinAccountViewModel getAccount();

        String getDate();

        int getCategory();

        List<SpinAccountViewModel> getAccounts();
    }

    interface Presenter {

        void setView(@Nullable AddTransactionIncomeExpenseMVP.View view);

        void setArguments(Bundle args);

        void loadAccounts();

        void save();

        int getTransactionType();
    }
}