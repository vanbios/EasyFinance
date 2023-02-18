package com.androidcollider.easyfin.transaction_categories.root;

import android.content.Context;

import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class TransactionCategoriesRootModule {

    @Provides
    TransactionCategoriesRootMVP.Model provideTransactionCategoriesRootMVPModel(Repository repository) {
        return new TransactionCategoriesRootModel(repository);
    }

    @Provides
    TransactionCategoriesRootMVP.Presenter provideTransactionCategoriesRootMVPPresenter(Context context,
                                                                                         TransactionCategoriesRootMVP.Model model) {
        return new TransactionCategoriesRootPresenter(context, model);
    }
}