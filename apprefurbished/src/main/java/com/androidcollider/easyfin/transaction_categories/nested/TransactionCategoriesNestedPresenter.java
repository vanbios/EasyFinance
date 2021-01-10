package com.androidcollider.easyfin.transaction_categories.nested;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ihor Bilous
 */

class TransactionCategoriesNestedPresenter implements TransactionCategoriesNestedMVP.Presenter {

    @Nullable
    private TransactionCategoriesNestedMVP.View view;
    private TransactionCategoriesNestedMVP.Model model;
    private Context context;
    private ResourcesManager resourcesManager;

    private List<TransactionCategory> transactionCategoryList;
    private TypedArray iconsArray;

    private boolean isExpense;


    TransactionCategoriesNestedPresenter(Context context,
                                         TransactionCategoriesNestedMVP.Model model,
                                         ResourcesManager resourcesManager) {
        this.context = context;
        this.model = model;
        this.resourcesManager = resourcesManager;
        transactionCategoryList = new ArrayList<>();
    }

    @Override
    public void setView(@Nullable TransactionCategoriesNestedMVP.View view) {
        this.view = view;
    }

    @Override
    public void setArguments(Bundle args) {
        int type = args.getInt(TransactionCategoriesNestedFragment.TYPE);
        isExpense = type == TransactionCategoriesNestedFragment.TYPE_EXPENSE;
    }

    @Override
    public void loadData() {
        iconsArray = resourcesManager.getIconArray(
                isExpense ?
                        ResourcesManager.ICON_TRANSACTION_CATEGORY_EXPENSE :
                        ResourcesManager.ICON_TRANSACTION_CATEGORY_INCOME
        );

        loadTransactionCategories();
    }

    @Override
    public void loadTransactionCategories() {
        model.getTransactionCategories(isExpense)
                .subscribe(transactionCategories -> {
                            List<TransactionCategory> actualTransactionCategoryList =
                                    getActualTransactionCategoryList(transactionCategories);
                            if (view != null) {
                                view.setTransactionCategoryList(actualTransactionCategoryList, iconsArray);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    @Override
    public void updateTransactionCategory(int id, String name) {
        if (view != null) {
            if (name.isEmpty()) {
                handleUpdatedTransactionCategoryNameIsNotValid(context.getString(R.string.empty_name_field));
                return;
            }
            if (!isNewTransactionCategoryNameUnique(name, id)) {
                handleUpdatedTransactionCategoryNameIsNotValid(context.getString(R.string.category_name_exist));
                return;
            }

            TransactionCategory category = new TransactionCategory(id, name, 1);

            model.updateTransactionCategory(category, isExpense)
                    .subscribe(transactionCategory -> {
                                if (view != null) {
                                    view.handleTransactionCategoryUpdated();
                                    view.dismissDialogUpdateTransactionCategory();
                                }
                            },
                            Throwable::printStackTrace
                    );
        }
    }

    @Override
    public void deleteTransactionCategoryById(int id) {
        model.deleteTransactionCategory(id, isExpense)
                .subscribe(aBoolean -> {
                            deleteTransactionCategoryFromListById(id);
                            if (view != null) {
                                view.deleteTransactionCategory();
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private void handleUpdatedTransactionCategoryNameIsNotValid(String message) {
        if (view != null) {
            view.showMessage(message);
            view.shakeDialogUpdateTransactionCategoryField();
        }
    }

    private boolean isNewTransactionCategoryNameUnique(String name, int id) {
        if (name.equalsIgnoreCase(getCategoryNameById(id))) return true;
        for (TransactionCategory category : transactionCategoryList) {
            if (name.equalsIgnoreCase(category.getName())) return false;
        }
        return true;
    }

    @Override
    public String getCategoryNameById(int id) {
        for (TransactionCategory transactionCategory : transactionCategoryList) {
            if (transactionCategory.getId() == id) return transactionCategory.getName();
        }
        return "";
    }

    private void deleteTransactionCategoryFromListById(int id) {
        int pos = -1;
        for (int i = 0; i < transactionCategoryList.size(); i++) {
            if (id == transactionCategoryList.get(i).getId()) {
                pos = i;
                break;
            }
        }
        if (pos >= 0) transactionCategoryList.remove(pos);
    }

    private List<TransactionCategory> getActualTransactionCategoryList(List<TransactionCategory> categoryList) {
        transactionCategoryList.clear();
        transactionCategoryList.addAll(categoryList);

        return Stream.of(transactionCategoryList)
                .filter(t -> t.getVisibility() == 1)
                .collect(Collectors.toList());
    }
}