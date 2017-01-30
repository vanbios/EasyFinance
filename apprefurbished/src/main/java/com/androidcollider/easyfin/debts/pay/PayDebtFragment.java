package com.androidcollider.easyfin.debts.pay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgDebts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.common.ui.fragments.FrgNumericDialog;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Ihor Bilous
 */

public class PayDebtFragment extends CommonFragmentAddEdit
        implements FrgNumericDialog.OnCommitAmountListener, PayDebtMVP.View {

    @BindView(R.id.tvPayDebtName)
    TextView tvDebtName;
    @BindView(R.id.tvPayDebtAmount)
    TextView tvAmount;
    @BindView(R.id.spinPayDebtAccount)
    Spinner spinAccount;
    @BindView(R.id.cardPayDebtElements)
    CardView cardView;
    @BindView(R.id.layoutActPayDebtParent)
    ScrollView mainContent;

    private List<Account> accountsAvailableList;

    @Inject
    ToastManager toastManager;

    @Inject
    HideTouchOutsideManager hideTouchOutsideManager;

    @Inject
    NumberFormatManager numberFormatManager;

    @Inject
    ResourcesManager resourcesManager;

    @Inject
    PayDebtMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_pay_debt;
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

        hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(mainContent, getActivity());

        accountsAvailableList = new ArrayList<>();

        presenter.setView(this);
        presenter.setArguments(getArguments());
        presenter.loadAccounts();
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
        EventBus.getDefault().post(new UpdateFrgDebts());
    }

    @OnClick({R.id.tvPayDebtAmount})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvPayDebtAmount:
                openNumericDialog();
                break;
        }
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
        tvDebtName.setText(name);
    }

    @Override
    public void setupSpinner() {
        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountsAvailableList,
                numberFormatManager,
                resourcesManager
        ));
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
    public void openNumericDialog() {
        openNumericDialog(tvAmount.getText().toString());
    }

    @Override
    public void notifyNotEnoughAccounts() {
        cardView.setVisibility(View.GONE);
        showDialogNoAccount(getString(R.string.debt_no_available_accounts_warning), true);
    }

    @Override
    public void disableAmountField() {
        tvAmount.setClickable(false);
    }

    @Override
    public void setAccounts(List<Account> accountList) {
        accountsAvailableList.clear();
        accountsAvailableList.addAll(accountList);
        cardView.setVisibility(View.VISIBLE);
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
    public Account getAccount() {
        return (Account) spinAccount.getSelectedItem();
    }

    @Override
    public List<Account> getAccounts() {
        return accountsAvailableList;
    }
}