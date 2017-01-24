package com.androidcollider.easyfin.common.managers.accounts_info;

import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class AccountsInfoModule {

    @Provides
    AccountsInfoManager provideAccountsInfoManager(Repository repository) {
        return new AccountsInfoManager(repository);
    }
}
