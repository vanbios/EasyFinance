package com.androidcollider.easyfin.transactions.add_edit.btw_accounts

import android.os.Bundle
import android.view.View
import android.widget.*
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideManager
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.ui.adapters.SpinAccountForTransAdapter
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment.OnCommitAmountListener
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit
import com.androidcollider.easyfin.common.utils.EditTextAmountWatcher
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class AddTransactionBetweenAccountsFragment : CommonFragmentAddEdit(), OnCommitAmountListener,
    AddTransactionBetweenAccountsMVP.View {
    private lateinit var spinAccountFrom: Spinner
    private lateinit var spinAccountTo: Spinner
    private lateinit var etExchange: EditText
    private lateinit var tvAmount: TextView
    private lateinit var layoutExchange: RelativeLayout
    private lateinit var scrollView: ScrollView
    private lateinit var adapterAccountTo: SpinAccountForTransAdapter
    private lateinit var accountListFrom: MutableList<SpinAccountViewModel>
    private lateinit var accountListTo: MutableList<SpinAccountViewModel>

    @Inject
    lateinit var shakeEditTextManager: ShakeEditTextManager

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var hideTouchOutsideManager: HideTouchOutsideManager

    @Inject
    lateinit var resourcesManager: ResourcesManager

    @Inject
    lateinit var presenter: AddTransactionBetweenAccountsMVP.Presenter

    override val contentView: Int
        get() = R.layout.frg_add_trans_btw

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        setToolbar()
        accountListFrom = ArrayList()
        presenter.setView(this)
        presenter.loadAccounts()
    }

    private fun setupUI(view: View) {
        spinAccountFrom = view.findViewById(R.id.spinAddTransBTWAccountFrom)
        spinAccountTo = view.findViewById(R.id.spinAddTransBTWAccountTo)
        etExchange = view.findViewById(R.id.editTextTransBTWExchange)
        tvAmount = view.findViewById(R.id.tvAddTransBTWAmount)
        layoutExchange = view.findViewById(R.id.layoutAddTransBTWExchange)
        scrollView = view.findViewById(R.id.scrollAddTransBTW)
        tvAmount.setOnClickListener { openNumericDialog() }
    }

    private fun setSpinners() {
        accountListTo = ArrayList()
        activity?.let {
            spinAccountFrom.adapter = SpinAccountForTransAdapter(
                it,
                R.layout.spin_head_text,
                accountListFrom,
                resourcesManager
            )
        }

        accountListTo.addAll(accountListFrom)
        accountListTo.removeAt(spinAccountFrom.selectedItemPosition)

        activity?.let {
            adapterAccountTo = SpinAccountForTransAdapter(
                it,
                R.layout.spin_head_text,
                accountListTo,
                resourcesManager
            )
        }

        spinAccountTo.adapter = adapterAccountTo
        spinAccountFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                updateSpinnerTo()
                presenter.setCurrencyMode()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinAccountTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                presenter.setCurrencyMode()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun updateSpinnerTo() {
        accountListTo.clear()
        accountListTo.addAll(accountListFrom)
        accountListTo.removeAt(spinAccountFrom.selectedItemPosition)
        adapterAccountTo.notifyDataSetChanged()
    }

    private fun pushBroadcast() {
        EventBus.getDefault().post(UpdateFrgHomeBalance())
        EventBus.getDefault().post(UpdateFrgAccounts())
    }

    override fun onCommitAmountSubmit(amount: String) {
        showAmount(amount)
    }

    public override fun handleSaveAction() {
        presenter.save()
    }

    override fun showAmount(amount: String?) {
        setTVTextSize(tvAmount, amount!!, 10, 15)
        tvAmount.text = amount
    }

    override fun showExchangeRate(rate: String?) {
        layoutExchange.visibility = View.VISIBLE
        etExchange.setText(rate)
        etExchange.setSelection(etExchange.text.length)
    }

    override fun hideExchangeRate() {
        layoutExchange.visibility = View.GONE
    }

    override fun highlightExchangeRateField() {
        shakeEditTextManager.highlightEditText(etExchange)
    }

    override fun showMessage(message: String) {
        activity?.let {
            toastManager.showClosableToast(it, message, ToastManager.SHORT)
        }
    }

    override fun openNumericDialog() {
        openNumericDialog(tvAmount.text.toString())
    }

    override fun performLastActionsAfterSaveAndClose() {
        pushBroadcast()
        finish()
    }

    override val amount: String
        get() = tvAmount.text.toString()
    override val exchangeRate: String
        get() = etExchange.text.toString()
    override val accountFrom: SpinAccountViewModel
        get() = spinAccountFrom.selectedItem as SpinAccountViewModel
    override val accountTo: SpinAccountViewModel
        get() = spinAccountTo.selectedItem as SpinAccountViewModel
    override val isMultiCurrencyTransaction: Boolean
        get() = layoutExchange.visibility == View.VISIBLE

    override fun notifyNotEnoughAccounts() {
        scrollView.visibility = View.GONE
        showDialogNoAccount(getString(R.string.dialog_text_transfer_no_accounts), false)
    }

    override fun setAccounts(accountList: List<SpinAccountViewModel>?) {
        accountListFrom.clear()
        accountListFrom.addAll(accountList!!)
        scrollView.visibility = View.VISIBLE
        showAmount("0,00")
        openNumericDialog()
        etExchange.addTextChangedListener(EditTextAmountWatcher(etExchange))
        setSpinners()
        hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(scrollView, requireActivity())
    }
}