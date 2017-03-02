package com.androidcollider.easyfin.transactions.list;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.models.TransactionCategory;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

interface TransactionsMVP {

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