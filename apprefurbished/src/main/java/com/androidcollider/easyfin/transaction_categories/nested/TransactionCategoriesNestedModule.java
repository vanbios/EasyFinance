package com.androidcollider.easyfin.transaction_categories.nested;

import android.content.Context;

import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class TransactionCategoriesNestedModule {

    @Provides
    TransactionCategoriesNestedMVP.Model provideTransactionCategoriesNestedMVPModel(Repository repository) {
        return new TransactionCategoriesNestedModel(repository);
    }

    @Provides
    TransactionCategoriesNestedMVP.Presenter provideTransactionCategoriesNestedMVPPresenter(Context context,
                                                                                            TransactionCategoriesNestedMVP.Model model,
                                                                                            ResourcesManager resourcesManager) {
        return new TransactionCategoriesNestedPresenter(context, model, resourcesManager);
    }
}