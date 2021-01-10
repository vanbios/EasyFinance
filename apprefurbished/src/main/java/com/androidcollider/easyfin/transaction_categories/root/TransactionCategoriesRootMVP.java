package com.androidcollider.easyfin.transaction_categories.root;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.androidcollider.easyfin.common.models.TransactionCategory;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

public interface TransactionCategoriesRootMVP {

    interface Model {

        Flowable<Pair<List<TransactionCategory>, List<TransactionCategory>>> getAllTransactionCategories();

        Flowable<TransactionCategory> addNewTransactionCategory(TransactionCategory transactionCategory, boolean isExpense);
    }

    interface View {

        void showMessage(String message);

        void shakeDialogNewTransactionCategoryField();

        void dismissDialogNewTransactionCategory();

        void handleNewTransactionCategoryAdded();
    }

    interface Presenter {

        void setView(@Nullable TransactionCategoriesRootMVP.View view);

        void loadData();

        void addNewCategory(String name, boolean isExpense);
    }
}