package com.androidcollider.easyfin.common.app;

import com.androidcollider.easyfin.accounts.list.AccountsFragment;
import com.androidcollider.easyfin.accounts.list.AccountsModule;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.accounts.add_edit.FrgAddAccount;
import com.androidcollider.easyfin.debts.add_edit.FrgAddDebt;
import com.androidcollider.easyfin.transactions.add_edit.btw_accounts.FrgAddTransactionBetweenAccounts;
import com.androidcollider.easyfin.transactions.add_edit.income_expense.FrgAddTransactionDefault;
import com.androidcollider.easyfin.debts.list.FrgDebts;
import com.androidcollider.easyfin.home.FrgHome;
import com.androidcollider.easyfin.common.ui.fragments.FrgMain;
import com.androidcollider.easyfin.common.ui.fragments.FrgNumericDialog;
import com.androidcollider.easyfin.debts.pay.FrgPayDebt;
import com.androidcollider.easyfin.common.ui.fragments.FrgPref;
import com.androidcollider.easyfin.transactions.list.FrgTransactions;
import com.androidcollider.easyfin.common.managers.accounts_info.AccountsInfoModule;
import com.androidcollider.easyfin.common.managers.api.ApiModule;
import com.androidcollider.easyfin.common.managers.chart.data.ChartDataModule;
import com.androidcollider.easyfin.common.managers.chart.setup.ChartSetupModule;
import com.androidcollider.easyfin.common.managers.connection.ConnectionModule;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatModule;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatModule;
import com.androidcollider.easyfin.common.managers.import_export_db.ImportExportDbModule;
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeModule;
import com.androidcollider.easyfin.common.managers.rates.rates_info.RatesInfoModule;
import com.androidcollider.easyfin.common.managers.rates.rates_loader.RatesLoaderModule;
import com.androidcollider.easyfin.common.managers.resources.ResourcesModule;
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefModule;
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideModule;
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextModule;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastModule;
import com.androidcollider.easyfin.common.repository.RepositoryModule;

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
        NumberFormatModule.class,
        DateFormatModule.class,
        ChartDataModule.class,
        ResourcesModule.class,
        ChartSetupModule.class,
        AccountsModule.class
})
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(FrgMain frgMain);

    void inject(FrgHome frgHome);

    void inject(AccountsFragment accountsFragment);

    void inject(FrgTransactions frgTransactions);

    void inject(FrgDebts frgDebts);

    void inject(FrgAddAccount frgAddAccount);

    void inject(FrgAddTransactionDefault frgAddTransactionDefault);

    void inject(FrgAddTransactionBetweenAccounts frgAddTransactionBetweenAccounts);

    void inject(FrgAddDebt frgAddDebt);

    void inject(FrgPayDebt frgPayDebt);

    void inject(FrgPref frgPref);

    void inject(FrgNumericDialog frgNumericDialog);
}
