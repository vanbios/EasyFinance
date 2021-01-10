package com.androidcollider.easyfin.accounts.add_edit;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * @author Ihor Bilous
 */

public class AddAccountFragment extends CommonFragmentAddEdit implements NumericDialogFragment.OnCommitAmountListener, AddAccountMVP.View {

    Spinner spinType;
    Spinner spinCurrency;
    EditText etName;
    TextView tvAmount;
    ScrollView mainContent;

    @Inject
    ShakeEditTextManager shakeEditTextManager;

    @Inject
    ToastManager toastManager;

    @Inject
    HideTouchOutsideManager hideTouchOutsideManager;

    @Inject
    ResourcesManager resourcesManager;

    @Inject
    AddAccountMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_add_account;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI(view);
        setToolbar();
        setSpinner();
        presenter.setView(this);
        presenter.setArguments(getArguments());
        hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(mainContent, getActivity());
    }

    private void setupUI(View view) {
        spinType = view.findViewById(R.id.spinAddAccountType);
        spinCurrency = view.findViewById(R.id.spinAddAccountCurrency);
        etName = view.findViewById(R.id.editTextAccountName);
        tvAmount = view.findViewById(R.id.tvAddAccountAmount);
        mainContent = view.findViewById(R.id.layoutActAccountParent);

        tvAmount.setOnClickListener(v -> openNumericDialog());
    }

    private void setSpinner() {
        spinType.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                R.id.tvSpinHeadIconText,
                R.id.ivSpinHeadIconText,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_TYPE),
                resourcesManager.getIconArray(ResourcesManager.ICON_ACCOUNT_TYPE)
        ));

        spinCurrency.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                R.id.tvSpinHeadIconText,
                R.id.ivSpinHeadIconText,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY),
                resourcesManager.getIconArray(ResourcesManager.ICON_FLAGS)
        ));
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
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
    public void showType(int type) {
        spinType.setSelection(type);
    }

    @Override
    public void showCurrency(int position) {
        spinCurrency.setSelection(position);
        spinCurrency.setEnabled(false);
    }

    @Override
    public void highlightNameField() {
        shakeEditTextManager.highlightEditText(etName);
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
    public void performLastActionsAfterSaveAndClose() {
        pushBroadcast();
        popAll();
    }

    @Override
    public String getAccountName() {
        return etName.getText().toString();
    }

    @Override
    public String getAccountAmount() {
        return tvAmount.getText().toString();
    }

    @Override
    public String getAccountCurrency() {
        return spinCurrency.getSelectedItem().toString();
    }

    @Override
    public int getAccountType() {
        return spinType.getSelectedItemPosition();
    }
}