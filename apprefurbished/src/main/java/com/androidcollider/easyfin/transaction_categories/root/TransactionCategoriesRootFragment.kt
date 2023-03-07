package com.androidcollider.easyfin.transaction_categories.root

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.getCustomView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.UpdateFrgTransactionCategories
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment
import com.androidcollider.easyfin.common.utils.animateViewWithChangeVisibilityAndClickable
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class TransactionCategoriesRootFragment : CommonFragment(),
    TransactionCategoriesRootMVP.View {

    private lateinit var pager: ViewPager2
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

    override val contentView: Int
        get() = R.layout.frg_transaction_categories_root

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
        pager = view.findViewById(R.id.vp_transaction_categories)
        tabLayout = view.findViewById(R.id.tabs_transaction_categories)
        fabAddNew = view.findViewById(R.id.fab_add_transaction_category)

        fabAddNew.setOnClickListener { transactionCategoryDialog?.show() }

        setupViewPager()
        buildTransactionCategoryDialog()

        showFab(show = false, withAnim = false)
        view.postDelayed({ showFab(show = true, withAnim = true) }, 1000)
    }

    private fun setupViewPager() {
        val adapterPager = TransactionCategoriesViewPager2Adapter(this)
        pager.adapter = adapterPager
        pager.offscreenPageLimit = 2
        pager.registerOnPageChangeCallback(pagerPageChangeCallback)

        val tabTitles = arrayOf(
            resources.getString(R.string.income).uppercase(Locale.getDefault()),
            resources.getString(R.string.cost).uppercase(Locale.getDefault())
        )
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun buildTransactionCategoryDialog() {
        activity?.let { act ->
            transactionCategoryDialog = dialogManager.buildAddTransactionCategoryDialog(
                act,
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
    }

    private fun checkCategoryIsExpense(): Boolean {
        return pager.currentItem == 1
    }

    val currentType: Int
        get() = pager.currentItem

    fun hideFab() {
        if (isFABVisible) {
            showFab(show = false, withAnim = true)
        }
    }

    fun showFab() {
        if (!isFABVisible) {
            showFab(show = true, withAnim = true)
        }
    }

    private fun showFab(show: Boolean, withAnim: Boolean) {
        if (withAnim) {
            animateViewWithChangeVisibilityAndClickable(
                fabAddNew,
                if (show) jumpFromBottomAnimation else jumpToBottomAnimation,
                show
            )
        } else {
            fabAddNew.visibility = if (show) View.VISIBLE else View.INVISIBLE
            fabAddNew.isClickable = show
        }

        isFABVisible = !isFABVisible
    }

    override val title: String
        get() = getString(R.string.transaction_categories)

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

    override fun onDestroyView() {
        super.onDestroyView()
        pager.unregisterOnPageChangeCallback(pagerPageChangeCallback)
    }

    var pagerPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            showFab()
        }
    }

    private var isFABVisible = true

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