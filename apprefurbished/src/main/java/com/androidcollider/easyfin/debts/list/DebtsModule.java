package com.androidcollider.easyfin.debts.list;

import android.content.Context;

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
public class DebtsModule {

    @Provides
    DebtsMVP.Model provideDebtsMVPModel(Repository repository,
                                        DateFormatManager dateFormatManager,
                                        NumberFormatManager numberFormatManager,
                                        ResourcesManager resourcesManager,
                                        Context context) {
        return new DebtsModel(repository, dateFormatManager, numberFormatManager, resourcesManager, context);
    }

    @Provides
    DebtsMVP.Presenter provideDebtsMVPPresenter(DebtsMVP.Model model) {
        return new DebtsPresenter(model);
    }
}