package com.androidcollider.easyfin.main

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.accounts.add_edit.AddAccountFragment
import com.androidcollider.easyfin.accounts.list.AccountsFragment
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment
import com.androidcollider.easyfin.common.utils.animateViewWithChangeVisibilityAndClickable
import com.androidcollider.easyfin.transactions.add_edit.btw_accounts.AddTransactionBetweenAccountsFragment
import com.androidcollider.easyfin.transactions.add_edit.income_expense.AddTransactionIncomeExpenseFragment
import com.androidcollider.easyfin.transactions.list.TransactionsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class MainFragment : CommonFragment(), MainMVP.View {

    private lateinit var pager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var fabMenu: FloatingActionButton
    private lateinit var faButtonExpense: FloatingActionButton
    private lateinit var faButtonIncome: FloatingActionButton
    private lateinit var faButtonBTW: FloatingActionButton
    private lateinit var mainContent: RelativeLayout

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var presenter: MainMVP.Presenter

    override val contentView: Int
        get() = R.layout.frg_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
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
        setupFABs()

        showFABMenu(show = false, withAnim = false)
        view.postDelayed({ showFABMenu(show = true, withAnim = true) }, 1000)
    }

    private fun setupViewPager() {
        val adapterPager = MainViewPager2Adapter(this)
        pager.adapter = adapterPager
        pager.offscreenPageLimit = 3
        pager.registerOnPageChangeCallback(pagerPageChangeCallback)

        val tabTitles = arrayOf(
            resources.getString(R.string.tab_home).uppercase(Locale.getDefault()),
            resources.getString(R.string.tab_transactions).uppercase(Locale.getDefault()),
            resources.getString(R.string.tab_accounts).uppercase(Locale.getDefault())
        )
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun setupFABs() {
        fabMenu.setOnClickListener { checkPageNum() }
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
        if (!isFABMenuExpanded) {
            expandFABMenu(expand = true, withAnim = true)
        } else collapseFloatingMenu(true)
    }

    private fun collapseFloatingMenu(withAnim: Boolean) {
        if (isFABMenuExpanded) {
            expandFABMenu(expand = false, withAnim)
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
        if (isFABMenuVisible) {
            showFABMenu(show = false, withAnim = true)
        }
    }

    fun showMenu() {
        if (!isFABMenuVisible) {
            showFABMenu(show = true, withAnim = true)
        }
    }

    private fun showFABMenu(show: Boolean, withAnim: Boolean) {
        if (!show && isFABMenuExpanded) {
            expandFABMenu(expand = false, withAnim = true, hideMenu = true)
        } else if (withAnim) {
            animateViewWithChangeVisibilityAndClickable(
                fabMenu,
                if (show) jumpFromBottomAnimation else jumpToBottomAnimation,
                show
            )
        } else {
            fabMenu.visibility = if (show) VISIBLE else INVISIBLE
            fabMenu.isClickable = show
        }

        isFABMenuVisible = !isFABMenuVisible
    }

    private fun expandFABMenu(expand: Boolean, withAnim: Boolean, hideMenu: Boolean = false) {
        if (withAnim) {
            animateViewWithChangeVisibilityAndClickable(
                faButtonIncome,
                if (expand) fromBottomAnimation else toBottomAnimation,
                expand
            )
            animateViewWithChangeVisibilityAndClickable(
                faButtonExpense,
                if (expand) fromBottomAnimation else toBottomAnimation,
                expand
            )
            animateViewWithChangeVisibilityAndClickable(
                faButtonBTW,
                if (expand) fromBottomAnimation else toBottomAnimation,
                expand
            )

            val menuAnimationSet = AnimationSet(false)
            menuAnimationSet.addAnimation(if (expand) rotateOpenAnimation else rotateCloseAnimation)
            if (hideMenu) menuAnimationSet.addAnimation(jumpToBottomAnimation)
            animateViewWithChangeVisibilityAndClickable(fabMenu, menuAnimationSet, !hideMenu)
        } else {
            faButtonIncome.visibility = if (expand) VISIBLE else INVISIBLE
            faButtonExpense.visibility = if (expand) VISIBLE else INVISIBLE
            faButtonBTW.visibility = if (expand) VISIBLE else INVISIBLE

            faButtonIncome.isClickable = expand
            faButtonExpense.isClickable = expand
            faButtonBTW.isClickable = expand
        }

        isFABMenuExpanded = !isFABMenuExpanded
    }

    override fun informNoAccounts() {
        showDialogNoAccount()
    }

    override val title: String
        get() = getString(R.string.app_name)

    override fun onDestroyView() {
        super.onDestroyView()
        pager.unregisterOnPageChangeCallback(pagerPageChangeCallback)
    }

    var pagerPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            showMenu()
            if (position == 2) collapseFloatingMenu(true)
        }
    }

    private var isFABMenuVisible = true
    private var isFABMenuExpanded = false

    private val rotateOpenAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open
        )
    }
    private val rotateCloseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close
        )
    }
    private val jumpFromBottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.jump_from_down
        )
    }
    private val jumpToBottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.jump_to_down
        )
    }
    private val fromBottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.show_from_bottom
        )
    }
    private val toBottomAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.hide_to_bottom
        )
    }
}