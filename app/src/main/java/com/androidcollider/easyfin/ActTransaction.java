package com.androidcollider.easyfin;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.adapters.MyFragmentPagerAdapter;
import com.androidcollider.easyfin.fragments.FrgAddTransactionBetweenAccounts;
import com.androidcollider.easyfin.fragments.FrgAddTransactionDefault;
import com.androidcollider.easyfin.objects.InfoFromDB;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;



public class ActTransaction extends AppCompatActivity {

    private ArrayList<String> accountNames = null;

    private ViewPager pagerTrans;

    public static Intent intent;
    public static int modeTransDef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_transaction);

        intent = getIntent();
        modeTransDef = intent.getIntExtra("mode", 1);

        if (modeTransDef == 1) {
            setToolbar(R.string.edit_transaction);
        }
        else {
        setToolbar(R.string.new_transaction);
        }

        accountNames = InfoFromDB.getInstance().getAccountNames();

        if (accountNames.isEmpty()) {
            showDialogNoAccount();
        }
        else {
            setViewPager();
        }
    }

    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolBar.inflateMenu(R.menu.toolbar_transaction_menu);
    }


    private void setViewPager() {
        pagerTrans = (ViewPager) findViewById(R.id.pagerAddTransaction);
        MyFragmentPagerAdapter adapterPager = new MyFragmentPagerAdapter(getSupportFragmentManager());
        adapterPager.addFragment(new FrgAddTransactionDefault(),
                getResources().getString(R.string.tab_transaction_default));

        if (accountNames.size() > 1 && modeTransDef == 0) {
            adapterPager.addFragment(new FrgAddTransactionBetweenAccounts(),
                    getResources().getString(R.string.tab_transaction_between));
        }

        pagerTrans.setAdapter(adapterPager);
        pagerTrans.setOffscreenPageLimit(2);

        if (accountNames.size() > 1) {

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsAddTransaction);

            tabLayout.setTabTextColors(getResources().getColor(R.color.custom_blue_gray_light),
                    getResources().getColor(R.color.custom_text_light));
            tabLayout.setupWithViewPager(pagerTrans);
        }
    }


    private void showDialogNoAccount() {

        new MaterialDialog.Builder(this)
                .title(getString(R.string.no_account))
                .content(getString(R.string.dialog_text_transaction_no_account))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.return_to_main))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        goToAddNewAccount();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        returnToMain();
                    }
                })
                .cancelable(false)
                .show();
    }


    private void returnToMain() {
        this.finish();
    }

    private void goToAddNewAccount() {
        this.finish();
        openAddAccountActivity();
    }

    private void openAddAccountActivity() {
        Intent intent = new Intent(this, ActAccount.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;}
            case R.id.transaction_action_save: {
                checkTransactionType();
                return true;}

        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_transaction_menu, menu);
        MenuItem saveTransactionItem = menu.findItem(R.id.transaction_action_save);
        saveTransactionItem.setEnabled(true);

        if (accountNames.isEmpty()) {
            saveTransactionItem.setVisible(false);}

        return true;
    }

    private void checkTransactionType() {
        int position = pagerTrans.getCurrentItem();
        switch (position) {
            case 0: {
                MyFragmentPagerAdapter mfa1 = (MyFragmentPagerAdapter) pagerTrans.getAdapter();
                FrgAddTransactionDefault frgAddTransactionDefault =
                        (FrgAddTransactionDefault) mfa1.getItem(position);
                frgAddTransactionDefault.addTransaction();
                break;}
            case 1: {
                MyFragmentPagerAdapter mfa2 = (MyFragmentPagerAdapter) pagerTrans.getAdapter();
                FrgAddTransactionBetweenAccounts frgAddTransactionBetweenAccounts =
                        (FrgAddTransactionBetweenAccounts) mfa2.getItem(position);
                frgAddTransactionBetweenAccounts.addTransactionBTW();
                break;}
        }
    }
}
