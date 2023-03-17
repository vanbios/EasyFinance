package com.androidcollider.easyfin.main

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.accounts.list.AccountsFragment
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.ui.MainActivity
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment
import com.androidcollider.easyfin.common.utils.animateViewWithChangeVisibilityAndClickable
import com.androidcollider.easyfin.debts.list.DebtsFragment
import com.androidcollider.easyfin.main.bottom_sheet_menu.MainBottomSheetMenuItem
import com.androidcollider.easyfin.main.bottom_sheet_menu.MainBottomSheetMenuItemSelectedListener
import com.androidcollider.easyfin.main.bottom_sheet_menu.MainBottomSheetMenuRecyclerAdapter
import com.androidcollider.easyfin.transactions.list.TransactionsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class MainFragment : CommonFragment(), MainMVP.View {

    private lateinit var navView: BottomNavigationView
    private lateinit var fabMenu: FloatingActionButton
    private lateinit var mainContent: ConstraintLayout

    private lateinit var childNavController: NavController

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
        navView = view.findViewById(R.id.navigationBarMain)
        fabMenu = view.findViewById(R.id.btnFloatMain)
        mainContent = view.findViewById(R.id.main_content)

        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.bottomNavContainer) as NavHostFragment
        childNavController = navHostFragment.navController
        navView.setupWithNavController(childNavController)
        childNavController.addOnDestinationChangedListener(destinationChangedListener)

        fabMenu.setOnClickListener { showBottomSheetMenuDialog() }

        showFABMenu(show = false, withAnim = false)
        view.postDelayed({ showFABMenu(show = true, withAnim = true) }, 1000)
    }

    private fun goToAddTransaction(type: Int) {
        findNavController().navigate(
            R.id.addTransactionIncomeExpenseFragment,
            bundleOf(
                TransactionsFragment.MODE to TransactionsFragment.MODE_ADD,
                TransactionsFragment.TYPE to type
            )
        )
    }

    private fun goToAddTransBTW() {
        findNavController().navigate(
            R.id.addTransactionBetweenAccountsFragment
        )
    }

    private fun goToAddDebt(type: Int) {
        findNavController().navigate(
            R.id.addDebtFragment,
            bundleOf(
                DebtsFragment.MODE to DebtsFragment.ADD,
                DebtsFragment.TYPE to type
            )
        )
    }

    private fun goToAddAccount() {
        findNavController().navigate(
            R.id.action_mainFragment_to_addAccountFragment,
            bundleOf(
                AccountsFragment.MODE to AccountsFragment.ADD
            )
        )
    }

    private fun showBottomSheetMenuDialog() {
        if (isBottomSheetMenuDialogOpened) return

        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(R.layout.main_bottom_sheet_menu)
        val recyclerView =
            bottomSheetDialog.findViewById<RecyclerView>(R.id.rvMainBottomSheetMenu) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = MainBottomSheetMenuRecyclerAdapter(
            bottomSheetMenuItems,
            object : MainBottomSheetMenuItemSelectedListener {
                override fun onItemSelected(id: Int) {
                    bottomSheetDialog.dismiss()
                    goToAddScreen(id)
                }
            })

        bottomSheetDialog.show()
        isBottomSheetMenuDialogOpened = true
        bottomSheetDialog.setOnDismissListener { isBottomSheetMenuDialogOpened = false }
    }

    private val bottomSheetMenuItems = arrayOf(
        MainBottomSheetMenuItem(
            1,
            "Create account",
            R.drawable.text_box_plus_outline
        ),
        MainBottomSheetMenuItem(
            2,
            "Take in debt",
            R.drawable.ic_debt_take_white_pad_10_48dp
        ),
        MainBottomSheetMenuItem(
            3,
            "Give in debt",
            R.drawable.ic_debt_give_white_pad_10_48dp
        ),
        MainBottomSheetMenuItem(
            4,
            "Add transaction between accounts",
            R.drawable.bank_transfer
        ),
        MainBottomSheetMenuItem(
            5,
            "Add income transaction",
            R.drawable.bank_transfer_in
        ),
        MainBottomSheetMenuItem(
            6,
            "Add expense transaction",
            R.drawable.bank_transfer_out
        )
    )

    private fun goToAddScreen(id: Int) {
        when (id) {
            1 -> goToAddAccount()
            2 -> goToAddDebt(DebtsFragment.TYPE_TAKE)
            3 -> goToAddDebt(DebtsFragment.TYPE_GIVE)
            4 -> goToAddTransBTW()
            5 -> goToAddTransaction(TransactionsFragment.TYPE_INCOME)
            6 -> goToAddTransaction(TransactionsFragment.TYPE_EXPENSE)
        }
    }

    private fun showDialogNoAccount() {
        activity?.let {
            dialogManager.showNoAccountDialog(it) { goToAddAccount() }
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
        if (isFABMenuVisible == show) return
        if (withAnim) {
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

    override fun informNoAccounts() {
        showDialogNoAccount()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        childNavController.removeOnDestinationChangedListener(destinationChangedListener)
    }

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            destination.label?.let {
                if (it.isNotEmpty()) {
                    (activity as MainActivity?)?.setToolbarTitle(it.toString())
                }
            }
        }

    override val title: String
        get() = getString(R.string.app_name)

    private var isFABMenuVisible = true
    private var isBottomSheetMenuDialogOpened = false

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
}