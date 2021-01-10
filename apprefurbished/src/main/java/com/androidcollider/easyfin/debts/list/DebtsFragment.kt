package com.androidcollider.easyfin.debts.list

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts
import com.androidcollider.easyfin.common.events.UpdateFrgDebts
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.models.Debt
import com.androidcollider.easyfin.common.ui.MainActivity
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment
import com.androidcollider.easyfin.debts.add_edit.AddDebtFragment
import com.androidcollider.easyfin.debts.pay.PayDebtFragment
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class DebtsFragment : CommonFragment(), DebtsMVP.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var fabMenu: FloatingActionMenu
    private lateinit var faButtonTake: FloatingActionButton
    private lateinit var faButtonGive: FloatingActionButton
    private lateinit var mainContent: FrameLayout

    private lateinit var recyclerAdapter: RecyclerDebtAdapter

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var presenter: DebtsMVP.Presenter

    override fun getContentView(): Int {
        return R.layout.frg_debts
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        presenter.setView(this)
        presenter.loadData()
        EventBus.getDefault().register(this)
    }

    private fun setupUI(view: View) {
        recyclerView = view.findViewById(R.id.recyclerDebt)
        tvEmpty = view.findViewById(R.id.tvEmptyDebt)
        fabMenu = view.findViewById(R.id.btnFloatDebts)
        faButtonTake = view.findViewById(R.id.btnFloatAddDebtTake)
        faButtonGive = view.findViewById(R.id.btnFloatAddDebtGive)
        mainContent = view.findViewById(R.id.debts_content)

        faButtonTake.setOnClickListener {
            goToAddDebt(TYPE_TAKE)
            collapseFloatingMenu(false)
        }
        faButtonGive.setOnClickListener {
            goToAddDebt(TYPE_GIVE)
            collapseFloatingMenu(false)
        }

        setupRecyclerView()
        addNonFabTouchListener(mainContent)

        fabMenu.hideMenu(false)
        Handler().postDelayed({ fabMenu.showMenu(true) }, 300)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerAdapter = RecyclerDebtAdapter()
        recyclerView.adapter = recyclerAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    if (!fabMenu.isMenuHidden) {
                        fabMenu.hideMenu(true)
                    }
                } else if (dy < 0) {
                    if (fabMenu.isMenuHidden) {
                        fabMenu.showMenu(true)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun setVisibility() {
        recyclerView.visibility = if (recyclerAdapter.itemCount == 0) View.GONE else View.VISIBLE
        tvEmpty.visibility = if (recyclerAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val id = recyclerAdapter.currentId

        when (item.itemId) {
            R.id.ctx_menu_pay_all_debt -> {
                presenter.getDebtById(id, PAY_ALL, ACTION_PAY)
            }
            R.id.ctx_menu_pay_part_debt -> {
                presenter.getDebtById(id, PAY_PART, ACTION_PAY)
            }
            R.id.ctx_menu_take_more_debt -> {
                presenter.getDebtById(id, TAKE_MORE, ACTION_PAY)
            }
            R.id.ctx_menu_edit_debt -> {
                presenter.getDebtById(id, EDIT, ACTION_EDIT)
            }
            R.id.ctx_menu_delete_debt -> {
                showDialogDeleteDebt(id)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun showDialogDeleteDebt(id: Int) {
        val activity = activity as MainActivity?
        if (activity != null) {
            dialogManager.showDeleteDialog(
                    activity,
                    getString(R.string.debt_delete_warning)
            ) { presenter.deleteDebtById(id) }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateFrgDebts?) {
        presenter.loadData()
    }

    private fun pushBroadcast() {
        EventBus.getDefault().post(UpdateFrgHomeBalance())
        EventBus.getDefault().post(UpdateFrgAccounts())
    }

    private fun goToAddDebt(type: Int) {
        val addDebtFragment = AddDebtFragment()
        val arguments = Bundle()
        arguments.putInt(MODE, ADD)
        arguments.putInt(TYPE, type)
        addDebtFragment.arguments = arguments
        addFragment(addDebtFragment)
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
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                addNonFabTouchListener(innerView)
            }
        }
    }

    override fun getTitle(): String {
        return getString(R.string.debts)
    }

    override fun setDebtList(debtList: List<DebtViewModel>) {
        recyclerAdapter.setItems(debtList)
        setVisibility()
    }

    override fun goToEditDebt(debt: Debt, mode: Int) {
        val addDebtFragment = AddDebtFragment()
        val arguments = Bundle()
        arguments.putInt(MODE, mode)
        arguments.putSerializable(DEBT, debt)
        addDebtFragment.arguments = arguments
        addFragment(addDebtFragment)
    }

    override fun goToPayDebt(debt: Debt, mode: Int) {
        val payDebtFragment = PayDebtFragment()
        val arguments = Bundle()
        arguments.putInt(MODE, mode)
        arguments.putSerializable(DEBT, debt)
        payDebtFragment.arguments = arguments
        addFragment(payDebtFragment)
    }

    override fun deleteDebt() {
        recyclerAdapter.deleteItem(recyclerAdapter.getPositionById(recyclerAdapter.currentId))
        setVisibility()
        pushBroadcast()
    }

    companion object {
        const val PAY_ALL = 1
        const val PAY_PART = 2
        const val TAKE_MORE = 3
        const val ADD = 0
        const val EDIT = 1
        const val ACTION_EDIT = 1
        const val ACTION_PAY = 2
        const val TYPE_GIVE = 0
        const val TYPE_TAKE = 1
        const val DEBT = "debt"
        const val TYPE = "type"
        const val MODE = "mode"
    }
}