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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.ViewPagerFragmentAdapter;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

public class FrgMain extends CommonFragment {
    public final static String BROADCAST_MAIN_SNACK_ACTION = "com.androidcollider.easyfin.mainsnack.broadcast";
    public final static String PARAM_STATUS_MAIN_SNACK = "show_main_snack";
    public final static int STATUS_MAIN_SNACK = 5;

    //private BroadcastReceiver broadcastReceiver;
    //private SharedPref sharedPref;

    private View view;
    private ViewPager pager;

    //private boolean isSnackBarDisabled;

    private FloatingActionMenu fabMenu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_main, container, false);

        initUI();

        if (InfoFromDB.getInstance().getAccountsNumber() == 0) showDialogNoAccount();

        //sharedPref = new SharedPref(getActivity());
        //isSnackBarDisabled = sharedPref.isSnackBarAccountDisable();

        /*if (!isSnackBarDisabled) {
            makeBroadcastReceiver();
        }*/

        checkForAndroidMPermissions();

        return view;
    }

    private void initUI() {
        initViewPager();
        initFabs();
    }

    private void initViewPager() {
        pager = (ViewPager) view.findViewById(R.id.pagerMain);
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

        ((TabLayout) view.findViewById(R.id.tabsMain)).setupWithViewPager(pager);
    }

    private void initFabs() {
        fabMenu = (FloatingActionMenu) view.findViewById(R.id.btnFloatMain);
        fabMenu.setOnMenuButtonClickListener(v -> checkPageNum());

        FloatingActionButton faButtonExpense = (FloatingActionButton) view.findViewById(R.id.btnFloatAddTransExpense);
        FloatingActionButton faButtonIncome = (FloatingActionButton) view.findViewById(R.id.btnFloatAddTransIncome);
        FloatingActionButton faButtonBTW = (FloatingActionButton) view.findViewById(R.id.btnFloatAddTransBTW);

        faButtonExpense.setOnClickListener(v -> {
            goToAddTransaction(0);
            collapseFloatingMenu(false);
        });

        faButtonIncome.setOnClickListener(v -> {
            goToAddTransaction(1);
            collapseFloatingMenu(false);
        });

        faButtonBTW.setOnClickListener(v -> {
            goToAddTransBTW();
            collapseFloatingMenu(false);
        });

        addNonFabTouchListener(view.findViewById(R.id.main_content));
    }

    /*private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra(PARAM_STATUS_MAIN_SNACK, 0) == STATUS_MAIN_SNACK)
                    showSnackBar();
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_MAIN_SNACK_ACTION);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }*/

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabMenu.hideMenu(false);
        new Handler().postDelayed(() -> fabMenu.showMenu(true), 1000);
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        if (!isSnackBarDisabled)
            getActivity().unregisterReceiver(broadcastReceiver);
    }*/

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