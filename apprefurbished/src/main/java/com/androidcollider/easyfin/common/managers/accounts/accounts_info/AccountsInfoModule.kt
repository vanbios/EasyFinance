package com.androidcollider.easyfin.common.managers.accounts.accounts_info

import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class AccountsInfoModule {
    @Provides
    fun provideAccountsInfoManager(repository: Repository): AccountsInfoManager {
        return AccountsInfoManager(repository)
    }
}