package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.fragments.FragmentExpense;
import com.androidcollider.easyfin.fragments.FragmentMain;
import com.androidcollider.easyfin.fragments.FragmentTransaction;
import com.androidcollider.easyfin.fragments.PageFragment;

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static final int PAGE_COUNT = 3;

    private Context context;


    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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
            case 0: tabName = context.getResources().getString(R.string.tab_main); break;
            case 1: tabName = context.getResources().getString(R.string.tab_expenses); break;
            case 2: tabName = context.getResources().getString(R.string.tab_transactions); break;
        }
        return tabName;
    }
}