package com.androidcollider.easyfin.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.ActAccount;
import com.androidcollider.easyfin.ActTransaction;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.MyFragmentPagerAdapter;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.SharedPref;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class FrgMain extends CommonFragment {

    public final static String BROADCAST_MAIN_SNACK_ACTION = "com.androidcollider.easyfin.mainsnack.broadcast";
    public final static String PARAM_STATUS_MAIN_SNACK = "show_main_snack";
    public final static int STATUS_MAIN_SNACK = 5;

    private BroadcastReceiver broadcastReceiver;

    private SharedPref sharedPref;

    private boolean isSnackBarDisabled;

    private View view;
    private ViewPager pager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frg_main, container, false);

        //InfoFromDB.getInstance().updateRatesForExchange();

        setViewPager();

        FloatingActionButton faButton = (FloatingActionButton) view.findViewById(R.id.btnFloatMain);
        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPageNum();
            }
        });


        if (InfoFromDB.getInstance().getAccountsNumber() == 0) {
            showDialogNoAccount();
        }

        sharedPref = new SharedPref(getActivity());

        isSnackBarDisabled = sharedPref.isSnackBarAccountDisable();

        if (!isSnackBarDisabled) {
            makeBroadcastReceiver();}

        return view;
    }

    private void setViewPager() {
        pager = (ViewPager) view.findViewById(R.id.pagerMain);
        MyFragmentPagerAdapter adapterPager = new MyFragmentPagerAdapter(getFragmentManager());
        adapterPager.addFragment(new FrgHome(), getResources().getString(R.string.tab_home));
        adapterPager.addFragment(new FrgTransactions(), getResources().getString(R.string.tab_transactions));
        adapterPager.addFragment(new FrgAccounts(), getResources().getString(R.string.tab_accounts));

        pager.setAdapter(adapterPager);
        pager.setOffscreenPageLimit(3);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabsMain);

        tabs.setTabTextColors(getResources().getColor(R.color.custom_blue_gray_light),
                getResources().getColor(R.color.custom_text_light));
        tabs.setupWithViewPager(pager);
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS_MAIN_SNACK, 0);

                if (status == STATUS_MAIN_SNACK) {

                    showSnackBar();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_MAIN_SNACK_ACTION);

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isSnackBarDisabled) {
            getActivity().unregisterReceiver(broadcastReceiver);}
    }

    private void showSnackBar() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinateLayoutFloatMain);

        Runnable task = new Runnable() {
            public void run() {
                Snackbar.make(coordinatorLayout, R.string.snack_account_list, Snackbar.LENGTH_LONG)
                        .setAction(R.string.get_it, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sharedPref.disableSnackBarAccount();
                            }
                        })
                        .show();
            }
        };
        worker.schedule(task, 2, TimeUnit.SECONDS);
    }

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    public void openSelectedPage(int page) {
        pager.setCurrentItem(page);
    }

    public void checkPageNum(){
        switch (pager.getCurrentItem()) {
            case 0:
            case 1: addTransaction(); break;
            case 2: addAccount(); break;
        }
    }

    private void addTransaction() {
        Intent intent = new Intent(getActivity(), ActTransaction.class);
        intent.putExtra("mode", 0);
        startActivity(intent);
    }

    private void addAccount() {
        Intent intent = new Intent(getActivity(), ActAccount.class);
        intent.putExtra("mode", 0);
        startActivity(intent);
    }

    private void showDialogNoAccount() {

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(getString(R.string.dialog_text_main_no_accounts))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.later))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addAccount();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                })
                .cancelable(false)
                .show();
    }

    @Override
    public String getTitle() {
        return getString(R.string.app_name);
    }

}
