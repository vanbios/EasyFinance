package com.androidcollider.easyfin.accounts.add_edit;

import android.support.annotation.Nullable;

import com.androidcollider.easyfin.common.managers.accounts_info.AccountsInfoManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.repository.Repository;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

class AddAccountModel implements AddAccountMVP.Model {

    private Repository repository;
    private AccountsInfoManager accountsInfoManager;
    private NumberFormatManager numberFormatManager;
    private ResourcesManager resourcesManager;

    @Nullable
    private Account accountForUpdate;


    AddAccountModel(Repository repository,
                    AccountsInfoManager accountsInfoManager,
                    NumberFormatManager numberFormatManager,
                    ResourcesManager resourcesManager) {
        this.repository = repository;
        this.accountsInfoManager = accountsInfoManager;
        this.numberFormatManager = numberFormatManager;
        this.resourcesManager = resourcesManager;
    }

    @Override
    public Observable<Account> addAccount(String name, String amount, int type, String currency) {
        Account account = Account.builder()
                .name(name)
                .amount(Double.parseDouble(numberFormatManager.prepareStringToParse(amount)))
                .type(type)
                .currency(currency)
                .build();

        return repository.addNewAccount(account);
    }

    @Override
    public Observable<Account> updateAccount(String name, String amount, int type, String currency) {
        Account account = Account.builder()
                .id(accountForUpdate != null ? accountForUpdate.getId() : 0)
                .name(name)
                .amount(Double.parseDouble(numberFormatManager.prepareStringToParse(amount)))
                .type(type)
                .currency(currency)
                .build();

        return repository.updateAccount(account);
    }

    @Override
    public void setAccountForUpdate(@Nullable Account account) {
        accountForUpdate = account;
    }

    @Override
    public String getAccountForUpdateName() {
        return accountForUpdate != null ? accountForUpdate.getName() : "";
    }

    @Override
    public String getAccountForUpdateAmount() {
        return accountForUpdate != null ?
                numberFormatManager.doubleToStringFormatterForEdit(
                        accountForUpdate.getAmount(),
                        NumberFormatManager.FORMAT_1,
                        NumberFormatManager.PRECISE_1
                ) :
                "0,00";
    }

    @Override
    public int getAccountForUpdateType() {
        return accountForUpdate != null ? accountForUpdate.getType() : 0;
    }

    @Override
    public int getAccountForUpdateCurrencyPosition() {
        if (accountForUpdate != null) {
            String[] currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);

            for (int i = 0; i < currencyArray.length; i++) {
                if (currencyArray[i].equals(accountForUpdate.getCurrency())) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean validateAccountName(String name) {
        return accountForUpdate == null ?
                accountsInfoManager.checkForAccountNameMatches(name) :
                accountsInfoManager.checkForAccountNameMatches(name) && !name.equals(accountForUpdate.getName());
    }
}