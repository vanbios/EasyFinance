package com.androidcollider.easyfin.managers.accounts_info;

import com.androidcollider.easyfin.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class AccountsInfoModule {

    @Provides
    public AccountsInfoManager provideAccountsInfoManager(Repository repository) {
        return new AccountsInfoManager(repository);
    }
}
