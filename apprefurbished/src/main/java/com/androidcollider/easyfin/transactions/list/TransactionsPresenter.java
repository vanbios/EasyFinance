package com.androidcollider.easyfin.transactions.list;

import android.support.annotation.Nullable;

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
                .subscribe(
                        transactionList -> {
                            if (view != null) {
                                view.setTransactionList(transactionList);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    @Override
    public void getTransactionById(int id) {
        model.getTransactionById(id)
                .subscribe(
                        transaction -> {
                            if (view != null) {
                                view.goToEditTransaction(transaction);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    @Override
    public void deleteTransactionById(int id) {
        model.deleteTransactionById(id)
                .subscribe(
                        aBoolean -> {
                            if (aBoolean && view != null) {
                                view.deleteTransaction();
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}