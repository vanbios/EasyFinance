package com.androidcollider.easyfin;

import com.androidcollider.easyfin.adapters.MyFragmentPagerAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FragmentAddTransactionBetweenAccounts;
import com.androidcollider.easyfin.fragments.FragmentAddTransactionDefault;

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
