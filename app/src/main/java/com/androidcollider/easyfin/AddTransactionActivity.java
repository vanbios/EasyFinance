package com.androidcollider.easyfin;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.adapters.MyFragmentPagerAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FragmentAddTransactionBetweenAccounts;
import com.androidcollider.easyfin.fragments.FragmentAddTransactionDefault;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import java.util.List;


public class AddTransactionActivity extends AppCompatActivity {

    DataSource dataSource;

    private ViewPager pagerTrans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtransaction);

        setToolbar(R.string.new_transaction);

        setViewPager();

        dataSource = new DataSource(this);

        checkForAccountExist();
    }


    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolBar.inflateMenu(R.menu.toolbar_add_transaction_menu);
    }


    private void setViewPager() {
        pagerTrans = (ViewPager) findViewById(R.id.pagerAddTransaction);
        MyFragmentPagerAdapter adapterPager = new MyFragmentPagerAdapter(getSupportFragmentManager());
        adapterPager.addFragment(new FragmentAddTransactionDefault(),
                getResources().getString(R.string.add_transaction_tab_default));
        adapterPager.addFragment(new FragmentAddTransactionBetweenAccounts(),
                getResources().getString(R.string.add_transaction_tab_between));

        pagerTrans.setAdapter(adapterPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsAddTransaction);
        tabLayout.setTabTextColors(getResources().getColor(R.color.custom_blue_gray_light),
                getResources().getColor(R.color.custom_text_light));
        tabLayout.setupWithViewPager(pagerTrans);

        pagerTrans.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {

                    case 0: {
                        checkForAccountExist();
                        break;
                    }

                    case 1: {
                        checkForAccountExist();
                        checkForCoupleAccountExist();
                        break;
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void showDialogNoExpense() {

        new MaterialDialog.Builder(this)
                .title(getString(R.string.no_expense))
                .content(getString(R.string.dialog_text_no_expense))
                .positiveText(getString(R.string.new_expense))
                .negativeText(getString(R.string.return_to_main))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        goToAddNewExpense();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        returnToMain();
                    }
                })
                .show();
    }

    private void showDialogSingleExpense() {

        new MaterialDialog.Builder(this)
                .title(getString(R.string.single_expense))
                .content(getString(R.string.dialog_text_single_expense))
                .positiveText(getString(R.string.new_expense))
                .negativeText(getString(R.string.return_to_main))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        goToAddNewExpense();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        returnToMain();
                    }
                })
                .show();
    }

    private void returnToMain() {
        closeActivity();
    }

    private void goToAddNewExpense() {
        closeActivity();
        openAddExpenseActivity();
    }

    private void openAddExpenseActivity() {
        Intent intent = new Intent(this, AddExpenseActivity.class);
        startActivity(intent);
    }

    private void checkForAccountExist() {
        List<String> accounts = dataSource.getAllAccountNames();

        if (accounts.size() == 0) {
            showDialogNoExpense();
        }
    }

    private void checkForCoupleAccountExist() {
        List<String> accounts = dataSource.getAllAccountNames();

        if (accounts.size() == 1) {
            showDialogSingleExpense();
        }
    }

    private void closeActivity() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                closeActivity();
                return true;}
            case R.id.add_transaction_action_save: {
                checkTransactionType();
                return true;}

        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_add_transaction_menu, menu);
        MenuItem saveTransaction = menu.findItem(R.id.add_transaction_action_save);
        saveTransaction.setEnabled(true);

        List<String> accounts = dataSource.getAllAccountNames();

        if (accounts.size() == 0) {
            saveTransaction.setEnabled(false);}

        return true;
    }


    private void checkTransactionType() {
        int position = pagerTrans.getCurrentItem();
        switch (position) {
            case 0:
            {
                MyFragmentPagerAdapter mfa = (MyFragmentPagerAdapter) pagerTrans.getAdapter();
                FragmentAddTransactionDefault fragmentAddTransactionDefault =
                        (FragmentAddTransactionDefault) mfa.getItem(position);
                fragmentAddTransactionDefault.addTransaction();
                break;}
            case 1:
            {Toast.makeText(this, "1", Toast.LENGTH_LONG).show(); break;}

        }
    }
}
