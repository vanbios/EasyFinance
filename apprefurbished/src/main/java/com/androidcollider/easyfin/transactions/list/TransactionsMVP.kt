package com.androidcollider.easyfin.transactions.list;

import android.util.Pair;

import androidx.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.models.TransactionCategory;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

public interface TransactionsMVP {

    interface Model {

        Flowable<Pair<List<TransactionViewModel>,
                Pair<List<TransactionCategory>, List<TransactionCategory>>>> getTransactionAndTransactionCategoriesLists();

        Flowable<Transaction> getTransactionById(int id);

        Flowable<Boolean> deleteTransactionById(int id);
    }

    interface View {

        void setTransactionAndTransactionCategoriesLists(List<TransactionViewModel> transactionList,
                                                         List<TransactionCategory> transactionCategoryIncomeList,
                                                         List<TransactionCategory> transactionCategoryExpenseList);

        void goToEditTransaction(Transaction transaction);

        void deleteTransaction();
    }

    interface Presenter {

        void setView(@Nullable TransactionsMVP.View view);

        void loadData();

        void getTransactionById(int id);

        void deleteTransactionById(int id);
    }
}