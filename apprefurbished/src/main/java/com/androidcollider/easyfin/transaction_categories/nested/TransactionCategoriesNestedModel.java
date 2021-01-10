package com.androidcollider.easyfin.transaction_categories.nested;

import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.androidcollider.easyfin.common.repository.Repository;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

class TransactionCategoriesNestedModel implements TransactionCategoriesNestedMVP.Model {

    private Repository repository;


    TransactionCategoriesNestedModel(Repository repository) {
        this.repository = repository;
    }


    @Override
    public Flowable<List<TransactionCategory>> getTransactionCategories(boolean isExpense) {
        return isExpense ?
                repository.getAllTransactionExpenseCategories() :
                repository.getAllTransactionIncomeCategories();
    }

    @Override
    public Flowable<TransactionCategory> updateTransactionCategory(TransactionCategory transactionCategory, boolean isExpense) {
        return isExpense ?
                repository.updateTransactionExpenseCategory(transactionCategory) :
                repository.updateTransactionIncomeCategory(transactionCategory);
    }

    @Override
    public Flowable<Boolean> deleteTransactionCategory(int id, boolean isExpense) {
        return isExpense ?
                repository.deleteTransactionExpenseCategory(id) :
                repository.deleteTransactionIncomeCategory(id);
    }
}