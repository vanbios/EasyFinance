package com.androidcollider.easyfin.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.accounts.add_edit.AddAccountFragment;
import com.androidcollider.easyfin.accounts.list.AccountsFragment;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.adapters.ViewPagerFragmentAdapter;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment;
import com.androidcollider.easyfin.home.FrgHome;
import com.androidcollider.easyfin.transactions.add_edit.btw_accounts.FrgAddTransactionBetweenAccounts;
import com.androidcollider.easyfin.transactions.add_edit.income_expense.FrgAddTransactionDefault;
import com.androidcollider.easyfin.transactions.list.TransactionsFragment;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Ihor Bilous
 */

public class MainFragment extends CommonFragment implements MainMVP.View {

    @BindView(R.id.pagerMain)
    ViewPager pager;
    @BindView(R.id.tabsMain)
    TabLayout tabLayout;
    @BindView(R.id.btnFloatMain)
    FloatingActionMenu fabMenu;
    @BindView(R.id.btnFloatAddTransExpense)
    FloatingActionButton faButtonExpense;
    @BindView(R.id.btnFloatAddTransIncome)
    FloatingActionButton faButtonIncome;
    @BindView(R.id.btnFloatAddTransBTW)
    FloatingActionButton faButtonBTW;
    @BindView(R.id.main_content)
    RelativeLayout mainContent;

    @Inject
    DialogManager dialogManager;

    @Inject
    MainMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    private void setupUI() {
        setupViewPager();
        setupFabs();
    }

    private void setupViewPager() {
        ViewPagerFragmentAdapter adapterPager = new ViewPagerFragmentAdapter(getChildFragmentManager());
        adapterPager.addFragment(new FrgHome(), getResources().getString(R.string.tab_home).toUpperCase());
        adapterPager.addFragment(new TransactionsFragment(), getResources().getString(R.string.tab_transactions).toUpperCase());
        adapterPager.addFragment(new AccountsFragment(), getResources().getString(R.string.tab_accounts).toUpperCase());

        pager.setAdapter(adapterPager);
        pager.setOffscreenPageLimit(3);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                showMenu();
                if (position == 2) collapseFloatingMenu(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabLayout.setupWithViewPager(pager);
    }

    private void setupFabs() {
        fabMenu.setOnMenuButtonClickListener(v -> checkPageNum());
        addNonFabTouchListener(mainContent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();

        fabMenu.hideMenu(false);
        new Handler().postDelayed(() -> fabMenu.showMenu(true), 1000);

        presenter.setView(this);
        presenter.checkIsAccountsExists();
    }

    /*private void showSnackBar() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayoutFloatMain);

        Runnable task = () -> Snackbar.make(coordinatorLayout, R.string.snack_account_list, Snackbar.LENGTH_LONG)
                .setAction(R.string.got_it, v -> sharedPref.disableSnackBarAccount())
                .show();
        worker.schedule(task, 2, TimeUnit.SECONDS);
    }

    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();*/

    public void openSelectedPage(int page) {
        pager.setCurrentItem(page);
    }

    public void checkPageNum() {
        switch (pager.getCurrentItem()) {
            case 0:
            case 1:
                changeFloatingMenuState();
                break;
            case 2:
                goToAddAccount();
                break;
        }
    }

    private void goToAddTransaction(int type) {
        FrgAddTransactionDefault frgAddTransDef = new FrgAddTransactionDefault();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        arguments.putInt("type", type);
        frgAddTransDef.setArguments(arguments);
        addFragment(frgAddTransDef);
    }

    private void goToAddTransBTW() {
        addFragment(new FrgAddTransactionBetweenAccounts());
    }

    private void goToAddAccount() {
        AddAccountFragment addAccountFragment = new AddAccountFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        addAccountFragment.setArguments(arguments);
        addFragment(addAccountFragment);
    }

    private void showDialogNoAccount() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            dialogManager.showNoAccountDialog(
                    activity,
                    (dialog, which) -> goToAddAccount()
            );
        }
    }

    private void changeFloatingMenuState() {
        if (!fabMenu.isOpened()) {
            fabMenu.open(true);
        } else collapseFloatingMenu(true);
    }

    private void collapseFloatingMenu(boolean withAnim) {
        if (fabMenu.isOpened()) {
            fabMenu.close(withAnim);
        }
    }

    private void addNonFabTouchListener(View view) {
        if (view instanceof RelativeLayout
                || view instanceof RecyclerView
                || view instanceof TextView) {
            view.setOnTouchListener((v, event) -> {
                collapseFloatingMenu(true);
                return false;
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                addNonFabTouchListener(innerView);
            }
        }
    }

    public void hideMenu() {
        if (!fabMenu.isMenuHidden()) {
            fabMenu.hideMenu(true);
        }
    }

    public void showMenu() {
        if (fabMenu.isMenuHidden()) {
            fabMenu.showMenu(true);
        }
    }

    @OnClick({R.id.btnFloatAddTransExpense, R.id.btnFloatAddTransIncome, R.id.btnFloatAddTransBTW})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFloatAddTransExpense:
                goToAddTransaction(0);
                collapseFloatingMenu(false);
                break;
            case R.id.btnFloatAddTransIncome:
                goToAddTransaction(1);
                collapseFloatingMenu(false);
                break;
            case R.id.btnFloatAddTransBTW:
                goToAddTransBTW();
                collapseFloatingMenu(false);
                break;
        }
    }

    @Override
    public void informNoAccounts() {
        showDialogNoAccount();
    }

    @Override
    public String getTitle() {
        return getString(R.string.app_name);
    }
}