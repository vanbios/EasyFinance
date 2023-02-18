package com.androidcollider.easyfin.main

import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.accounts.add_edit.AddAccountFragment
import com.androidcollider.easyfin.accounts.list.AccountsFragment
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.ui.adapters.ViewPagerFragmentAdapter
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment
import com.androidcollider.easyfin.home.HomeFragment
import com.androidcollider.easyfin.transactions.add_edit.btw_accounts.AddTransactionBetweenAccountsFragment
import com.androidcollider.easyfin.transactions.add_edit.income_expense.AddTransactionIncomeExpenseFragment
import com.androidcollider.easyfin.transactions.list.TransactionsFragment
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.tabs.TabLayout
import java.util.*
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class MainFragment : CommonFragment(), MainMVP.View {

    lateinit var pager: ViewPager
    lateinit var tabLayout: TabLayout
    lateinit var fabMenu: FloatingActionMenu
    lateinit var faButtonExpense: FloatingActionButton
    lateinit var faButtonIncome: FloatingActionButton
    lateinit var faButtonBTW: FloatingActionButton
    lateinit var mainContent: RelativeLayout

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var presenter: MainMVP.Presenter

    override fun getContentView(): Int {
        return R.layout.frg_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        presenter.setView(this)
        presenter.checkIsAccountsExists()
    }

    private fun setupUI(view: View) {
        pager = view.findViewById(R.id.pagerMain)
        tabLayout = view.findViewById(R.id.tabsMain)
        fabMenu = view.findViewById(R.id.btnFloatMain)
        faButtonExpense = view.findViewById(R.id.btnFloatAddTransExpense)
        faButtonIncome = view.findViewById(R.id.btnFloatAddTransIncome)
        faButtonBTW = view.findViewById(R.id.btnFloatAddTransBTW)
        mainContent = view.findViewById(R.id.main_content)

        faButtonExpense.setOnClickListener {
            goToAddTransaction(TransactionsFragment.TYPE_EXPENSE)
            collapseFloatingMenu(false)
        }
        faButtonIncome.setOnClickListener {
            goToAddTransaction(TransactionsFragment.TYPE_INCOME)
            collapseFloatingMenu(false)
        }
        faButtonBTW.setOnClickListener {
            goToAddTransBTW()
            collapseFloatingMenu(false)
        }

        setupViewPager()
        setupFabs()

        fabMenu.hideMenu(false)
        Handler().postDelayed({ fabMenu.showMenu(true) }, 1000)
    }

    private fun setupViewPager() {
        val adapterPager = ViewPagerFragmentAdapter(childFragmentManager)
        adapterPager.addFragment(HomeFragment(), resources.getString(R.string.tab_home).uppercase(Locale.getDefault()))
        adapterPager.addFragment(TransactionsFragment(), resources.getString(R.string.tab_transactions).uppercase(Locale.getDefault()))
        adapterPager.addFragment(AccountsFragment(), resources.getString(R.string.tab_accounts).uppercase(Locale.getDefault()))
        pager.adapter = adapterPager
        pager.offscreenPageLimit = 3
        pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                showMenu()
                if (position == 2) collapseFloatingMenu(true)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        tabLayout.setupWithViewPager(pager)
    }

    private fun setupFabs() {
        fabMenu.setOnMenuButtonClickListener { checkPageNum() }
        addNonFabTouchListener(mainContent)
    }

    fun openSelectedPage(page: Int) {
        pager.currentItem = page
    }

    private fun checkPageNum() {
        when (pager.currentItem) {
            0, 1 -> changeFloatingMenuState()
            2 -> goToAddAccount()
        }
    }

    private fun goToAddTransaction(type: Int) {
        val frgAddTransDef = AddTransactionIncomeExpenseFragment()
        val arguments = Bundle()
        arguments.putInt(TransactionsFragment.MODE, TransactionsFragment.MODE_ADD)
        arguments.putInt(TransactionsFragment.TYPE, type)
        frgAddTransDef.arguments = arguments
        addFragment(frgAddTransDef)
    }

    private fun goToAddTransBTW() {
        addFragment(AddTransactionBetweenAccountsFragment())
    }

    private fun goToAddAccount() {
        val addAccountFragment = AddAccountFragment()
        val arguments = Bundle()
        arguments.putInt(AccountsFragment.MODE, AccountsFragment.ADD)
        addAccountFragment.arguments = arguments
        addFragment(addAccountFragment)
    }

    private fun showDialogNoAccount() {
        activity?.let {
            dialogManager.showNoAccountDialog(it) { goToAddAccount() }
        }
    }

    private fun changeFloatingMenuState() {
        if (!fabMenu.isOpened) {
            fabMenu.open(true)
        } else collapseFloatingMenu(true)
    }

    private fun collapseFloatingMenu(withAnim: Boolean) {
        if (fabMenu.isOpened) {
            fabMenu.close(withAnim)
        }
    }

    private fun addNonFabTouchListener(view: View?) {
        if (view is RelativeLayout || view is RecyclerView || view is TextView) {
            view.setOnTouchListener(OnTouchListener { v: View?, event: MotionEvent? ->
                collapseFloatingMenu(true)
                if (event?.action == MotionEvent.ACTION_UP) {
                    v?.performClick()
                }
                false
            })
        }
        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                addNonFabTouchListener(innerView)
            }
        }
    }

    fun hideMenu() {
        if (!fabMenu.isMenuHidden) {
            fabMenu.hideMenu(true)
        }
    }

    fun showMenu() {
        if (fabMenu.isMenuHidden) {
            fabMenu.showMenu(true)
        }
    }

    override fun informNoAccounts() {
        showDialogNoAccount()
    }

    override fun getTitle(): String {
        return getString(R.string.app_name)
    }
}