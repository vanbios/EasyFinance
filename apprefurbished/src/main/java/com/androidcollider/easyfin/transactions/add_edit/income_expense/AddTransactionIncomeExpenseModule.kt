package com.androidcollider.easyfin.transactions.add_edit.income_expense

import android.content.Context
import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class AddTransactionIncomeExpenseModule {
    @Provides
    fun provideAddTransactionIncomeExpenseMVPModel(
        repository: Repository?,
        numberFormatManager: NumberFormatManager?,
        dateFormatManager: DateFormatManager?,
        accountsToSpinViewModelManager: AccountsToSpinViewModelManager?
    ): AddTransactionIncomeExpenseMVP.Model {
        return AddTransactionIncomeExpenseModel(
            repository,
            numberFormatManager,
            dateFormatManager,
            accountsToSpinViewModelManager
        )
    }

    @Provides
    fun provideAddTransactionIncomeExpenseMVPPresenter(
        context: Context?,
        model: AddTransactionIncomeExpenseMVP.Model?,
        resourcesManager: ResourcesManager?
    ): AddTransactionIncomeExpenseMVP.Presenter {
        return AddTransactionIncomeExpensePresenter(context, model, resourcesManager)
    }
}