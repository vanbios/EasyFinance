package com.androidcollider.easyfin.transaction_categories.root

import android.content.Context
import com.androidcollider.easyfin.common.repository.Repository
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class TransactionCategoriesRootModule {
    @Provides
    fun provideTransactionCategoriesRootMVPModel(repository: Repository?): TransactionCategoriesRootMVP.Model {
        return TransactionCategoriesRootModel(repository)
    }

    @Provides
    fun provideTransactionCategoriesRootMVPPresenter(
        context: Context?,
        model: TransactionCategoriesRootMVP.Model?
    ): TransactionCategoriesRootMVP.Presenter {
        return TransactionCategoriesRootPresenter(context, model)
    }
}