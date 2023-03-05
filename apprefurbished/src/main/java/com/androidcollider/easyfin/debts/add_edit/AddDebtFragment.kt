package com.androidcollider.easyfin.debts.add_edit;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgDebts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
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

/**
 * @author Ihor Bilous
 */

public class AddDebtFragment extends CommonFragmentAddEdit
        implements NumericDialogFragment.OnCommitAmountListener, AddDebtMVP.View {

    TextView tvDate;
    TextView tvAmount;
    EditText etName;
    Spinner spinAccount;
    CardView cardView;
    ScrollView mainContent;

    private DatePickerDialog datePickerDialog;
    private List<SpinAccountViewModel> accountList;

    @Inject
    ShakeEditTextManager shakeEditTextManager;

    @Inject
    ToastManager toastManager;

    @Inject
    HideTouchOutsideManager hideTouchOutsideManager;

    @Inject
    DateFormatManager dateFormatManager;

    @Inject
    ResourcesManager resourcesManager;

    @Inject
    AddDebtMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_add_debt;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI(view);
        setToolbar();

        hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(mainContent, getActivity());

        accountList = new ArrayList<>();

        presenter.setView(this);
        presenter.setArguments(getArguments());
        presenter.loadAccounts();
    }

    private void setupUI(View view) {
        tvDate = view.findViewById(R.id.tvAddDebtDate);
        tvAmount = view.findViewById(R.id.tvAddDebtAmount);
        etName = view.findViewById(R.id.editTextDebtName);
        spinAccount = view.findViewById(R.id.spinAddDebtAccount);
        cardView = view.findViewById(R.id.cardAddDebtElements);
        mainContent = view.findViewById(R.id.layoutActAddDebtParent);

        tvAmount.setOnClickListener(v -> openNumericDialog());
        tvDate.setOnClickListener(v -> datePickerDialog.show());
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
        EventBus.getDefault().post(new UpdateFrgDebts());
    }

    private void setDateText(Calendar calendar) {
        tvDate.setText(dateFormatManager.dateToString(calendar.getTime(), DateFormatManager.DAY_MONTH_YEAR_SPACED));
    }

    @Override
    public void onCommitAmountSubmit(String amount) {
        showAmount(amount);
    }

    @Override
    protected void handleSaveAction() {
        presenter.save();
    }


    @Override
    public void showAmount(String amount) {
        setTVTextSize(tvAmount, amount, 10, 15);
        tvAmount.setText(amount);
    }

    @Override
    public void showName(String name) {
        etName.setText(name);
        etName.setSelection(etName.getText().length());
    }

    @Override
    public void setupSpinner() {
        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountList,
                resourcesManager
        ));
    }

    @Override
    public void highlightNameField() {
        shakeEditTextManager.highlightEditText(etName);
    }

    @Override
    public void showAccount(int position) {
        spinAccount.setSelection(position);
    }

    @Override
    public void showMessage(String message) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            toastManager.showClosableToast(activity, message, ToastManager.SHORT);
        }
    }

    @Override
    public void setupDateTimeField(Calendar calendar, long initTime) {
        setDateText(calendar);

        datePickerDialog = new DatePickerDialog(getActivity(), (view1, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            if (newDate.getTimeInMillis() < initTime) {
                showMessage(getString(R.string.debt_deadline_past));
            } else {
                setDateText(newDate);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void openNumericDialog() {
        openNumericDialog(tvAmount.getText().toString());
    }

    @Override
    public void notifyNotEnoughAccounts() {
        cardView.setVisibility(View.GONE);
        showDialogNoAccount(getString(R.string.dialog_text_debt_no_account), true);
    }

    @Override
    public void setAmountTextColor(int color) {
        tvAmount.setTextColor(color);
    }

    @Override
    public void setAccounts(List<SpinAccountViewModel> accountList) {
        cardView.setVisibility(View.VISIBLE);
        this.accountList.clear();
        this.accountList.addAll(accountList);
    }

    @Override
    public void performLastActionsAfterSaveAndClose() {
        pushBroadcast();
        this.finish();
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
    public String getName() {
        return etName.getText().toString();
    }

    @Override
    public List<SpinAccountViewModel> getAccounts() {
        return accountList;
    }
}