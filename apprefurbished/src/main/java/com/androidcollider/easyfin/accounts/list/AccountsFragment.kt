package com.androidcollider.easyfin.accounts.list

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.accounts.add_edit.AddAccountFragment
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.models.Account
import com.androidcollider.easyfin.common.ui.MainActivity
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentWithEvents
import com.androidcollider.easyfin.main.MainFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class AccountsFragment : CommonFragmentWithEvents(), AccountsMVP.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    private lateinit var recyclerAdapter: RecyclerAccountAdapter

    @Inject
    lateinit var resourcesManager: ResourcesManager

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var presenter: AccountsMVP.Presenter


    override val contentView: Int
        get() = R.layout.frg_accounts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        presenter.setView(this)
        presenter.loadData()
    }

    private fun setupUI(view: View) {
        recyclerView = view.findViewById(R.id.recyclerAccount)
        tvEmpty = view.findViewById(R.id.tvEmptyAccounts)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerAdapter = RecyclerAccountAdapter(resourcesManager)
        recyclerView.adapter = recyclerAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val parentFragment = parentFragment as MainFragment?
                if (parentFragment != null) {
                    if (dy > 0) {
                        parentFragment.hideMenu()
                    } else if (dy < 0) {
                        parentFragment.showMenu()
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateFrgAccounts?) {
        presenter.loadData()
    }

    private fun setVisibility() {
        recyclerView.visibility = if (recyclerAdapter.itemCount == 0) View.GONE else View.VISIBLE
        tvEmpty.visibility = if (recyclerAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val id = recyclerAdapter.currentId
        val itemId = item.itemId
        if (itemId == R.id.ctx_menu_edit_account) {
            presenter.getAccountById(id)
        } else if (itemId == R.id.ctx_menu_delete_account) {
            showDialogDeleteAccount(id)
        }
        return super.onContextItemSelected(item)
    }

    private fun showDialogDeleteAccount(id: Int) {
        val activity = activity as MainActivity?
        if (activity != null) {
            dialogManager.showDeleteDialog(
                activity,
                getString(R.string.dialog_text_delete_account)
            ) { presenter.deleteAccountById(id) }
        }
    }

    private fun pushBroadcast() {
        EventBus.getDefault().post(UpdateFrgHomeBalance())
    }

    override fun setAccountList(accountList: List<AccountViewModel>) {
        recyclerAdapter.setItems(accountList)
        setVisibility()
    }

    override fun goToEditAccount(account: Account) {
        val addAccountFragment = AddAccountFragment()
        val arguments = Bundle()
        arguments.putInt(MODE, EDIT)
        arguments.putSerializable(ACCOUNT, account)
        addAccountFragment.arguments = arguments
        val activity = activity as MainActivity?
        activity?.addFragment(addAccountFragment)
    }

    override fun deleteAccount() {
        recyclerAdapter.deleteItem(recyclerAdapter.getPositionById(recyclerAdapter.currentId))
        setVisibility()
        pushBroadcast()
    }

    companion object {
        const val ACCOUNT = "account"
        const val MODE = "mode"
        const val ADD = 0
        const val EDIT = 1
    }
}