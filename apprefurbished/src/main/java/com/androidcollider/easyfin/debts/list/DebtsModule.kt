package com.androidcollider.easyfin.debts.list

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
class DebtsModule {
    @Provides
    fun provideDebtsMVPModel(
        repository: Repository?,
        dateFormatManager: DateFormatManager?,
        numberFormatManager: NumberFormatManager?,
        resourcesManager: ResourcesManager?,
        context: Context?
    ): DebtsMVP.Model {
        return DebtsModel(
            repository,
            dateFormatManager,
            numberFormatManager,
            resourcesManager,
            context
        )
    }

    @Provides
    fun provideDebtsMVPPresenter(model: DebtsMVP.Model?): DebtsMVP.Presenter {
        return DebtsPresenter(model)
    }
}