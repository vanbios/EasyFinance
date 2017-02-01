package com.androidcollider.easyfin.transactions.add_edit.income_expense;

import android.content.Context;

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class AddTransactionIncomeExpenseModule {

    @Provides
    AddTransactionIncomeExpenseMVP.Model provideAddTransactionIncomeExpenseMVPModel(Repository repository,
                                                                                    NumberFormatManager numberFormatManager,
                                                                                    DateFormatManager dateFormatManager,
                                                                                    AccountsToSpinViewModelManager accountsToSpinViewModelManager) {
        return new AddTransactionIncomeExpenseModel(repository, numberFormatManager, dateFormatManager, accountsToSpinViewModelManager);
    }

    @Provides
    AddTransactionIncomeExpenseMVP.Presenter provideAddTransactionIncomeExpenseMVPPresenter(Context context,
                                                                                            AddTransactionIncomeExpenseMVP.Model model,
                                                                                            ResourcesManager resourcesManager) {
        return new AddTransactionIncomeExpensePresenter(context, model, resourcesManager);
    }
}