package com.androidcollider.easyfin.debts.pay

import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.ui.MainActivity
import com.androidcollider.easyfin.common.ui.adapters.SpinAccountForTransHeadIconAdapter
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit
import com.androidcollider.easyfin.common.utils.setSafeOnClickListener
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class PayDebtFragment : CommonFragmentAddEdit(), PayDebtMVP.View {

    private lateinit var tvDebtName: TextView
    private lateinit var tvAmount: TextView
    private lateinit var spinAccount: Spinner
    private lateinit var cardView: CardView
    private lateinit var mainContent: ScrollView

    private lateinit var accountsAvailableList: MutableList<SpinAccountViewModel>

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var hideTouchOutsideManager: HideTouchOutsideManager

    @Inject
    lateinit var resourcesManager: ResourcesManager

    @Inject
    lateinit var presenter: PayDebtMVP.Presenter

    override val contentView: Int
        get() = R.layout.frg_pay_debt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(mainContent, requireActivity())
        accountsAvailableList = ArrayList()
        presenter.setView(this)
        presenter.setArguments(arguments)
        presenter.loadAccounts()
    }

    private fun setupUI(view: View) {
        tvDebtName = view.findViewById(R.id.tvPayDebtName)
        tvAmount = view.findViewById(R.id.tvPayDebtAmount)
        spinAccount = view.findViewById(R.id.spinPayDebtAccount)
        cardView = view.findViewById(R.id.cardPayDebtElements)
        mainContent = view.findViewById(R.id.layoutActPayDebtParent)
        tvAmount.setSafeOnClickListener { openNumericDialog() }
    }

    private fun pushBroadcast() {
        //EventBus.getDefault().post(UpdateFrgHomeBalance())
        //EventBus.getDefault().post(UpdateFrgAccounts())
        //EventBus.getDefault().post(UpdateFrgDebts())
    }

    override fun updateAmount(amount: String) {
        showAmount(amount)
    }

    override fun handleSaveAction() {
        presenter.save()
    }

    override fun showAmount(amount: String) {
        setTVTextSize(tvAmount, amount, 10, 15)
        tvAmount.text = amount
    }

    override fun showName(name: String?) {
        tvDebtName.text = name
    }

    override fun setupSpinner() {
        activity?.let {
            spinAccount.adapter = SpinAccountForTransHeadIconAdapter(
                it,
                R.layout.spin_head_icon_text_without_tint,
                accountsAvailableList,
                resourcesManager
            )
        }
    }

    override fun showAccount(position: Int) {
        spinAccount.setSelection(position)
    }

    override fun showMessage(message: String) {
        (activity as MainActivity?)?.let {
            toastManager.showClosableToast(it, message, ToastManager.SHORT)
        }
    }

    override fun openNumericDialog() {
        openNumericDialog(tvAmount.text.toString())
    }

    override fun notifyNotEnoughAccounts() {
        cardView.visibility = View.GONE
        showDialogNoAccount(getString(R.string.debt_no_available_accounts_warning), true)
    }

    override fun disableAmountField() {
        tvAmount.isClickable = false
    }

    override fun performLastActionsAfterSaveAndClose() {
        pushBroadcast()
        findNavController().navigateUp()
    }

    override val amount: String
        get() = tvAmount.text.toString()

    override val account: SpinAccountViewModel
        get() = spinAccount.selectedItem as SpinAccountViewModel

    override var accounts: List<SpinAccountViewModel>
        get() = accountsAvailableList
        set(accountList) {
            accountsAvailableList.clear()
            accountsAvailableList.addAll(accountList)
            cardView.visibility = View.VISIBLE
        }
}