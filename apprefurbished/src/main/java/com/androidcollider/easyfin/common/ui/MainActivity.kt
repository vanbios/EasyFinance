package com.androidcollider.easyfin.common.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.accounts.add_edit.AddAccountFragment
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.permission.PermissionManager
import com.androidcollider.easyfin.common.managers.rates.rates_loader.RatesLoaderManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.ui.fragments.PrefFragment
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit
import com.androidcollider.easyfin.debts.list.DebtsFragment
import com.androidcollider.easyfin.faq.FAQFragment
import com.androidcollider.easyfin.main.MainFragment
import com.androidcollider.easyfin.transaction_categories.root.TransactionCategoriesRootFragment
import com.google.android.material.navigation.NavigationView
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    @Inject
    lateinit var ratesLoaderManager: RatesLoaderManager

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var resourcesManager: ResourcesManager

    @Inject
    lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nav_drawer_root_layout)
        (application as App).component?.inject(this)
        setupUI()
        val fragmentManager = supportFragmentManager
        fragmentManager.addOnBackStackChangedListener(this)
        ratesLoaderManager.updateRatesForExchange()
        initializeViews()
        setToolbar(getString(R.string.app_name))
        addFragment(MainFragment())
        permissionManager.setActivity(this)
        //permissionManager.requestRequiredPermissions()
    }

    private fun setupUI() {
        drawerLayout = findViewById(R.id.navDrawerLayout)
        navigationView = findViewById(R.id.nvView)
        toolbar = findViewById(R.id.toolbarMain)
    }

    private fun initializeViews() {
        setSupportActionBar(toolbar)
        val mDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )
        //mDrawerToggle.isDrawerIndicatorEnabled = true
        mDrawerToggle.syncState()
        drawerLayout.addDrawerListener(mDrawerToggle)

        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawers()

            when (item.itemId) {
                R.id.drawer_item_home -> openSelectedFrgMainPage(0)
                R.id.drawer_item_transactions -> openSelectedFrgMainPage(1)
                R.id.drawer_item_accounts -> openSelectedFrgMainPage(2)
                R.id.drawer_item_debts -> addFragment(DebtsFragment())
                R.id.drawer_item_transaction_categories -> addFragment(
                    TransactionCategoriesRootFragment()
                )
                R.id.drawer_item_settings -> addFragment(PrefFragment())
                R.id.drawer_item_faq -> addFragment(FAQFragment())
                R.id.drawer_item_about_app -> dialogManager.showAppAboutDialog(this@MainActivity)
            }

            if (item.itemId != R.id.drawer_item_about_app) {
                navigationView.menu.forEach {
                    it.isChecked = it.itemId == item.itemId
                }
            }

            false
        }
    }

    private fun setToolbar(title: String) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(false)
            actionBar.setDisplayShowTitleEnabled(true)
            actionBar.title = title
            actionBar.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationIcon(R.drawable.ic_menu)
        }
    }

    private fun openSelectedFrgMainPage(page: Int) {
        popFragments()
        val f = topFragment
        if (f is MainFragment) {
            f.openSelectedPage(page)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun addFragment(f: Fragment) {
        treatFragment(f, true, false)
    }

    private val topFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container)

    private fun treatFragment(f: Fragment, addToBackStack: Boolean, replace: Boolean) {
        val tag = f.javaClass.name
        val ft = supportFragmentManager.beginTransaction()
        if (replace) {
            ft.replace(R.id.fragment_container, f, tag)
        } else {
            val currentTop = topFragment
            if (currentTop != null) ft.hide(currentTop)
            ft.add(R.id.fragment_container, f, tag)
        }
        if (addToBackStack) ft.addToBackStack(tag)
        ft.commitAllowingStateLoss()
    }

    override fun onBackStackChanged() {
        val topFragment = topFragment
        if (topFragment is CommonFragment && topFragment !is CommonFragmentAddEdit) {
            setToolbar(topFragment.title)
        } else if (topFragment is PrefFragment) {
            setToolbar(topFragment.title)
        }
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val inputManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = this.currentFocus
        if (view != null) {
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun popFragments() {
        supportFragmentManager.popBackStackImmediate(1, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun popFragment() {
        supportFragmentManager.popBackStack()
    }

    override fun onBackPressed() {
        val fragment = topFragment
        if (fragment is MainFragment) {
            if (backPressExitTime + 2000 > System.currentTimeMillis()) {
                finish()
            } else {
                toastManager.showClosableToast(
                    this,
                    getString(R.string.press_again_to_exit),
                    ToastManager.SHORT
                )
                backPressExitTime = System.currentTimeMillis()
            }
        } else if (fragment is AddAccountFragment) popFragments()
        else if (fragment is CommonFragmentAddEdit) popFragment()
        else popFragments()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        restartActivity()
        super.onConfigurationChanged(newConfig)
    }

    private fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionManager.onRequestPermissionsResult(requestCode, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private var backPressExitTime: Long = 0
    }
}