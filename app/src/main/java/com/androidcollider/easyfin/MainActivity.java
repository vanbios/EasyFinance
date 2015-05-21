package com.androidcollider.easyfin;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    static final int PAGE_COUNT = 3;


    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setToolbar(R.string.app_name);

        setViewPager();

        //dataSource = new DataSource(this);
        //fragmentMain = new FragmentMain();

        //fragmentMain.getActivity();
        //fragmentMain.setCurrentBalance();



    }



    private void setToolbar (int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
    }

    private void setViewPager() {
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));


        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        tabs.setViewPager(pager);

        pager.setOffscreenPageLimit(3);

        tabs.setTextSize(30);
        //tabs.setTextColorResource(R.color.navy);
        tabs.bringToFront();


        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                /*Fragment fragment = ((FragmentStatePagerAdapter)pager.getAdapter()).getFragment(position);

                if (position == 0 && fragment != null)
                {
                    fragment.onResume();
                }*/
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }



    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        /*private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;
        private Context mContext;*/



        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            /*mFragmentManager = fm;
            mFragmentTags = new HashMap<Integer, String>();
            mContext = MainActivity.this;*/




        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return FragmentMain.newInstance(position);
                case 1: return FragmentExpense.newInstance(position);
                case 2: return FragmentTransaction.newInstance(position);

                default: return PageFragment.newInstance(position);}
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String tabName = "";
            switch (position) {
                case 0: tabName = getResources().getString(R.string.tab_main); break;
                case 1: tabName = getResources().getString(R.string.tab_expenses); break;
                case 2: tabName = getResources().getString(R.string.tab_transactions); break;
                //case 3: tabName = "Курси"; break;
            }
            return tabName;
        }

        /*@Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                // record the fragment tag here.
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            }
            return obj;
        }

        public Fragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return mFragmentManager.findFragmentByTag(tag);
        }*/
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

    public void addTransactionMain(View view) {
        Intent intent = new Intent(this, AddTransaction.class);
        startActivity(intent);
    }

    public void addExpenseMain(View view) {
        Intent intent = new Intent(this, AddExpense.class);
        startActivity(intent);
    }
}
