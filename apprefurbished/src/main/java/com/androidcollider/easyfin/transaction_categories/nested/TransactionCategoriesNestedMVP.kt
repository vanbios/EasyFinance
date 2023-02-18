package com.androidcollider.easyfin.transaction_categories.nested;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.androidcollider.easyfin.common.models.TransactionCategory;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

public interface TransactionCategoriesNestedMVP {

    interface Model {

        Flowable<List<TransactionCategory>> getTransactionCategories(boolean isExpense);

        Flowable<TransactionCategory> updateTransactionCategory(TransactionCategory transactionCategory, boolean isExpense);

        Flowable<Boolean> deleteTransactionCategory(int id, boolean isExpense);
    }

    interface View {

        void setTransactionCategoryList(List<TransactionCategory> transactionCategoryList, TypedArray iconsArray);

        void showMessage(String message);

        void shakeDialogUpdateTransactionCategoryField();

        void dismissDialogUpdateTransactionCategory();

        void handleTransactionCategoryUpdated();

        void deleteTransactionCategory();
    }

    interface Presenter {

        void setView(@Nullable TransactionCategoriesNestedMVP.View view);

        void loadData();

        void loadTransactionCategories();

        void setArguments(Bundle args);

        void updateTransactionCategory(int id, String name);

        void deleteTransactionCategoryById(int id);

        String getCategoryNameById(int id);
    }
}