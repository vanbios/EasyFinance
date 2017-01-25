package com.androidcollider.easyfin.transactions.list;

import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Transaction;

import java.util.List;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

interface TransactionsMVP {

    interface Model {

        Observable<List<TransactionViewModel>> getTransactionList();

        Observable<Transaction> getTransactionById(int id);

        Observable<Boolean> deleteTransactionById(int id);
    }

    interface View {

        void setTransactionList(List<TransactionViewModel> transactionList);

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