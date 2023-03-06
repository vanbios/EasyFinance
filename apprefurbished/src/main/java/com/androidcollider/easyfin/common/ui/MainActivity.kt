package com.androidcollider.easyfin.common.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.accounts.add_edit.AddAccountFragment
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.permission.PermissionManager
import com.androidcollider.easyfin.common.managers.rates.rates_loader.RatesLoaderManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.ui.adapters.NavigationDrawerRecyclerAdapter
import com.androidcollider.easyfin.common.ui.fragments.PrefFragment
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit
import com.androidcollider.easyfin.common.ui.fragments.common.PreferenceFragment
import com.androidcollider.easyfin.debts.list.DebtsFragment
import com.androidcollider.easyfin.faq.FAQFragment
import com.androidcollider.easyfin.main.MainFragment
import com.androidcollider.easyfin.transaction_categories.root.TransactionCategoriesRootFragment
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerNavDrawer: RecyclerView
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
        recyclerNavDrawer = findViewById(R.id.recyclerViewNavDrawer)
        toolbar = findViewById(R.id.toolbarMain)
    }

    private fun initializeViews() {
        setSupportActionBar(toolbar)
        recyclerNavDrawer.setHasFixedSize(true)
        recyclerNavDrawer.layoutManager = LinearLayoutManager(this)
        recyclerNavDrawer.adapter = NavigationDrawerRecyclerAdapter(resourcesManager)
        val mDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.app_name,
            R.string.app_name
        )
        drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
        val gestureDetector = GestureDetector(this, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })
        recyclerNavDrawer.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    val position = recyclerNavDrawer.getChildAdapterPosition(child)
                    if (position != 0 && position != 5) {
                        drawerLayout.closeDrawers()
                        when (position) {
                            1 -> openSelectedFrgMainPage(0)
                            2 -> openSelectedFrgMainPage(1)
                            3 -> openSelectedFrgMainPage(2)
                            4 -> addFragment(DebtsFragment())
                            6 -> addFragment(TransactionCategoriesRootFragment())
                            7 -> addFragment(PrefFragment())
                            8 -> addFragment(FAQFragment())
                            9 -> dialogManager.showAppAboutDialog(this@MainActivity)
                        }
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
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
        } else if (topFragment is PreferenceFragment) {
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
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        private var backPressExitTime: Long = 0
    }
}