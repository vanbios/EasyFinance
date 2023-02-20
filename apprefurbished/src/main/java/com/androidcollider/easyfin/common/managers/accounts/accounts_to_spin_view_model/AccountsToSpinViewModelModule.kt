package com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class AccountsToSpinViewModelModule {
    @Provides
    fun provideAccountsToSpinViewModelManager(
        numberFormatManager: NumberFormatManager,
        resourcesManager: ResourcesManager
    ): AccountsToSpinViewModelManager {
        return AccountsToSpinViewModelManager(numberFormatManager, resourcesManager)
    }
}