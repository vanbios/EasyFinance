package com.androidcollider.easyfin.transactions.list

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
import com.androidcollider.easyfin.common.events.UpdateFrgHome
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager
import com.androidcollider.easyfin.common.models.Transaction
import com.androidcollider.easyfin.common.models.TransactionCategory
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
class TransactionsFragment : CommonFragmentWithEvents(), TransactionsMVP.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    private lateinit var recyclerAdapter: RecyclerTransactionAdapter

    @Inject
    lateinit var resourcesManager: ResourcesManager

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var letterTileManager: LetterTileManager

    @Inject
    lateinit var presenter: TransactionsMVP.Presenter

    override val contentView: Int
        get() = R.layout.frg_transactions

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
        recyclerView = view.findViewById(R.id.recyclerTransaction)
        tvEmpty = view.findViewById(R.id.tvEmptyTransactions)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerAdapter = RecyclerTransactionAdapter(resourcesManager, letterTileManager)
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

    private fun setVisibility() {
        recyclerView.visibility = if (recyclerAdapter.itemCount == 0) View.GONE else View.VISIBLE
        tvEmpty.visibility = if (recyclerAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val id = recyclerAdapter.currentId
        val itemId = item.itemId
        if (itemId == R.id.ctx_menu_edit_transaction) {
            presenter.getTransactionById(id)
        } else if (itemId == R.id.ctx_menu_delete_transaction) {
            showDialogDeleteTransaction(id)
        }
        return super.onContextItemSelected(item)
    }

    private fun showDialogDeleteTransaction(id: Int) {
        val activity = activity as MainActivity?
        if (activity != null) {
            dialogManager.showDeleteDialog(
                activity,
                getString(R.string.transaction_delete_warning)
            ) { presenter.deleteTransactionById(id) }
        }
    }

    private fun pushBroadcast() {
        EventBus.getDefault().post(UpdateFrgHome())
        EventBus.getDefault().post(UpdateFrgAccounts())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateFrgTransactions?) {
        presenter.loadData()
    }

    override fun setTransactionAndTransactionCategoriesLists(
        transactionList: List<TransactionViewModel>,
        transactionCategoryIncomeList: List<TransactionCategory>,
        transactionCategoryExpenseList: List<TransactionCategory>
    ) {
        recyclerAdapter.setTransactionCategories(
            transactionCategoryIncomeList,
            transactionCategoryExpenseList
        )
        recyclerAdapter.setItems(transactionList)
        setVisibility()
    }

    override fun goToEditTransaction(transaction: Transaction) {
        findNavController().navigate(
            R.id.addTransactionIncomeExpenseFragment,
            bundleOf(
                MODE to MODE_EDIT,
                TRANSACTION to transaction
            )
        )
    }

    override fun deleteTransaction() {
        recyclerAdapter.deleteItem(recyclerAdapter.getPositionById(recyclerAdapter.currentId))
        setVisibility()
        pushBroadcast()
    }

    companion object {
        const val TRANSACTION = "transaction"
        const val MODE = "mode"
        const val TYPE = "type"
        const val MODE_ADD = 0
        const val MODE_EDIT = 1
        const val TYPE_EXPENSE = 0
        const val TYPE_INCOME = 1
    }
}