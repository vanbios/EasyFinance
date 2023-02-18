package com.androidcollider.easyfin.accounts.list;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class AccountsModule {

    @Provides
    AccountsMVP.Model provideAccountsMVPModel(Repository repository,
                                              NumberFormatManager numberFormatManager,
                                              ResourcesManager resourcesManager) {
        return new AccountsModel(repository, numberFormatManager, resourcesManager);
    }

    @Provides
    AccountsMVP.Presenter provideAccountsMVPPresenter(AccountsMVP.Model model) {
        return new AccountsPresenter(model);
    }
}