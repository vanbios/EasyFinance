package com.androidcollider.easyfin.common.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.permission.PermissionManager
import com.androidcollider.easyfin.common.managers.rates.rates_loader.RatesLoaderManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    private lateinit var navController: NavController

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
        ratesLoaderManager.updateRatesForExchange()
        initializeViews()
        setToolbar(getString(R.string.app_name))

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(destinationChangedListener)

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

            if (!isCurrentScreenMain()) {
                navController.popBackStack(R.id.mainFragment, false)
            }

            when (item.itemId) {
                R.id.drawer_item_home -> goToMainScreenPage(0)
                R.id.drawer_item_transactions -> goToMainScreenPage(1)
                R.id.drawer_item_accounts -> goToMainScreenPage(2)
                R.id.drawer_item_debts -> goToDebtsScreen()
                R.id.drawer_item_transaction_categories -> goToTransactionCategoriesScreen()
                R.id.drawer_item_settings -> goToSettingsScreen()
                R.id.drawer_item_faq -> goToFAQScreen()
                R.id.drawer_item_about_app -> dialogManager.showAppAboutDialog(this@MainActivity)
            }

            false
        }
    }

    private fun isCurrentScreenMain(): Boolean {
        var currentScreenId = 0
        navController.currentDestination?.id?.let {
            currentScreenId = it
        }
        return currentScreenId == R.id.mainFragment
    }

    private fun goToMainScreenPage(page: Int) {

    }

    private fun goToDebtsScreen() {
        navController.navigate(R.id.action_mainFragment_to_debtsFragment)
    }

    private fun goToTransactionCategoriesScreen() {
        navController.navigate(R.id.action_mainFragment_to_transactionCategoriesRootFragment)
    }

    private fun goToSettingsScreen() {
        navController.navigate(R.id.action_mainFragment_to_prefFragment)
    }

    private fun goToFAQScreen() {
        navController.navigate(R.id.action_mainFragment_to_FAQFragment)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
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

    override fun onBackPressed() {
        if (isCurrentScreenMain()) {
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
        } else navController.navigateUp()
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

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(destinationChangedListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionManager.onRequestPermissionsResult(requestCode, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            if (screenWithLabelsIds.contains(destination.id)) {
                destination.label?.let { setToolbar(it.toString()) }
            }
            hideKeyboard()
        }

    private val screenWithLabelsIds = setOf(
        R.id.mainFragment, R.id.debtsFragment,
        R.id.transactionCategoriesRootFragment,
        R.id.prefFragment, R.id.FAQFragment
    )

    companion object {
        private var backPressExitTime: Long = 0
    }
}