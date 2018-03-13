package com.androidcollider.easyfin.transactions.add_edit.income_expense;

import android.app.DatePickerDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgHome;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactionCategories;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager;
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Ihor Bilous
 */

public class AddTransactionIncomeExpenseFragment extends CommonFragmentAddEdit
        implements NumericDialogFragment.OnCommitAmountListener, AddTransactionIncomeExpenseMVP.View {

    @BindView(R.id.tvTransactionDate)
    TextView tvDate;
    @BindView(R.id.tvAddTransDefAmount)
    TextView tvAmount;
    @BindView(R.id.spinAddTransCategory)
    Spinner spinCategory;
    @BindView(R.id.spinAddTransDefAccount)
    Spinner spinAccount;
    @BindView(R.id.scrollAddTransDef)
    ScrollView scrollView;
    @BindView(R.id.ivAddTransCategory)
    ImageView ivAddTransCategory;

    private DatePickerDialog datePickerDialog;
    private List<SpinAccountViewModel> accountList;

    private EditText etNewTransCategoryName;
    private MaterialDialog transactionCategoryDialog;

    @Inject
    ToastManager toastManager;

    @Inject
    DialogManager dialogManager;

    @Inject
    DateFormatManager dateFormatManager;

    @Inject
    ResourcesManager resourcesManager;

    @Inject
    LetterTileManager letterTileManager;

    @Inject
    ShakeEditTextManager shakeEditTextManager;

    @Inject
    AddTransactionIncomeExpenseMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_add_trans_def;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setToolbar();

        accountList = new ArrayList<>();
        buildTransactionCategoryDialog();

        presenter.setView(this);
        presenter.setArguments(getArguments());
        presenter.loadAccountsAndCategories();
    }

    private void setDateText(Calendar calendar) {
        tvDate.setText(dateFormatManager.dateToString(calendar.getTime(), DateFormatManager.DAY_MONTH_YEAR_SPACED));
    }

    private void buildTransactionCategoryDialog() {
        transactionCategoryDialog = dialogManager.buildAddTransactionCategoryDialog(getActivity(),
                (dialog, which) -> {
                    if (etNewTransCategoryName != null) {
                        presenter.addNewCategory(etNewTransCategoryName.getText().toString().trim());
                    }
                },
                (dialog, which) -> dialog.dismiss());

        View root = transactionCategoryDialog.getCustomView();
        if (root != null) {
            etNewTransCategoryName = root.findViewById(R.id.et_transaction_category_name);
        }
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHome());
        EventBus.getDefault().post(new UpdateFrgTransactions());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    @OnClick({R.id.tvTransactionDate, R.id.tvAddTransDefAmount, R.id.ivAddTransCategory})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTransactionDate:
                datePickerDialog.show();
                break;
            case R.id.tvAddTransDefAmount:
                openNumericDialog();
                break;
            case R.id.ivAddTransCategory:
                transactionCategoryDialog.show();
                break;
        }
    }

    @Override
    public void onCommitAmountSubmit(String amount) {
        showAmount(amount, presenter.getTransactionType());
    }

    @Override
    protected void handleSaveAction() {
        presenter.save();
    }

    @Override
    public void showAmount(String amount, int transType) {
        setTVTextSize(tvAmount, amount, 9, 14);
        tvAmount.setText(String.format("%1$s %2$s", transType == 1 ? "+" : "-", amount));
    }

    @Override
    public void showMessage(String message) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            toastManager.showClosableToast(activity, message, ToastManager.SHORT);
        }
    }

    @Override
    public void openNumericDialog() {
        openNumericDialog(tvAmount.getText().toString());
    }

    @Override
    public void notifyNotEnoughAccounts() {
        scrollView.setVisibility(View.GONE);
        showDialogNoAccount(getString(R.string.dialog_text_transaction_no_account), false);
    }

    @Override
    public void setAmountTextColor(int color) {
        tvAmount.setTextColor(color);
    }

    @Override
    public void setAccounts(List<SpinAccountViewModel> accountList) {
        this.accountList.clear();
        this.accountList.addAll(accountList);
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void performLastActionsAfterSaveAndClose() {
        pushBroadcast();
        finish();
    }

    @Override
    public String getAmount() {
        return tvAmount.getText().toString();
    }

    @Override
    public SpinAccountViewModel getAccount() {
        return (SpinAccountViewModel) spinAccount.getSelectedItem();
    }

    @Override
    public String getDate() {
        return tvDate.getText().toString();
    }

    @Override
    public int getCategory() {
        return ((TransactionCategory) spinCategory.getSelectedItem()).getId();
    }

    @Override
    public List<SpinAccountViewModel> getAccounts() {
        return accountList;
    }

    @Override
    public void setupSpinners(List<TransactionCategory> categoryList, TypedArray categoryIcons) {
        setupCategorySpinner(categoryList, categoryIcons, categoryIcons.length() - 1);

        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountList,
                resourcesManager
        ));
    }

    @Override
    public void setupCategorySpinner(List<TransactionCategory> categoryList, TypedArray categoryIcons, int selectedPos) {
        spinCategory.setAdapter(new TransactionCategoryAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                R.id.tvSpinHeadIconText,
                R.id.ivSpinHeadIconText,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                categoryList,
                categoryIcons,
                letterTileManager));

        spinCategory.setSelection(selectedPos);
    }

    @Override
    public void showCategory(int category) {
        spinCategory.setSelection(category);
    }

    @Override
    public void showAccount(int position) {
        spinAccount.setSelection(position);
    }

    @Override
    public void setupDateTimeField(Calendar calendar) {
        setDateText(calendar);

        datePickerDialog = new DatePickerDialog(getActivity(), (view1, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);

            if (newDate.getTimeInMillis() > System.currentTimeMillis()) {
                showMessage(getString(R.string.transaction_date_future));
            } else {
                setDateText(newDate);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void shakeDialogNewTransactionCategoryField() {
        if (etNewTransCategoryName != null) {
            shakeEditTextManager.highlightEditText(etNewTransCategoryName);
        }
    }

    @Override
    public void dismissDialogNewTransactionCategory() {
        if (transactionCategoryDialog != null && transactionCategoryDialog.isShowing()) {
            transactionCategoryDialog.dismiss();
            etNewTransCategoryName.getText().clear();
        }
    }

    @Override
    public void handleNewTransactionCategoryAdded() {
        EventBus.getDefault().post(new UpdateFrgTransactionCategories());
    }
}