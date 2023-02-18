package com.androidcollider.easyfin.main

import com.androidcollider.easyfin.common.managers.accounts.accounts_info.AccountsInfoManager
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class MainModule {
    @Provides
    fun provideMainMVPModel(accountsInfoManager: AccountsInfoManager?): MainMVP.Model {
        return MainModel(accountsInfoManager)
    }

    @Provides
    fun provideMainMVPPresenter(model: MainMVP.Model?): MainMVP.Presenter {
        return MainPresenter(model)
    }
}