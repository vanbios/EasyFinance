package com.androidcollider.easyfin.accounts.add_edit

import android.content.Context
import com.androidcollider.easyfin.common.managers.accounts.accounts_info.AccountsInfoManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class AddAccountModule {
    @Provides
    fun provideAddAccountMVPModel(
        repository: Repository,
        accountsInfoManager: AccountsInfoManager,
        numberFormatManager: NumberFormatManager,
        resourcesManager: ResourcesManager
    ): AddAccountMVP.Model {
        return AddAccountModel(
            repository,
            accountsInfoManager,
            numberFormatManager,
            resourcesManager
        )
    }

    @Provides
    fun provideAddAccountMVPPresenter(
        model: AddAccountMVP.Model,
        context: Context
    ): AddAccountMVP.Presenter {
        return AddAccountPresenter(model, context)
    }
}