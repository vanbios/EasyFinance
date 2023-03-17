package com.androidcollider.easyfin.common.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.permission.PermissionManager
import com.androidcollider.easyfin.common.managers.rates.rates_loader.RatesLoaderManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.google.android.material.appbar.MaterialToolbar
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class MainActivity : AppCompatActivity() {

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
        setContentView(R.layout.main)
        (application as App).component?.inject(this)
        setupUI()
        ratesLoaderManager.updateRatesForExchange()
        initializeViews()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(destinationChangedListener)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        permissionManager.setActivity(this)
        //permissionManager.requestRequiredPermissions()
    }

    private fun setupUI() {
        toolbar = findViewById(R.id.toolbarMain)
    }

    private fun initializeViews() {
        setSupportActionBar(toolbar)
    }

    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
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

    private fun isCurrentScreenMain(): Boolean {
        var currentScreenId = 0
        navController.currentDestination?.id?.let {
            currentScreenId = it
        }
        return currentScreenId == R.id.mainFragment
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
        NavController.OnDestinationChangedListener { _, _, _ ->
            hideKeyboard()
        }

    companion object {
        private var backPressExitTime: Long = 0
    }
}