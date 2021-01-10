package com.androidcollider.easyfin.transaction_categories.root

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.getCustomView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.UpdateFrgTransactionCategories
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.ui.adapters.ViewPagerFragmentAdapter
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment
import com.androidcollider.easyfin.transaction_categories.nested.TransactionCategoriesNestedFragment
import com.github.clans.fab.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class TransactionCategoriesRootFragment : CommonFragment(),
        TransactionCategoriesRootMVP.View {

    private lateinit var pager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var fabAddNew: FloatingActionButton
    private lateinit var etNewTransCategoryName: EditText

    private var transactionCategoryDialog: MaterialDialog? = null

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var shakeEditTextManager: ShakeEditTextManager

    @Inject
    lateinit var presenter: TransactionCategoriesRootMVP.Presenter

    override fun getContentView(): Int {
        return R.layout.frg_transaction_categories_root
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
    }

    private fun setupUI(view: View) {
        pager = view.findViewById(R.id.vp_transaction_categories)
        tabLayout = view.findViewById(R.id.tabs_transaction_categories)
        fabAddNew = view.findViewById(R.id.fab_add_transaction_category)

        fabAddNew.setOnClickListener { transactionCategoryDialog?.show() }

        setupViewPager()
        buildTransactionCategoryDialog()

        fabAddNew.hide(false)
        Handler().postDelayed({ fabAddNew.show(true) }, 1000)
    }

    private fun setupViewPager() {
        val adapterPager = ViewPagerFragmentAdapter(childFragmentManager)
        adapterPager.addFragment(getNestedFragment(false), resources.getString(R.string.income).toUpperCase(Locale.getDefault()))
        adapterPager.addFragment(getNestedFragment(true), resources.getString(R.string.cost).toUpperCase(Locale.getDefault()))
        pager.adapter = adapterPager
        pager.offscreenPageLimit = 2
        pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                showFab()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        tabLayout.setupWithViewPager(pager)
    }

    private fun getNestedFragment(isExpense: Boolean): TransactionCategoriesNestedFragment {
        val fragment = TransactionCategoriesNestedFragment()
        val args = Bundle()
        args.putInt(TransactionCategoriesNestedFragment.TYPE,
                if (isExpense) TransactionCategoriesNestedFragment.TYPE_EXPENSE else TransactionCategoriesNestedFragment.TYPE_INCOME
        )
        fragment.arguments = args
        return fragment
    }

    private fun buildTransactionCategoryDialog() {
        transactionCategoryDialog = dialogManager.buildAddTransactionCategoryDialog(activity!!,
                {
                    presenter.addNewCategory(
                            etNewTransCategoryName.text.toString().trim { it <= ' ' },
                            checkCategoryIsExpense()
                    )
                }
        ) { dialog: MaterialDialog -> dialog.dismiss() }
        val root: View? = transactionCategoryDialog?.getCustomView()
        root?.let {
            etNewTransCategoryName = it.findViewById(R.id.et_transaction_category_name)
        }
    }

    private fun checkCategoryIsExpense(): Boolean {
        return pager.currentItem == 1
    }

    val currentType: Int
        get() = pager.currentItem

    fun hideFab() {
        if (!fabAddNew.isHidden) {
            fabAddNew.hide(true)
        }
    }

    fun showFab() {
        if (fabAddNew.isHidden) {
            fabAddNew.show(true)
        }
    }

    override fun getTitle(): String {
        return getString(R.string.transaction_categories)
    }

    override fun showMessage(message: String) {
        activity?.let {
            toastManager.showClosableToast(it, message, ToastManager.SHORT)
        }
    }

    override fun shakeDialogNewTransactionCategoryField() {
        shakeEditTextManager.highlightEditText(etNewTransCategoryName)
    }

    override fun dismissDialogNewTransactionCategory() {
        transactionCategoryDialog?.let {
            if (it.isShowing) {
                it.dismiss()
                etNewTransCategoryName.text.clear()
            }
        }
    }

    override fun handleNewTransactionCategoryAdded() {
        EventBus.getDefault().post(UpdateFrgTransactionCategories())
    }
}