package com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class AccountsToSpinViewModelModule {

    @Provides
    public AccountsToSpinViewModelManager provideAccountsToSpinViewModelManager(NumberFormatManager numberFormatManager,
                                                                                ResourcesManager resourcesManager) {
        return new AccountsToSpinViewModelManager(numberFormatManager, resourcesManager);
    }
}