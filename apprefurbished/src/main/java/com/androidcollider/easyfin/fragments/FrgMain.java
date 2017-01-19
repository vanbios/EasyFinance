package com.androidcollider.easyfin.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.ViewPagerFragmentAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.fragments.common.CommonFragment;
import com.androidcollider.easyfin.managers.accounts_info.AccountsInfoManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

public class FrgMain extends CommonFragment {

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
    AccountsInfoManager accountsInfoManager;


    @Override
    public int getContentView() {
        return R.layout.frg_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    private void initUI() {
        initViewPager();
        initFabs();
    }

    private void initViewPager() {
        ViewPagerFragmentAdapter adapterPager = new ViewPagerFragmentAdapter(getChildFragmentManager());
        adapterPager.addFragment(new FrgHome(), getResources().getString(R.string.tab_home).toUpperCase());
        adapterPager.addFragment(new FrgTransactions(), getResources().getString(R.string.tab_transactions).toUpperCase());
        adapterPager.addFragment(new FrgAccounts(), getResources().getString(R.string.tab_accounts).toUpperCase());

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

    private void initFabs() {
        fabMenu.setOnMenuButtonClickListener(v -> checkPageNum());
        addNonFabTouchListener(mainContent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();

        fabMenu.hideMenu(false);
        new Handler().postDelayed(() -> fabMenu.showMenu(true), 1000);

        checkForAndroidMPermissions();
        accountsInfoManager.getAccountsCountObservable()
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer count) {
                        if (count == 0) showDialogNoAccount();
                    }
                });
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
        FrgAddAccount frgAddAccount = new FrgAddAccount();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        frgAddAccount.setArguments(arguments);
        addFragment(frgAddAccount);
    }

    private void showDialogNoAccount() {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(getString(R.string.dialog_text_main_no_accounts))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.later))
                .onPositive((dialog, which) -> goToAddAccount())
                .cancelable(false)
                .show();
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

    private void checkForAndroidMPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasStoragePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            List<String> permissions = new ArrayList<>();
            if (hasStoragePermission != PackageManager.PERMISSION_GRANTED)
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (!permissions.isEmpty())
                requestPermissions(permissions.toArray(new String[permissions.size()]),
                        REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
        }
    }

    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 123;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    else if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.app_name);
    }
}