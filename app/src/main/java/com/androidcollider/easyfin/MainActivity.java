package com.androidcollider.easyfin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.androidcollider.easyfin.adapters.MyFragmentPagerAdapter;
import com.androidcollider.easyfin.fragments.FrgAccounts;
import com.androidcollider.easyfin.fragments.FrgMain;
import com.androidcollider.easyfin.fragments.FrgTransactions;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private ViewPager pager;

    private BroadcastReceiver broadcastReceiver;

    public final static String BROADCAST_MAIN_SNACK_ACTION = "com.androidcollider.easyfin.mainsnack.broadcast";

    public final static String PARAM_STATUS_MAIN_SNACK = "show_main_snack";

    public final static int STATUS_MAIN_SNACK = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        setToolbar(R.string.app_name);

        setViewPager();

        makeBroadcastReceiver();
    }


    private void setToolbar (int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
    }

    private void setViewPager() {
        pager = (ViewPager) findViewById(R.id.pagerMain);
        MyFragmentPagerAdapter adapterPager = new MyFragmentPagerAdapter(getSupportFragmentManager());
        adapterPager.addFragment(FrgMain.newInstance(0), getResources().getString(R.string.tab_main));
        adapterPager.addFragment(FrgTransactions.newInstance(1), getResources().getString(R.string.tab_transactions));
        adapterPager.addFragment(FrgAccounts.newInstance(2), getResources().getString(R.string.tab_accounts));

        pager.setAdapter(adapterPager);
        pager.setOffscreenPageLimit(3);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabsMain);

        tabs.setTabTextColors(getResources().getColor(R.color.custom_blue_gray_light),
                getResources().getColor(R.color.custom_text_light));
        tabs.setupWithViewPager(pager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.exit:
                this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSnackBar() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayoutFloatMain);

        Runnable task = new Runnable() {
            public void run() {
                Snackbar.make(coordinatorLayout, R.string.snack_account_list, Snackbar.LENGTH_LONG)
                        .setAction(R.string.get_it, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences sharedPrefs = getSharedPreferences("SP_SnackBar", MODE_PRIVATE);
                                SharedPreferences.Editor ed;

                                if(!sharedPrefs.contains("initialized")) {
                                    ed = sharedPrefs.edit();
                                    ed.putBoolean("initialized", true);
                                    ed.apply();
                                }
                            }
                        })
                        .show();
            }
        };
        worker.schedule(task, 2, TimeUnit.SECONDS);
    }

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();


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

        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
    }

    public void addTransactionMain() {
        Intent intent = new Intent(this, ActTransaction.class);
        startActivity(intent);
    }

    public void addAccountMain() {
        Intent intent = new Intent(this, ActAccount.class);

        intent.putExtra("mode", 0);
        startActivity(intent);
    }

    public void checkPageNum(View view){
        switch (pager.getCurrentItem()) {
            case 0:
            case 1: addTransactionMain(); break;
            case 2: addAccountMain(); break;
        }
    }
}
