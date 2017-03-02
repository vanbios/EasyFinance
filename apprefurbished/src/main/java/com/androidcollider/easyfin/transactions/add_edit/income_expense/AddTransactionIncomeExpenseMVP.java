package com.androidcollider.easyfin.transactions.add_edit.income_expense;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

interface AddTransactionIncomeExpenseMVP {

    interface Model {

        Flowable<Pair<List<SpinAccountViewModel>, List<TransactionCategory>>> getAccountsAndTransactionCategories(boolean isExpense);

        Flowable<List<TransactionCategory>> getTransactionCategories(boolean isExpense);

        Flowable<Transaction> addNewTransaction(Transaction transaction);

        Flowable<Transaction> updateTransaction(Transaction transaction);

        Flowable<Boolean> updateTransactionDifferentAccounts(Transaction transaction, double oldAccountAmount, int oldAccountId);

        Flowable<TransactionCategory> addNewTransactionCategory(TransactionCategory transactionCategory, boolean isExpense);

        String prepareStringToParse(String value);

        long getMillisFromString(String date);

        boolean isDoubleNegative(double d);

        String getTransactionForEditAmount(int type, double amount);
    }

    interface View {

        void showAmount(String amount, int type);

        void setupSpinners(List<TransactionCategory> categoryList, TypedArray categoryIcons);

        void setupCategorySpinner(List<TransactionCategory> categoryList, TypedArray categoryIcons);

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

        void shakeDialogNewTransactionCategoryField();

        void dismissDialogNewTransactionCategory();
    }

    interface Presenter {

        void setView(@Nullable AddTransactionIncomeExpenseMVP.View view);

        void setArguments(Bundle args);

        void loadAccountsAndCategories();

        void save();

        int getTransactionType();

        void addNewCategory(String name);
    }
}