package com.androidcollider.easyfin.debts.add_edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts
import com.androidcollider.easyfin.common.events.UpdateFrgDebts
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideManager
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.ui.MainActivity
import com.androidcollider.easyfin.common.ui.adapters.SpinAccountForTransHeadIconAdapter
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment.OnCommitAmountListener
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class AddDebtFragment : CommonFragmentAddEdit(), OnCommitAmountListener, AddDebtMVP.View {

    private lateinit var tvDate: TextView
    private lateinit var tvAmount: TextView
    private lateinit var etName: EditText
    private lateinit var spinAccount: Spinner
    private lateinit var cardView: CardView
    private lateinit var mainContent: ScrollView
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var accountList: MutableList<SpinAccountViewModel>

    @Inject
    lateinit var shakeEditTextManager: ShakeEditTextManager

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var hideTouchOutsideManager: HideTouchOutsideManager

    @Inject
    lateinit var dateFormatManager: DateFormatManager

    @Inject
    lateinit var resourcesManager: ResourcesManager

    @Inject
    lateinit var presenter: AddDebtMVP.Presenter

    override fun getContentView(): Int {
        return R.layout.frg_add_debt
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        setToolbar()
        hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(mainContent, activity)
        accountList = ArrayList()
        presenter.setView(this)
        presenter.setArguments(arguments)
        presenter.loadAccounts()
    }

    private fun setupUI(view: View) {
        tvDate = view.findViewById(R.id.tvAddDebtDate)
        tvAmount = view.findViewById(R.id.tvAddDebtAmount)
        etName = view.findViewById(R.id.editTextDebtName)
        spinAccount = view.findViewById(R.id.spinAddDebtAccount)
        cardView = view.findViewById(R.id.cardAddDebtElements)
        mainContent = view.findViewById(R.id.layoutActAddDebtParent)
        tvAmount.setOnClickListener { openNumericDialog() }
        tvDate.setOnClickListener { datePickerDialog.show() }
    }

    private fun pushBroadcast() {
        EventBus.getDefault().post(UpdateFrgHomeBalance())
        EventBus.getDefault().post(UpdateFrgAccounts())
        EventBus.getDefault().post(UpdateFrgDebts())
    }

    private fun setDateText(calendar: Calendar?) {
        tvDate.text =
            dateFormatManager.dateToString(
                calendar!!.time,
                DateFormatManager.DAY_MONTH_YEAR_SPACED
            )
    }

    override fun onCommitAmountSubmit(amount: String) {
        showAmount(amount)
    }

    override fun handleSaveAction() {
        presenter.save()
    }

    override fun showAmount(amount: String?) {
        setTVTextSize(tvAmount, amount!!, 10, 15)
        tvAmount.text = amount
    }

    override fun showName(name: String?) {
        etName.setText(name)
        etName.setSelection(etName.text.length)
    }

    override fun setupSpinner() {
        spinAccount.adapter = SpinAccountForTransHeadIconAdapter(
            activity,
            R.layout.spin_head_icon_text,
            accountList,
            resourcesManager
        )
    }

    override fun highlightNameField() {
        shakeEditTextManager.highlightEditText(etName)
    }

    override fun showAccount(position: Int) {
        spinAccount.setSelection(position)
    }

    override fun showMessage(message: String?) {
        (activity as MainActivity?)?.let {
            toastManager.showClosableToast(it, message, ToastManager.SHORT)
        }
    }

    override fun setupDateTimeField(calendar: Calendar?, initTime: Long) {
        setDateText(calendar)
        datePickerDialog = DatePickerDialog(
            activity as MainActivity,
            { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val newDate = Calendar.getInstance()
                newDate[year, monthOfYear] = dayOfMonth
                if (newDate.timeInMillis < initTime) {
                    showMessage(getString(R.string.debt_deadline_past))
                } else {
                    setDateText(newDate)
                }
            },
            calendar!![Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
    }

    override fun openNumericDialog() {
        openNumericDialog(tvAmount.text.toString())
    }

    override fun notifyNotEnoughAccounts() {
        cardView.visibility = View.GONE
        showDialogNoAccount(getString(R.string.dialog_text_debt_no_account), true)
    }

    override fun setAmountTextColor(color: Int) {
        tvAmount.setTextColor(color)
    }

    override fun performLastActionsAfterSaveAndClose() {
        pushBroadcast()
        finish()
    }

    override val amount: String
        get() = tvAmount.text.toString()

    override val account: SpinAccountViewModel
        get() = spinAccount.selectedItem as SpinAccountViewModel

    override val date: String
        get() = tvDate.text.toString()

    override val name: String
        get() = etName.text.toString()

    override var accounts: List<SpinAccountViewModel>
        get() = accountList
        set(accountList) {
            cardView.visibility = View.VISIBLE
            this.accountList.clear()
            this.accountList.addAll(accountList)
        }
}