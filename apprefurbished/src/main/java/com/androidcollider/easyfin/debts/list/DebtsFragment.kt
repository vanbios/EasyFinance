package com.androidcollider.easyfin.debts.list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
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
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentWithEvents
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class DebtsFragment : CommonFragmentWithEvents(), DebtsMVP.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    private lateinit var recyclerAdapter: RecyclerDebtAdapter

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var presenter: DebtsMVP.Presenter

    override val contentView: Int
        get() = R.layout.frg_debts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)

        recyclerAdapter = RecyclerDebtAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        presenter.setView(this)
        presenter.loadData()
    }

    private fun setupUI(view: View) {
        recyclerView = view.findViewById(R.id.recyclerDebt)
        tvEmpty = view.findViewById(R.id.tvEmptyDebt)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = recyclerAdapter
        /*recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    if (isFABMenuVisible) {
                        showFABMenu(show = false, withAnim = true)
                    }
                } else if (dy < 0) {
                    if (!isFABMenuVisible) {
                        showFABMenu(show = true, withAnim = true)
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })*/
    }

    private fun setVisibility() {
        recyclerView.visibility = if (recyclerAdapter.itemCount == 0) View.GONE else View.VISIBLE
        tvEmpty.visibility = if (recyclerAdapter.itemCount == 0) View.VISIBLE else View.GONE
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
        (activity as MainActivity?)?.let {
            dialogManager.showDeleteDialog(
                it,
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

    val title: String
        get() = getString(R.string.debts)

    override fun setDebtList(debtList: List<DebtViewModel>) {
        recyclerAdapter.setItems(debtList)
        setVisibility()
    }

    override fun goToEditDebt(debt: Debt?, mode: Int) {
        findNavController().navigate(
            R.id.addDebtFragment,
            bundleOf(
                MODE to mode,
                DEBT to debt
            )
        )
    }

    override fun goToPayDebt(debt: Debt?, mode: Int) {
        findNavController().navigate(
            R.id.payDebtFragment,
            bundleOf(
                MODE to mode,
                DEBT to debt
            )
        )
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