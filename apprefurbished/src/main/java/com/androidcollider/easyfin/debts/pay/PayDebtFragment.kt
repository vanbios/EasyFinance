package com.androidcollider.easyfin.debts.pay;

import android.os.Bundle;
import android.view.View;
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
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @author Ihor Bilous
 */

public class PayDebtFragment extends CommonFragmentAddEdit
        implements NumericDialogFragment.OnCommitAmountListener, PayDebtMVP.View {

    TextView tvDebtName;
    TextView tvAmount;
    Spinner spinAccount;
    CardView cardView;
    ScrollView mainContent;

    private List<SpinAccountViewModel> accountsAvailableList;

    @Inject
    ToastManager toastManager;

    @Inject
    HideTouchOutsideManager hideTouchOutsideManager;

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
        setupUI(view);
        setToolbar();

        hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(mainContent, getActivity());

        accountsAvailableList = new ArrayList<>();

        presenter.setView(this);
        presenter.setArguments(getArguments());
        presenter.loadAccounts();
    }

    private void setupUI(View view) {
        tvDebtName = view.findViewById(R.id.tvPayDebtName);
        tvAmount = view.findViewById(R.id.tvPayDebtAmount);
        spinAccount = view.findViewById(R.id.spinPayDebtAccount);
        cardView = view.findViewById(R.id.cardPayDebtElements);
        mainContent = view.findViewById(R.id.layoutActPayDebtParent);

        tvAmount.setOnClickListener(v -> openNumericDialog());
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
        EventBus.getDefault().post(new UpdateFrgDebts());
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
    public void setAccounts(List<SpinAccountViewModel> accountList) {
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
    public SpinAccountViewModel getAccount() {
        return (SpinAccountViewModel) spinAccount.getSelectedItem();
    }

    @Override
    public List<SpinAccountViewModel> getAccounts() {
        return accountsAvailableList;
    }
}