package com.androidcollider.easyfin;


import android.content.Context;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.fragments.CommonFragment;
import com.androidcollider.easyfin.fragments.FrgDebts;
import com.androidcollider.easyfin.fragments.FrgFAQ;
import com.androidcollider.easyfin.fragments.FrgMain;
import com.androidcollider.easyfin.fragments.FrgPref;
import com.androidcollider.easyfin.fragments.PreferenceFragment;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.ToastUtils;


public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private DrawerLayout mDrawerLayout;
    private MaterialDialog appAboutDialog;

    private static long backPressExitTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        InfoFromDB.getInstance().updateRatesForExchange();

        setToolbar(R.string.app_name);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        buildAppAboutDialog();

        addFragment(new FrgMain());
    }

    private void setToolbar (int id) {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(id);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolBar.setNavigationIcon(R.drawable.ic_menu);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {

                            case R.id.nav_home: {
                                openSelectedFrgMainPage(0);
                                break;
                            }
                            case R.id.nav_transactions: {
                                openSelectedFrgMainPage(1);
                                break;
                            }
                            case R.id.nav_accounts: {
                                openSelectedFrgMainPage(2);
                                break;
                            }
                            case R.id.nav_debts: {
                                addFragment(new FrgDebts());
                                break;
                            }
                            case R.id.nav_settings: {
                                addFragment(new FrgPref());
                                break;
                            }
                            case R.id.nav_faq: {
                                addFragment(new FrgFAQ());
                                break;
                            }
                            case R.id.nav_about: {
                                appAboutDialog.show();
                                break;
                            }
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void openSelectedFrgMainPage(int page) {
        popFragments();

        Fragment f = getTopFragment();

        if (f instanceof FrgMain) {

            FrgMain frgMain = (FrgMain) f;
            frgMain.openSelectedPage(page);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void buildAppAboutDialog() {

        appAboutDialog = new MaterialDialog.Builder(this)
                .title(R.string.app_about)
                .customView(R.layout.app_about, true)
                .positiveText(R.string.ok)
                .build();

        View appAboutLayout = appAboutDialog.getCustomView();
        if (appAboutLayout != null) {
            TextView tvVersion = (TextView) appAboutLayout.findViewById(R.id.tvAboutAppVersion);
            tvVersion.setText(getString(R.string.about_app_version) + " " + BuildConfig.VERSION_NAME);
        }
    }


    public void addFragment(Fragment f){
        treatFragment(f, true, false);
    }

    public void replaceFragment(Fragment f){
        treatFragment(f, false, true);
    }

    public Fragment getTopFragment(){
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }
    
    private void treatFragment(Fragment f, boolean addToBackStack, boolean replace){
        String tag = f.getClass().getName();
        FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();

        if (replace) {

            ft.replace(R.id.fragment_container, f, tag);

        } else {

            Fragment currentTop = getTopFragment();

            if (currentTop != null) ft.hide(currentTop);

            ft.add(R.id.fragment_container, f, tag);
        }

        if (addToBackStack) ft.addToBackStack(tag);

        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackStackChanged() {
        int cnt = getSupportFragmentManager().getBackStackEntryCount();

        Log.d("COLLIDER", String.valueOf(cnt));

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(cnt > 0);

            Fragment topFragment = getTopFragment();

            if (topFragment instanceof CommonFragment) {
                actionBar.setTitle(((CommonFragment) topFragment).getTitle());
            }
            else if (topFragment instanceof PreferenceFragment) {
                actionBar.setTitle(((PreferenceFragment) topFragment).getTitle());
            }
        }

        hideKeyboard();
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void popFragments() {
        getSupportFragmentManager().popBackStackImmediate(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getTopFragment();

        if (fragment instanceof FrgMain) {

            if (backPressExitTime + 2000 > System.currentTimeMillis()) {

                this.finish();

            } else {

                ToastUtils.showClosableToast(this, getString(R.string.press_again_to_exit), 1);
                backPressExitTime = System.currentTimeMillis();
            }
        }

        else {

            popFragments();
        }
    }

}