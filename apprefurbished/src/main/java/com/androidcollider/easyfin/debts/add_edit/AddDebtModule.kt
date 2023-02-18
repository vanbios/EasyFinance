package com.androidcollider.easyfin.debts.add_edit

import android.content.Context
import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class AddDebtModule {
    @Provides
    fun provideAddDebtMVPModel(
        repository: Repository?,
        numberFormatManager: NumberFormatManager?,
        dateFormatManager: DateFormatManager?,
        accountsToSpinViewModelManager: AccountsToSpinViewModelManager?
    ): AddDebtMVP.Model {
        return AddDebtModel(
            repository,
            numberFormatManager,
            dateFormatManager,
            accountsToSpinViewModelManager
        )
    }

    @Provides
    fun provideAddDebtMVPPresenter(
        context: Context?,
        model: AddDebtMVP.Model?
    ): AddDebtMVP.Presenter {
        return AddDebtPresenter(context, model)
    }
}