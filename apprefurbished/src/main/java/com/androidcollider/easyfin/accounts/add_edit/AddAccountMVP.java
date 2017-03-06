package com.androidcollider.easyfin.accounts.add_edit;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.models.Account;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

interface AddAccountMVP {

    interface Model {

        Flowable<Account> addAccount(String name, String amount, int type, String currency);

        Flowable<Account> updateAccount(String name, String amount, int type, String currency);

        void setAccountForUpdate(Account account);

        String getAccountForUpdateName();

        String getAccountForUpdateAmount();

        int getAccountForUpdateType();

        int getAccountForUpdateCurrencyPosition();

        boolean validateAccountName(String name);
    }

    interface View {

        void showAmount(String amount);

        void showName(String name);

        void showType(int type);

        void showCurrency(int position);

        void highlightNameField();

        void showMessage(String message);

        void openNumericDialog();

        void performLastActionsAfterSaveAndClose();

        String getAccountName();

        String getAccountAmount();

        String getAccountCurrency();

        int getAccountType();
    }

    interface Presenter {

        void setView(@Nullable AddAccountMVP.View view);

        void setArguments(Bundle args);

        void save();
    }
}