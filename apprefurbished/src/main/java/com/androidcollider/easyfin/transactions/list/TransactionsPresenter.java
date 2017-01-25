package com.androidcollider.easyfin.transactions.list;

import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Transaction;

import java.util.List;

import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

class TransactionsPresenter implements TransactionsMVP.Presenter {

    @Nullable
    private TransactionsMVP.View view;
    private TransactionsMVP.Model model;


    TransactionsPresenter(TransactionsMVP.Model model) {
        this.model = model;
    }

    @Override
    public void setView(@Nullable TransactionsMVP.View view) {
        this.view = view;
    }

    @Override
    public void loadData() {
        model.getTransactionList()
                .subscribe(new Subscriber<List<TransactionViewModel>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<TransactionViewModel> transactionList) {
                        if (view != null) {
                            view.setTransactionList(transactionList);
                        }
                    }
                });
    }

    @Override
    public void getTransactionById(int id) {
        model.getTransactionById(id)
                .subscribe(new Subscriber<Transaction>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Transaction transaction) {
                        if (view != null) {
                            view.goToEditTransaction(transaction);
                        }
                    }
                });
    }

    @Override
    public void deleteTransactionById(int id) {
        model.deleteTransactionById(id)
                .subscribe(new Subscriber<Boolean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean && view != null) {
                            view.deleteTransaction();
                        }
                    }
                });
    }
}