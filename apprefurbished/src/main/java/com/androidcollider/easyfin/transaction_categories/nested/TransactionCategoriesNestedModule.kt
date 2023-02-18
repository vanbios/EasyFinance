package com.androidcollider.easyfin.transaction_categories.nested

import android.content.Context
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class TransactionCategoriesNestedModule {
    @Provides
    fun provideTransactionCategoriesNestedMVPModel(repository: Repository?): TransactionCategoriesNestedMVP.Model {
        return TransactionCategoriesNestedModel(repository)
    }

    @Provides
    fun provideTransactionCategoriesNestedMVPPresenter(
        context: Context?,
        model: TransactionCategoriesNestedMVP.Model?,
        resourcesManager: ResourcesManager?
    ): TransactionCategoriesNestedMVP.Presenter {
        return TransactionCategoriesNestedPresenter(context, model, resourcesManager)
    }
}