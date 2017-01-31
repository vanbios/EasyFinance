package com.androidcollider.easyfin.common.app;

import com.androidcollider.easyfin.accounts.add_edit.AddAccountFragment;
import com.androidcollider.easyfin.accounts.add_edit.AddAccountModule;
import com.androidcollider.easyfin.accounts.list.AccountsFragment;
import com.androidcollider.easyfin.accounts.list.AccountsModule;
import com.androidcollider.easyfin.common.managers.accounts.accounts_info.AccountsInfoModule;
import com.androidcollider.easyfin.common.managers.accounts.accounts_to_spin_view_model.AccountsToSpinViewModelModule;
import com.androidcollider.easyfin.common.managers.analytics.AnalyticsModule;
import com.androidcollider.easyfin.common.managers.api.ApiModule;
import com.androidcollider.easyfin.common.managers.chart.data.ChartDataModule;
import com.androidcollider.easyfin.common.managers.chart.setup.ChartSetupModule;
import com.androidcollider.easyfin.common.managers.connection.ConnectionModule;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatModule;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatModule;
import com.androidcollider.easyfin.common.managers.import_export_db.ImportExportDbModule;
import com.androidcollider.easyfin.common.managers.permission.PermissionModule;
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeModule;
import com.androidcollider.easyfin.common.managers.rates.rates_info.RatesInfoModule;
import com.androidcollider.easyfin.common.managers.rates.rates_loader.RatesLoaderModule;
import com.androidcollider.easyfin.common.managers.resources.ResourcesModule;
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefModule;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogModule;
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideModule;
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextModule;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastModule;
import com.androidcollider.easyfin.common.repository.RepositoryModule;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment;
import com.androidcollider.easyfin.common.ui.fragments.PrefFragment;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit;
import com.androidcollider.easyfin.debts.add_edit.AddDebtFragment;
import com.androidcollider.easyfin.debts.add_edit.AddDebtModule;
import com.androidcollider.easyfin.debts.list.DebtsFragment;
import com.androidcollider.easyfin.debts.list.DebtsModule;
import com.androidcollider.easyfin.debts.pay.PayDebtFragment;
import com.androidcollider.easyfin.debts.pay.PayDebtModule;
import com.androidcollider.easyfin.home.HomeFragment;
import com.androidcollider.easyfin.main.MainFragment;
import com.androidcollider.easyfin.main.MainModule;
import com.androidcollider.easyfin.transactions.add_edit.btw_accounts.AddTransactionBetweenAccountsFragment;
import com.androidcollider.easyfin.transactions.add_edit.btw_accounts.AddTransactionBetweenAccountsModule;
import com.androidcollider.easyfin.transactions.add_edit.income_expense.AddTransactionIncomeExpenseFragment;
import com.androidcollider.easyfin.transactions.add_edit.income_expense.AddTransactionIncomeExpenseModule;
import com.androidcollider.easyfin.transactions.list.TransactionsFragment;
import com.androidcollider.easyfin.transactions.list.TransactionsModule;

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
        DialogModule.class,
        PermissionModule.class,
        AccountsToSpinViewModelModule.class,
        AnalyticsModule.class,
        MainModule.class,
        AccountsModule.class,
        TransactionsModule.class,
        DebtsModule.class,
        AddAccountModule.class,
        AddTransactionBetweenAccountsModule.class,
        AddTransactionIncomeExpenseModule.class,
        AddDebtModule.class,
        PayDebtModule.class
})
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(MainFragment mainFragment);

    void inject(HomeFragment homeFragment);

    void inject(AccountsFragment accountsFragment);

    void inject(TransactionsFragment transactionsFragment);

    void inject(DebtsFragment debtsFragment);

    void inject(AddAccountFragment addAccountFragment);

    void inject(AddTransactionIncomeExpenseFragment addTransactionIncomeExpenseFragment);

    void inject(AddTransactionBetweenAccountsFragment addTransactionBetweenAccountsFragment);

    void inject(AddDebtFragment addDebtFragment);

    void inject(PayDebtFragment payDebtFragment);

    void inject(PrefFragment prefFragment);

    void inject(NumericDialogFragment numericDialogFragment);

    void inject(CommonFragment commonFragment);

    void inject(CommonFragmentAddEdit commonFragmentAddEdit);
}