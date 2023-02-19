package com.androidcollider.easyfin.accounts.list

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class AccountsModule {
    @Provides
    fun provideAccountsMVPModel(
        repository: Repository,
        numberFormatManager: NumberFormatManager,
        resourcesManager: ResourcesManager
    ): AccountsMVP.Model {
        return AccountsModel(repository, numberFormatManager, resourcesManager)
    }

    @Provides
    fun provideAccountsMVPPresenter(model: AccountsMVP.Model): AccountsMVP.Presenter {
        return AccountsPresenter(model)
    }
}