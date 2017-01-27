package com.androidcollider.easyfin.transactions.add_edit.income_expense;

import android.app.DatePickerDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgHome;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.ui.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.common.ui.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.common.ui.fragments.FrgNumericDialog;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit;

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
        implements FrgNumericDialog.OnCommitAmountListener, AddTransactionIncomeExpenseMVP.View {

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

    private DatePickerDialog datePickerDialog;
    private List<Account> accountList;

    @Inject
    ToastManager toastManager;

    @Inject
    DateFormatManager dateFormatManager;

    @Inject
    NumberFormatManager numberFormatManager;

    @Inject
    ResourcesManager resourcesManager;

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

        presenter.setView(this);
        presenter.setArguments(getArguments());
        presenter.loadAccounts();
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHome());
        EventBus.getDefault().post(new UpdateFrgTransactions());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    @OnClick({R.id.tvTransactionDate, R.id.tvAddTransDefAmount})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTransactionDate:
                datePickerDialog.show();
                break;
            case R.id.tvAddTransDefAmount:
                openNumericDialog();
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
        toastManager.showClosableToast(getActivity(), message, ToastManager.SHORT);
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
    public void setAccounts(List<Account> accountList) {
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
    public Account getAccount() {
        return (Account) spinAccount.getSelectedItem();
    }

    @Override
    public String getDate() {
        return tvDate.getText().toString();
    }

    @Override
    public int getCategory() {
        return spinCategory.getSelectedItemPosition();
    }

    @Override
    public List<Account> getAccounts() {
        return accountList;
    }

    @Override
    public void setupSpinners(String[] categoryArray, TypedArray categoryIcons) {
        spinCategory.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                R.id.tvSpinHeadIconText,
                R.id.ivSpinHeadIconText,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                categoryArray,
                categoryIcons));

        spinCategory.setSelection(categoryArray.length - 1);

        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountList,
                numberFormatManager,
                resourcesManager
        ));
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
        tvDate.setText(dateFormatManager.dateToString(calendar.getTime(), DateFormatManager.DAY_MONTH_YEAR_SPACED));

        datePickerDialog = new DatePickerDialog(getActivity(), (view1, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);

            if (newDate.getTimeInMillis() > System.currentTimeMillis()) {
                showMessage(getString(R.string.transaction_date_future));
            } else {
                tvDate.setText(dateFormatManager.dateToString(newDate.getTime(), DateFormatManager.DAY_MONTH_YEAR_SPACED));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }
}