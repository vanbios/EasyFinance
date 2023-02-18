package com.androidcollider.easyfin.debts.add_edit;

import android.content.Context;

import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelManager;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class AddDebtModule {

    @Provides
    AddDebtMVP.Model provideAddDebtMVPModel(Repository repository,
                                            NumberFormatManager numberFormatManager,
                                            DateFormatManager dateFormatManager,
                                            AccountsToSpinViewModelManager accountsToSpinViewModelManager) {
        return new AddDebtModel(repository, numberFormatManager, dateFormatManager, accountsToSpinViewModelManager);
    }

    @Provides
    AddDebtMVP.Presenter provideAddDebtMVPPresenter(Context context,
                                                    AddDebtMVP.Model model) {
        return new AddDebtPresenter(context, model);
    }
}