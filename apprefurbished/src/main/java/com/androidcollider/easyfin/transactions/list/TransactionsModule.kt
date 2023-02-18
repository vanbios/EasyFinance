package com.androidcollider.easyfin.transactions.list

import android.content.Context
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
class TransactionsModule {
    @Provides
    fun provideTransactionsMVPModel(
        repository: Repository?,
        dateFormatManager: DateFormatManager?,
        numberFormatManager: NumberFormatManager?,
        resourcesManager: ResourcesManager?,
        context: Context?
    ): TransactionsMVP.Model {
        return TransactionsModel(
            repository,
            dateFormatManager,
            numberFormatManager,
            resourcesManager,
            context
        )
    }

    @Provides
    fun provideTransactionsMVPPresenter(model: TransactionsMVP.Model?): TransactionsMVP.Presenter {
        return TransactionsPresenter(model)
    }
}