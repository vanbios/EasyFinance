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
import com.androidcollider.easyfin.fragments.FrgPref;
import com.androidcollider.easyfin.fragments.FrgTransactions;
import com.androidcollider.easyfin.managers.accounts_info.AccountsInfoModule;
import com.androidcollider.easyfin.managers.api.ApiModule;
import com.androidcollider.easyfin.managers.chart.ChartDataModule;
import com.androidcollider.easyfin.managers.connection.ConnectionModule;
import com.androidcollider.easyfin.managers.format.DoubleFormatManager;
import com.androidcollider.easyfin.managers.format.DoubleFormatModule;
import com.androidcollider.easyfin.managers.import_export_db.ImportExportDbModule;
import com.androidcollider.easyfin.managers.rates.exchange.ExchangeModule;
import com.androidcollider.easyfin.managers.rates.rates_info.RatesInfoModule;
import com.androidcollider.easyfin.managers.rates.rates_loader.RatesLoaderModule;
import com.androidcollider.easyfin.managers.shared_pref.SharedPrefModule;
import com.androidcollider.easyfin.managers.ui.hide_touch_outside.HideTouchOutsideModule;
import com.androidcollider.easyfin.managers.ui.shake_edit_text.ShakeEditTextModule;
import com.androidcollider.easyfin.managers.ui.toast.ToastModule;
import com.androidcollider.easyfin.repository.RepositoryModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Ihor Bilous
 */

@Singleton
@Component(modules = {
        AppModule.class,
        ApiModule.class,
        RepositoryModule.class,
        ExchangeModule.class,
        RatesInfoModule.class,
        RatesLoaderModule.class,
        ConnectionModule.class,
        AccountsInfoModule.class,
        ImportExportDbModule.class,
        SharedPrefModule.class,
        ShakeEditTextModule.class,
        ToastModule.class,
        HideTouchOutsideModule.class,
        DoubleFormatModule.class,
        ChartDataModule.class
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

    void  inject(FrgPref frgPref);
}
