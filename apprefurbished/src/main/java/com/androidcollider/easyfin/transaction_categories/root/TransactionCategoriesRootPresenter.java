package com.androidcollider.easyfin.transaction_categories.root;

import android.content.Context;
import android.support.annotation.Nullable;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ihor Bilous
 */

class TransactionCategoriesRootPresenter implements TransactionCategoriesRootMVP.Presenter {

    @Nullable
    private TransactionCategoriesRootMVP.View view;
    private TransactionCategoriesRootMVP.Model model;
    private Context context;

    private List<TransactionCategory> transactionCategoryIncomeList, transactionCategoryIncomeNotFilteredList;
    private List<TransactionCategory> transactionCategoryExpenseList, transactionCategoryExpenseNotFilteredList;


    TransactionCategoriesRootPresenter(Context context,
                                       TransactionCategoriesRootMVP.Model model) {
        this.context = context;
        this.model = model;
        transactionCategoryIncomeList = new ArrayList<>();
        transactionCategoryExpenseList = new ArrayList<>();
        transactionCategoryIncomeNotFilteredList = new ArrayList<>();
        transactionCategoryExpenseNotFilteredList = new ArrayList<>();
    }

    @Override
    public void setView(@Nullable TransactionCategoriesRootMVP.View view) {
        this.view = view;
    }

    @Override
    public void loadData() {
        model.getAllTransactionCategories()
                .subscribe(pair -> {
                            saveActualTransactionCategoryList(pair.first, false);
                            saveActualTransactionCategoryList(pair.second, true);
                        },
                        Throwable::printStackTrace
                );
    }

    @Override
    public void addNewCategory(String name, boolean isExpense) {
        if (view != null) {
            if (name.isEmpty()) {
                handleNewTransactionCategoryNameIsNotValid(context.getString(R.string.empty_name_field));
                return;
            }
            if (!isNewTransactionCategoryNameUnique(name, isExpense)) {
                handleNewTransactionCategoryNameIsNotValid(context.getString(R.string.category_name_exist));
                return;
            }

            int id = getIdForNewTransactionCategory(isExpense);
            TransactionCategory category = new TransactionCategory(id, name);

            model.addNewTransactionCategory(category, isExpense)
                    .subscribe(transactionCategory -> {
                                if (view != null) {
                                    addNewTransactionCategoryInLists(transactionCategory, isExpense);
                                    view.handleNewTransactionCategoryAdded();
                                    view.dismissDialogNewTransactionCategory();
                                }
                            },
                            Throwable::printStackTrace
                    );
        }
    }

    private void addNewTransactionCategoryInLists(TransactionCategory transactionCategory, boolean isExpense) {
        if (isExpense) {
            transactionCategoryExpenseList.add(transactionCategory);
            transactionCategoryExpenseNotFilteredList.add(transactionCategory);
        } else {
            transactionCategoryIncomeList.add(transactionCategory);
            transactionCategoryIncomeNotFilteredList.add(transactionCategory);
        }
    }

    private void handleNewTransactionCategoryNameIsNotValid(String message) {
        if (view != null) {
            view.showMessage(message);
            view.shakeDialogNewTransactionCategoryField();
        }
    }

    private boolean isNewTransactionCategoryNameUnique(String name, boolean isExpense) {
        for (TransactionCategory category : isExpense ?
                transactionCategoryExpenseList :
                transactionCategoryIncomeList) {
            if (name.equalsIgnoreCase(category.getName())) return false;
        }
        return true;
    }

    private int getIdForNewTransactionCategory(boolean isExpense) {
        List<TransactionCategory> list = isExpense ? transactionCategoryExpenseNotFilteredList : transactionCategoryIncomeNotFilteredList;
        return list.isEmpty() ? 0 : list.get(list.size() - 1).getId() + 1;
    }

    private void saveActualTransactionCategoryList(List<TransactionCategory> categoryList, boolean isExpense) {
        List<TransactionCategory> list = isExpense ? transactionCategoryExpenseList : transactionCategoryIncomeList;
        List<TransactionCategory> notFilteredList = isExpense ? transactionCategoryExpenseNotFilteredList : transactionCategoryIncomeNotFilteredList;
        notFilteredList.clear();
        notFilteredList.addAll(categoryList);
        list.clear();
        list.addAll(getFilteredList(categoryList));
    }

    private List<TransactionCategory> getFilteredList(List<TransactionCategory> list) {
        return Stream.of(list)
                .filter(t -> t.getVisibility() == 1)
                .collect(Collectors.toList());
    }
}