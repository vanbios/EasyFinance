package com.androidcollider.easyfin.debts.pay;

import android.content.Context;

import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.repository.Repository;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class PayDebtModule {

    @Provides
    PayDebtMVP.Model providePayDebtMVPModel(Repository repository,
                                            NumberFormatManager numberFormatManager) {
        return new PayDebtModel(repository, numberFormatManager);
    }

    @Provides
    PayDebtMVP.Presenter providePayDebtMVPPresenter(Context context,
                                                    PayDebtMVP.Model model) {
        return new PayDebtPresenter(context, model);
    }
}