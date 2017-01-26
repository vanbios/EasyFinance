package com.androidcollider.easyfin.main;

import com.androidcollider.easyfin.common.managers.accounts_info.AccountsInfoManager;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class MainModule {

    @Provides
    MainMVP.Model provideMainMVPModel(AccountsInfoManager accountsInfoManager) {
        return new MainModel(accountsInfoManager);
    }

    @Provides
    MainMVP.Presenter provideMainMVPPresenter(MainMVP.Model model) {
        return new MainPresenter(model);
    }
}