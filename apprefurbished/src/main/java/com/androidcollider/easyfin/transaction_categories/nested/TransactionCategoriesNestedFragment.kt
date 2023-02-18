package com.androidcollider.easyfin.transaction_categories.nested

import android.content.res.TypedArray
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.getCustomView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.UpdateFrgTransactionCategories
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.models.TransactionCategory
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentWithEvents
import com.androidcollider.easyfin.transaction_categories.root.TransactionCategoriesRootFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class TransactionCategoriesNestedFragment : CommonFragmentWithEvents(),
        TransactionCategoriesNestedMVP.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerTransactionCategoriesAdapter
    private lateinit var etTransCategoryName: EditText

    private var updateTransactionCategoryDialog: MaterialDialog? = null

    private var type = 0

    @Inject
    lateinit var letterTileManager: LetterTileManager

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var shakeEditTextManager: ShakeEditTextManager

    @Inject
    lateinit var presenter: TransactionCategoriesNestedMVP.Presenter

    override fun getContentView(): Int {
        return R.layout.frg_transaction_categories_nested
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        arguments?.let {
            type = it.getInt(TYPE)
        }
        presenter.setView(this)
        presenter.setArguments(arguments)
        presenter.loadData()
    }

    private fun setupUI(view: View) {
        recyclerView = view.findViewById(R.id.rv_transaction_categories)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerAdapter = RecyclerTransactionCategoriesAdapter(letterTileManager)
        recyclerView.adapter = recyclerAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val parentFragment = parentFragment as TransactionCategoriesRootFragment?
                if (parentFragment != null) {
                    if (dy > 0) {
                        parentFragment.hideFab()
                    } else if (dy < 0) {
                        parentFragment.showFab()
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val currentType = currentParentType
        if (currentType == type) {
            val id = recyclerAdapter.currentId
            val itemId = item.itemId
            if (itemId == R.id.ctx_menu_edit_transaction_category) {
                showUpdateTransactionCategoryDialog(id)
            } else if (itemId == R.id.ctx_menu_delete_transaction_category) {
                showDeleteTransactionCategoryDialog(id)
            }
        }
        return super.onContextItemSelected(item)
    }

    private val currentParentType: Int
        get() {
            val parentFragment = parentFragment as TransactionCategoriesRootFragment?
            return parentFragment?.currentType ?: -1
        }

    private fun showDeleteTransactionCategoryDialog(id: Int) {
        activity?.let {
            dialogManager.showDeleteDialog(
                    it,
                    getString(R.string.category_delete_warning)
            ) { presenter.deleteTransactionCategoryById(id) }
        }
    }

    private fun showUpdateTransactionCategoryDialog(id: Int) {
        activity?.let { act ->
            updateTransactionCategoryDialog = dialogManager.buildUpdateTransactionCategoryDialog(act,
                    {
                        presenter.updateTransactionCategory(id, etTransCategoryName.text.toString().trim { it <= ' ' })
                    }
            ) { dialog: MaterialDialog ->
                dialog.dismiss()
                etTransCategoryName.text.clear()
            }
            val root: View? = updateTransactionCategoryDialog?.getCustomView()
            root?.let {
                etTransCategoryName = it.findViewById(R.id.et_transaction_category_name)
                etTransCategoryName.setText(presenter.getCategoryNameById(id))
                etTransCategoryName.setSelection(etTransCategoryName.text.toString().length)
            }
            updateTransactionCategoryDialog?.show()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateFrgTransactionCategories?) {
        presenter.loadTransactionCategories()
    }

    override fun setTransactionCategoryList(transactionCategoryList: List<TransactionCategory>, iconsArray: TypedArray) {
        recyclerAdapter.setItems(transactionCategoryList, iconsArray)
    }

    override fun showMessage(message: String) {
        activity?.let {
            toastManager.showClosableToast(activity, message, ToastManager.SHORT)
        }
    }

    override fun shakeDialogUpdateTransactionCategoryField() {
        shakeEditTextManager.highlightEditText(etTransCategoryName)
    }

    override fun dismissDialogUpdateTransactionCategory() {
        updateTransactionCategoryDialog?.let {
            if (it.isShowing) {
                it.dismiss()
                etTransCategoryName.text.clear()
            }
        }
    }

    override fun handleTransactionCategoryUpdated() {
        presenter.loadTransactionCategories()
        EventBus.getDefault().post(UpdateFrgTransactions())
    }

    override fun deleteTransactionCategory() {
        recyclerAdapter.deleteItem(recyclerAdapter.getPositionById(recyclerAdapter.currentId))
    }

    companion object {
        const val TYPE = "type"
        const val TYPE_EXPENSE = 1
        const val TYPE_INCOME = 0
    }
}