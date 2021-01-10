package com.androidcollider.easyfin.common.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.accounts.add_edit.AddAccountFragment;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.managers.permission.PermissionManager;
import com.androidcollider.easyfin.common.managers.rates.rates_loader.RatesLoaderManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.ui.adapters.NavigationDrawerRecyclerAdapter;
import com.androidcollider.easyfin.common.ui.fragments.PrefFragment;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit;
import com.androidcollider.easyfin.common.ui.fragments.common.PreferenceFragment;
import com.androidcollider.easyfin.debts.list.DebtsFragment;
import com.androidcollider.easyfin.faq.FAQFragment;
import com.androidcollider.easyfin.main.MainFragment;
import com.androidcollider.easyfin.transaction_categories.root.TransactionCategoriesRootFragment;

import javax.inject.Inject;

/**
 * @author Ihor Bilous
 */

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    DrawerLayout drawerLayout;
    RecyclerView recyclerNavDrawer;
    Toolbar toolbar;

    private static long backPressExitTime;
    private final int TOOLBAR_DEFAULT = 1;

    @Inject
    RatesLoaderManager ratesLoaderManager;

    @Inject
    ToastManager toastManager;

    @Inject
    DialogManager dialogManager;

    @Inject
    ResourcesManager resourcesManager;

    @Inject
    PermissionManager permissionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_root_layout);

        ((App) getApplication()).getComponent().inject(this);

        setupUI();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        ratesLoaderManager.updateRatesForExchange();

        initializeViews();
        setToolbar(getString(R.string.app_name), TOOLBAR_DEFAULT);

        addFragment(new MainFragment());

        permissionManager.setActivity(this);
        permissionManager.requestRequiredPermissions();
    }

    private void setupUI() {
        drawerLayout = findViewById(R.id.navDrawerLayout);
        recyclerNavDrawer = findViewById(R.id.recyclerViewNavDrawer);
        toolbar = findViewById(R.id.toolbarMain);
    }

    private void initializeViews() {
        setSupportActionBar(toolbar);

        if (recyclerNavDrawer != null) recyclerNavDrawer.setHasFixedSize(true);
        recyclerNavDrawer.setLayoutManager(new LinearLayoutManager(this));
        recyclerNavDrawer.setAdapter(new NavigationDrawerRecyclerAdapter(resourcesManager));

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        recyclerNavDrawer.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());

                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = recyclerNavDrawer.getChildAdapterPosition(child);
                    if (position != 0 && position != 5) {
                        drawerLayout.closeDrawers();

                        switch (position) {
                            case 1:
                                openSelectedFrgMainPage(0);
                                break;
                            case 2:
                                openSelectedFrgMainPage(1);
                                break;
                            case 3:
                                openSelectedFrgMainPage(2);
                                break;
                            case 4:
                                addFragment(new DebtsFragment());
                                break;
                            case 6:
                                addFragment(new TransactionCategoriesRootFragment());
                                break;
                            case 7:
                                addFragment(new PrefFragment());
                                break;
                            case 8:
                                addFragment(new FAQFragment());
                                break;
                            case 9:
                                dialogManager.showAppAboutDialog(MainActivity.this);
                                break;
                        }
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }

    private void setToolbar(String title, int mode) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            switch (mode) {
                case TOOLBAR_DEFAULT: {
                    actionBar.setDisplayShowCustomEnabled(false);
                    actionBar.setDisplayShowTitleEnabled(true);
                    actionBar.setTitle(title);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    toolbar.setNavigationIcon(R.drawable.ic_menu);
                    break;
                }
            }
        }
    }

    private void openSelectedFrgMainPage(int page) {
        popFragments();
        Fragment f = getTopFragment();
        if (f instanceof MainFragment) {
            MainFragment mainFragment = (MainFragment) f;
            mainFragment.openSelectedPage(page);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void addFragment(Fragment f) {
        treatFragment(f, true, false);
    }

    public Fragment getTopFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private void treatFragment(Fragment f, boolean addToBackStack, boolean replace) {
        String tag = f.getClass().getName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
        Fragment topFragment = getTopFragment();
        if (topFragment instanceof CommonFragment && !(topFragment instanceof CommonFragmentAddEdit)) {
            setToolbar((((CommonFragment) topFragment).getTitle()), TOOLBAR_DEFAULT);
        } else if (topFragment instanceof PreferenceFragment) {
            setToolbar((((PreferenceFragment) topFragment).getTitle()), TOOLBAR_DEFAULT);
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

    private void popFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getTopFragment();
        if (fragment instanceof MainFragment) {
            if (backPressExitTime + 2000 > System.currentTimeMillis()) {
                this.finish();
            } else {
                toastManager.showClosableToast(this, getString(R.string.press_again_to_exit), ToastManager.SHORT);
                backPressExitTime = System.currentTimeMillis();
            }
        } else if (fragment instanceof AddAccountFragment) popFragments();
        else if (fragment instanceof CommonFragmentAddEdit) popFragment();
        else popFragments();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        restartActivity();
        super.onConfigurationChanged(newConfig);
    }

    private void restartActivity() {
        Intent intent = getIntent();
        this.finish();
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}