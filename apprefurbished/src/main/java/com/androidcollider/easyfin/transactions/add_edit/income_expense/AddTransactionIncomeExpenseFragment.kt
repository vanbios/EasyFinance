package com.androidcollider.easyfin.transactions.add_edit.income_expense

import android.app.DatePickerDialog
import android.content.res.TypedArray
import android.os.Bundle
import android.view.View
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.getCustomView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts
import com.androidcollider.easyfin.common.events.UpdateFrgHome
import com.androidcollider.easyfin.common.events.UpdateFrgTransactionCategories
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.models.TransactionCategory
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
class AddTransactionIncomeExpenseFragment : CommonFragmentAddEdit(),
    OnCommitAmountListener,
    AddTransactionIncomeExpenseMVP.View {

    private lateinit var tvDate: TextView
    private lateinit var tvAmount: TextView
    private lateinit var spinCategory: Spinner
    private lateinit var spinAccount: Spinner
    private lateinit var scrollView: ScrollView
    private lateinit var ivAddTransCategory: ImageView
    private lateinit var etNewTransCategoryName: EditText

    private var datePickerDialog: DatePickerDialog? = null
    private var transactionCategoryDialog: MaterialDialog? = null

    private lateinit var accountList: MutableList<SpinAccountViewModel>

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var dateFormatManager: DateFormatManager

    @Inject
    lateinit var resourcesManager: ResourcesManager

    @Inject
    lateinit var letterTileManager: LetterTileManager

    @Inject
    lateinit var shakeEditTextManager: ShakeEditTextManager

    @Inject
    lateinit var presenter: AddTransactionIncomeExpenseMVP.Presenter

    override fun getContentView(): Int {
        return R.layout.frg_add_trans_def
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        accountList = ArrayList()
        buildTransactionCategoryDialog()
        presenter.setView(this)
        presenter.setArguments(arguments)
        presenter.loadAccountsAndCategories()
    }

    private fun setupUI(view: View) {
        tvDate = view.findViewById(R.id.tvTransactionDate)
        tvAmount = view.findViewById(R.id.tvAddTransDefAmount)
        spinCategory = view.findViewById(R.id.spinAddTransCategory)
        spinAccount = view.findViewById(R.id.spinAddTransDefAccount)
        scrollView = view.findViewById(R.id.scrollAddTransDef)
        ivAddTransCategory = view.findViewById(R.id.ivAddTransCategory)

        tvDate.setOnClickListener { datePickerDialog?.show() }
        tvAmount.setOnClickListener { openNumericDialog() }
        ivAddTransCategory.setOnClickListener { transactionCategoryDialog?.show() }

        setToolbar()
    }

    private fun setDateText(calendar: Calendar) {
        tvDate.text =
            dateFormatManager.dateToString(calendar.time, DateFormatManager.DAY_MONTH_YEAR_SPACED)
    }

    private fun buildTransactionCategoryDialog() {
        activity?.let { act ->
            transactionCategoryDialog = dialogManager.buildAddTransactionCategoryDialog(act,
                {
                    presenter.addNewCategory(
                        etNewTransCategoryName.text.toString().trim { it <= ' ' })
                }
            ) { dialog: MaterialDialog -> dialog.dismiss() }
            val root: View? = transactionCategoryDialog?.getCustomView()
            root?.let {
                etNewTransCategoryName = it.findViewById(R.id.et_transaction_category_name)
            }
        }
    }

    private fun pushBroadcast() {
        EventBus.getDefault().post(UpdateFrgHome())
        EventBus.getDefault().post(UpdateFrgTransactions())
        EventBus.getDefault().post(UpdateFrgAccounts())
    }

    override fun onCommitAmountSubmit(amount: String) {
        showAmount(amount, presenter.transactionType)
    }

    override fun handleSaveAction() {
        presenter.save()
    }

    override fun showAmount(amount: String, transType: Int) {
        setTVTextSize(tvAmount, amount, 9, 14)
        tvAmount.text = String.format("%1\$s %2\$s", if (transType == 1) "+" else "-", amount)
    }

    override fun showMessage(message: String) {
        activity?.let {
            toastManager.showClosableToast(it, message, ToastManager.SHORT)
        }
    }

    override fun openNumericDialog() {
        openNumericDialog(tvAmount.text.toString())
    }

    override fun notifyNotEnoughAccounts() {
        scrollView.visibility = View.GONE
        showDialogNoAccount(getString(R.string.dialog_text_transaction_no_account), false)
    }

    override fun setAmountTextColor(color: Int) {
        tvAmount.setTextColor(color)
    }

    override fun setAccounts(accountList: List<SpinAccountViewModel>) {
        this.accountList.clear()
        this.accountList.addAll(accountList)
        scrollView.visibility = View.VISIBLE
    }

    override fun performLastActionsAfterSaveAndClose() {
        pushBroadcast()
        finish()
    }

    override fun getAmount(): String {
        return tvAmount.text.toString()
    }

    override fun getAccount(): SpinAccountViewModel {
        return spinAccount.selectedItem as SpinAccountViewModel
    }

    override fun getDate(): String {
        return tvDate.text.toString()
    }

    override fun getCategory(): Int {
        return (spinCategory.selectedItem as TransactionCategory).id
    }

    override fun getAccounts(): List<SpinAccountViewModel> {
        return accountList
    }

    override fun setupSpinners(categoryList: List<TransactionCategory>, categoryIcons: TypedArray) {
        setupCategorySpinner(categoryList, categoryIcons, categoryIcons.length() - 1)
        spinAccount.adapter = SpinAccountForTransHeadIconAdapter(
            activity,
            R.layout.spin_head_icon_text,
            accountList,
            resourcesManager
        )
    }

    override fun setupCategorySpinner(
        categoryList: List<TransactionCategory>,
        categoryIcons: TypedArray, selectedPos: Int
    ) {
        spinCategory.adapter = TransactionCategoryAdapter(
            activity,
            R.layout.spin_head_icon_text,
            R.id.tvSpinHeadIconText,
            R.id.ivSpinHeadIconText,
            R.layout.spin_drop_icon_text,
            R.id.tvSpinDropIconText,
            R.id.ivSpinDropIconText,
            categoryList,
            categoryIcons,
            letterTileManager
        )
        spinCategory.setSelection(selectedPos)
    }

    override fun showCategory(category: Int) {
        spinCategory.setSelection(category)
    }

    override fun showAccount(position: Int) {
        spinAccount.setSelection(position)
    }

    override fun setupDateTimeField(calendar: Calendar) {
        setDateText(calendar)
        activity?.let {
            datePickerDialog = DatePickerDialog(it, { _: DatePicker?,
                                                      year: Int,
                                                      monthOfYear: Int,
                                                      dayOfMonth: Int ->
                val newDate = Calendar.getInstance()
                newDate[year, monthOfYear] = dayOfMonth
                if (newDate.timeInMillis > System.currentTimeMillis()) {
                    showMessage(getString(R.string.transaction_date_future))
                } else {
                    setDateText(newDate)
                }
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
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