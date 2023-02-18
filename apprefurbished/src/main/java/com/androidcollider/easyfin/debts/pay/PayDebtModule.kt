package com.androidcollider.easyfin.debts.pay

import android.content.Context
import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class PayDebtModule {
    @Provides
    fun providePayDebtMVPModel(
        repository: Repository,
        numberFormatManager: NumberFormatManager,
        accountsToSpinViewModelManager: AccountsToSpinViewModelManager
    ): PayDebtMVP.Model {
        return PayDebtModel(
            repository,
            numberFormatManager,
            accountsToSpinViewModelManager
        )
    }

    @Provides
    fun providePayDebtMVPPresenter(
        context: Context?,
        model: PayDebtMVP.Model?
    ): PayDebtMVP.Presenter {
        return PayDebtPresenter(context, model)
    }
}