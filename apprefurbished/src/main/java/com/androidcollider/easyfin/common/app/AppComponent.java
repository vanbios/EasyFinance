package com.androidcollider.easyfin.common.app;

import com.androidcollider.easyfin.common.MainActivity;
import com.androidcollider.easyfin.fragments.FrgAccounts;
import com.androidcollider.easyfin.fragments.FrgAddAccount;
import com.androidcollider.easyfin.fragments.FrgAddDebt;
import com.androidcollider.easyfin.fragments.FrgAddTransactionBetweenAccounts;
import com.androidcollider.easyfin.fragments.FrgAddTransactionDefault;
import com.androidcollider.easyfin.fragments.FrgDebts;
import com.androidcollider.easyfin.fragments.FrgHome;
import com.androidcollider.easyfin.fragments.FrgMain;
import com.androidcollider.easyfin.fragments.FrgPayDebt;
import com.androidcollider.easyfin.fragments.FrgTransactions;
import com.androidcollider.easyfin.repository.RepositoryModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Ihor Bilous
 */

@Singleton
@Component(modules = {
        AppModule.class,
        RepositoryModule.class
})
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(FrgMain frgMain);

    void inject(FrgHome frgHome);

    void inject(FrgAccounts frgAccounts);

    void inject(FrgTransactions frgTransactions);

    void inject(FrgDebts frgDebts);

    void inject(FrgAddAccount frgAddAccount);

    void inject(FrgAddTransactionDefault frgAddTransactionDefault);

    void inject(FrgAddTransactionBetweenAccounts frgAddTransactionBetweenAccounts);

    void inject(FrgAddDebt frgAddDebt);

    void inject(FrgPayDebt frgPayDebt);
}
