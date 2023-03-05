package com.androidcollider.easyfin.accounts.add_edit

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideManager
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.ui.MainActivity
import com.androidcollider.easyfin.common.ui.adapters.SpinIconTextHeadAdapter
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment.OnCommitAmountListener
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class AddAccountFragment : CommonFragmentAddEdit(), OnCommitAmountListener, AddAccountMVP.View {

    private lateinit var spinType: Spinner
    private lateinit var spinCurrency: Spinner
    private lateinit var etName: EditText
    private lateinit var tvAmount: TextView
    private lateinit var mainContent: ScrollView

    @Inject
    lateinit var shakeEditTextManager: ShakeEditTextManager

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var hideTouchOutsideManager: HideTouchOutsideManager

    @Inject
    lateinit var resourcesManager: ResourcesManager

    @Inject
    lateinit var presenter: AddAccountMVP.Presenter

    override fun getContentView(): Int {
        return R.layout.frg_add_account
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        setToolbar()
        setSpinner()
        presenter.setView(this)
        presenter.setArguments(arguments)
        hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(mainContent, activity)
    }

    private fun setupUI(view: View) {
        spinType = view.findViewById(R.id.spinAddAccountType)
        spinCurrency = view.findViewById(R.id.spinAddAccountCurrency)
        etName = view.findViewById(R.id.editTextAccountName)
        tvAmount = view.findViewById(R.id.tvAddAccountAmount)
        mainContent = view.findViewById(R.id.layoutActAccountParent)
        tvAmount.setOnClickListener { openNumericDialog() }
    }

    private fun setSpinner() {
        spinType.adapter = SpinIconTextHeadAdapter(
            activity,
            R.layout.spin_head_icon_text,
            R.id.tvSpinHeadIconText,
            R.id.ivSpinHeadIconText,
            R.layout.spin_drop_icon_text,
            R.id.tvSpinDropIconText,
            R.id.ivSpinDropIconText,
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_TYPE),
            resourcesManager.getIconArray(ResourcesManager.ICON_ACCOUNT_TYPE)
        )
        spinCurrency.adapter = SpinIconTextHeadAdapter(
            activity,
            R.layout.spin_head_icon_text,
            R.id.tvSpinHeadIconText,
            R.id.ivSpinHeadIconText,
            R.layout.spin_drop_icon_text,
            R.id.tvSpinDropIconText,
            R.id.ivSpinDropIconText,
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY),
            resourcesManager.getIconArray(ResourcesManager.ICON_FLAGS)
        )
    }

    private fun pushBroadcast() {
        EventBus.getDefault().post(UpdateFrgHomeBalance())
        EventBus.getDefault().post(UpdateFrgAccounts())
    }

    override fun onCommitAmountSubmit(amount: String) {
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
        etName.setText(name)
        etName.setSelection(etName.text.length)
    }

    override fun showType(type: Int) {
        spinType.setSelection(type)
    }

    override fun showCurrency(position: Int) {
        spinCurrency.setSelection(position)
        spinCurrency.isEnabled = false
    }

    override fun highlightNameField() {
        shakeEditTextManager.highlightEditText(etName)
    }

    override fun showMessage(message: String) {
        (activity as MainActivity?)?.let {
            toastManager.showClosableToast(it, message, ToastManager.SHORT)
        }
    }

    override fun openNumericDialog() {
        openNumericDialog(tvAmount.text.toString())
    }

    override fun performLastActionsAfterSaveAndClose() {
        pushBroadcast()
        popAll()
    }

    override val accountName: String
        get() = etName.text.toString()

    override val accountAmount: String
        get() = tvAmount.text.toString()

    override val accountCurrency: String
        get() = spinCurrency.selectedItem.toString()

    override val accountType: Int
        get() = spinType.selectedItemPosition
}