package com.androidcollider.easyfin.accounts.add_edit;

import android.content.Context;

import com.androidcollider.easyfin.common.managers.accounts_info.AccountsInfoManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class AddAccountModule {

    @Provides
    AddAccountMVP.Model provideAddAccountMVPModel(Repository repository,
                                                  AccountsInfoManager accountsInfoManager,
                                                  NumberFormatManager numberFormatManager,
                                                  ResourcesManager resourcesManager) {
        return new AddAccountModel(repository, accountsInfoManager, numberFormatManager, resourcesManager);
    }

    @Provides
    AddAccountMVP.Presenter provideAddAccountMVPPresenter(AddAccountMVP.Model model,
                                                          Context context) {
        return new AddAccountPresenter(model, context);
    }
}