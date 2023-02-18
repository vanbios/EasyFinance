package com.androidcollider.easyfin.transactions.list;

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
public class TransactionsModule {

    @Provides
    TransactionsMVP.Model provideTransactionsMVPModel(Repository repository,
                                                      DateFormatManager dateFormatManager,
                                                      NumberFormatManager numberFormatManager,
                                                      ResourcesManager resourcesManager,
                                                      Context context) {
        return new TransactionsModel(repository, dateFormatManager, numberFormatManager, resourcesManager, context);
    }

    @Provides
    TransactionsMVP.Presenter provideTransactionsMVPPresenter(TransactionsMVP.Model model) {
        return new TransactionsPresenter(model);
    }
}