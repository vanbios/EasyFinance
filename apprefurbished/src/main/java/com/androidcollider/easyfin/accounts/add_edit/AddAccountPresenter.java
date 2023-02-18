package com.androidcollider.easyfin.accounts.add_edit;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.accounts.list.AccountsFragment;
import com.androidcollider.easyfin.common.models.Account;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

class AddAccountPresenter implements AddAccountMVP.Presenter {

    @Nullable
    private AddAccountMVP.View view;
    private final AddAccountMVP.Model model;
    private final Context context;

    private int mode;


    AddAccountPresenter(AddAccountMVP.Model model, Context context) {
        this.model = model;
        this.context = context;
    }

    @Override
    public void setView(@Nullable AddAccountMVP.View view) {
        this.view = view;
    }

    @Override
    public void setArguments(Bundle args) {
        mode = args.getInt(AccountsFragment.MODE, 0);
        switch (mode) {
            case AccountsFragment.ADD:
                if (view != null) {
                    view.showAmount("0,00");
                    view.openNumericDialog();
                }
                break;
            case AccountsFragment.EDIT:
                model.setAccountForUpdate((Account) args.getSerializable(AccountsFragment.ACCOUNT));
                if (view != null) {
                    view.showName(model.getAccountForUpdateName());
                    view.showAmount(model.getAccountForUpdateAmount());
                    view.showType(model.getAccountForUpdateType());
                    view.showCurrency(model.getAccountForUpdateCurrencyPosition());
                }
                break;
        }
    }

    @Override
    public void save() {
        if (view != null) {
            String accountName = view.getAccountName();
            if (checkForFillNameField(accountName) && isAccountNameIsValid(accountName)) {
                String name = view.getAccountName();
                String amount = view.getAccountAmount();
                int type = view.getAccountType();
                String currency = view.getAccountCurrency();

                getSaveAccountObservable(name, amount, type, currency)
                        .subscribe(
                                account -> {
                                    if (view != null) {
                                        view.performLastActionsAfterSaveAndClose();
                                    }
                                },
                                Throwable::printStackTrace
                        );
            }
        }
    }

    private boolean checkForFillNameField(String accountName) {
        if (accountName.replaceAll("\\s+", "").isEmpty()) {
            if (view != null) {
                view.highlightNameField();
                view.showMessage(context.getString(R.string.empty_name_field));
            }
            return false;
        }
        return true;
    }

    private boolean isAccountNameIsValid(String accountName) {
        if (model.validateAccountName(accountName)) {
            if (view != null) {
                view.highlightNameField();
                view.showMessage(context.getString(R.string.account_name_exist));
            }
            return false;
        }
        return true;
    }

    private Flowable<Account> getSaveAccountObservable(String name, String amount, int type, String currency) {
        return mode == AccountsFragment.EDIT ?
                model.updateAccount(
                        name,
                        amount,
                        type,
                        currency
                ) :
                model.addAccount(
                        name,
                        amount,
                        type,
                        currency
                );
    }
}