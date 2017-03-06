package com.androidcollider.easyfin.transaction_categories.root;

import android.support.v4.util.Pair;

import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.androidcollider.easyfin.common.repository.Repository;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

class TransactionCategoriesRootModel implements TransactionCategoriesRootMVP.Model {

    private Repository repository;


    TransactionCategoriesRootModel(Repository repository) {
        this.repository = repository;
    }


    @Override
    public Flowable<Pair<List<TransactionCategory>, List<TransactionCategory>>> getAllTransactionCategories() {
        return Flowable.combineLatest(
                repository.getAllTransactionIncomeCategories(),
                repository.getAllTransactionExpenseCategories(),
                Pair::new
        );
    }

    @Override
    public Flowable<TransactionCategory> addNewTransactionCategory(TransactionCategory transactionCategory, boolean isExpense) {
        return isExpense ?
                        repository.addNewTransactionExpenseCategory(transactionCategory) :
                        repository.addNewTransactionIncomeCategory(transactionCategory);
    }
}