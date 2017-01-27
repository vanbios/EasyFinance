package com.androidcollider.easyfin.transactions.add_edit.income_expense;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.models.Transaction;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

class AddTransactionIncomeExpensePresenter implements AddTransactionIncomeExpenseMVP.Presenter {

    @Nullable
    private AddTransactionIncomeExpenseMVP.View view;
    private AddTransactionIncomeExpenseMVP.Model model;
    private Context context;
    private ResourcesManager resourcesManager;

    private int mode, transType;
    private Transaction transFromIntent;


    AddTransactionIncomeExpensePresenter(Context context,
                                         AddTransactionIncomeExpenseMVP.Model model,
                                         ResourcesManager resourcesManager) {
        this.context = context;
        this.model = model;
        this.resourcesManager = resourcesManager;
    }

    @Override
    public void setView(@Nullable AddTransactionIncomeExpenseMVP.View view) {
        this.view = view;
    }

    @Override
    public void setArguments(Bundle args) {
        mode = args.getInt("mode", 0);

        switch (mode) {
            case 0: {
                transType = args.getInt("type", 0);
                break;
            }
            case 1: {
                transFromIntent = (Transaction) args.getSerializable("transaction");
                if (transFromIntent != null) {
                    double amount = transFromIntent.getAmount();
                    transType = model.isDoubleNegative(amount) ? 0 : 1;
                }
                break;
            }
        }
    }

    @Override
    public void loadAccounts() {
        model.getAllAccounts()
                .subscribe(new Subscriber<List<Account>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Account> accountList) {
                        if (view != null) {
                            if (accountList.isEmpty()) {
                                view.notifyNotEnoughAccounts();
                            } else {
                                view.setAccounts(accountList);

                                switch (mode) {
                                    case 0: {
                                        if (view != null) {
                                            view.showAmount("0,00", transType);
                                            view.openNumericDialog();
                                        }
                                        break;
                                    }
                                    case 1: {
                                        if (transFromIntent != null) {
                                            double amount = transFromIntent.getAmount();
                                            transType = model.isDoubleNegative(amount) ? 0 : 1;
                                            if (view != null) {
                                                view.showAmount(model.getTransactionForEditAmount(transType, amount), transType);
                                            }
                                        }
                                        break;
                                    }
                                }

                                if (view != null) {
                                    view.setAmountTextColor(ContextCompat.getColor(context, transType == 1 ? R.color.custom_green : R.color.custom_red));
                                }

                                String[] categoryArray = resourcesManager.getStringArray(
                                        transType == 1 ?
                                                ResourcesManager.STRING_TRANSACTION_CATEGORY_INCOME :
                                                ResourcesManager.STRING_TRANSACTION_CATEGORY_EXPENSE
                                );
                                TypedArray categoryIcons = resourcesManager.getIconArray(
                                        transType == 1 ?
                                                ResourcesManager.ICON_TRANSACTION_CATEGORY_INCOME :
                                                ResourcesManager.ICON_TRANSACTION_CATEGORY_EXPENSE);

                                view.setupSpinners(categoryArray, categoryIcons);

                                if (mode == 1) {
                                    String accountName = transFromIntent.getAccountName();

                                    for (int i = 0; i < accountList.size(); i++) {
                                        if (accountList.get(i).getName().equals(accountName)) {
                                            view.showAccount(i);
                                            break;
                                        }
                                    }

                                    view.showCategory(transFromIntent.getCategory());
                                }

                                Calendar calendar = Calendar.getInstance();
                                if (mode == 1) {
                                    calendar.setTime(new Date(transFromIntent.getDate()));
                                }
                                view.setupDateTimeField(calendar);
                            }
                        }
                    }
                });
    }

    @Override
    public void save() {
        switch (mode) {
            case 0:
                addTransaction();
                break;
            case 1:
                editTransaction();
                break;
        }
    }

    @Override
    public int getTransactionType() {
        return transType;
    }

    private void addTransaction() {
        if (view != null) {
            String sum = model.prepareStringToParse(view.getAmount());
            if (checkSumField(sum)) {
                double amount = Double.parseDouble(sum);
                boolean isExpense = transType == 0;
                if (isExpense) amount *= -1;

                Account account = view.getAccount();

                double accountAmount = account.getAmount();

                if (checkIsEnoughCosts(isExpense, amount, accountAmount)) {
                    accountAmount += amount;

                    Transaction transaction = Transaction.builder()
                            .date(model.getMillisFromString(view.getDate()))
                            .amount(amount)
                            .category(view.getCategory())
                            .idAccount(account.getId())
                            .accountAmount(accountAmount)
                            .accountName(account.getName())
                            .accountType(account.getType())
                            .currency(account.getCurrency())
                            .build();

                    model.addNewTransaction(transaction)
                            .subscribe(new Subscriber<Transaction>() {

                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Transaction transaction) {
                                    if (transaction != null && view != null) {
                                        view.performLastActionsAfterSaveAndClose();
                                    }
                                }
                            });
                }
            }
        }
    }

    private void editTransaction() {
        if (view != null) {
            String sum = model.prepareStringToParse(view.getAmount());
            if (checkSumField(sum)) {
                double amount = Double.parseDouble(sum);
                boolean isExpense = transType == 0;
                if (isExpense) amount *= -1;

                Account account = view.getAccount();
                double accountAmount = account.getAmount();
                int accountId = account.getId();

                int oldAccountId = transFromIntent.getIdAccount();
                boolean isAccountTheSame = accountId == oldAccountId;
                double oldAmount = transFromIntent.getAmount();
                double oldAccountAmount = 0;

                if (isAccountTheSame) accountAmount -= oldAmount;
                else {
                    for (Account account1 : view.getAccounts()) {
                        if (oldAccountId == account1.getId()) {
                            oldAccountAmount = account1.getAmount() - oldAmount;
                            break;
                        }
                    }
                }

                if (checkIsEnoughCosts(isExpense, amount, accountAmount)) {
                    accountAmount += amount;

                    Transaction transaction = Transaction.builder()
                            .date(model.getMillisFromString(view.getDate()))
                            .amount(amount)
                            .category(view.getCategory())
                            .idAccount(account.getId())
                            .accountAmount(accountAmount)
                            .id(transFromIntent.getId())
                            .currency(account.getCurrency())
                            .accountType(account.getType())
                            .accountName(account.getName())
                            .build();

                    if (isAccountTheSame) {
                        model.updateTransaction(transaction)
                                .subscribe(new Subscriber<Transaction>() {

                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Transaction transaction) {
                                        if (transaction != null && view != null) {
                                            view.performLastActionsAfterSaveAndClose();
                                        }
                                    }
                                });
                    } else {
                        model.updateTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId)
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
                                            view.performLastActionsAfterSaveAndClose();
                                        }
                                    }
                                });
                    }
                }
            }
        }
    }

    private boolean checkSumField(String sum) {
        if (!sum.matches(".*\\d.*") || Double.parseDouble(sum) == 0) {
            if (view != null) {
                view.showMessage(context.getString(R.string.empty_amount_field));
            }
            return false;
        }
        return true;
    }

    private boolean checkIsEnoughCosts(boolean isExpense, double amount, double accountAmount) {
        if (isExpense && Math.abs(amount) > accountAmount) {
            if (view != null) {
                view.showMessage(context.getString(R.string.not_enough_costs));
            }
            return false;
        }
        return true;
    }
}