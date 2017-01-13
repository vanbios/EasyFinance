package com.androidcollider.easyfin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.managers.accounts_info.AccountsInfoManager;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.managers.ui.shake_edit_text.ShakeEditTextManager;
import com.androidcollider.easyfin.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.repository.Repository;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import rx.Subscriber;

public class FrgAddAccount extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    private View view;
    private Spinner spinType, spinCurrency;
    private EditText etName;
    private TextView tvAmount;
    private String oldName;
    private int idAccount, mode;
    private Account accFrIntent;

    @Inject
    Repository repository;

    @Inject
    AccountsInfoManager accountsInfoManager;

    @Inject
    ShakeEditTextManager shakeEditTextManager;

    @Inject
    ToastManager toastManager;

    @Inject
    HideTouchOutsideManager hideTouchOutsideManager;

    @Inject
    NumberFormatManager numberFormatManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_account, container, false);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        initializeFields();
        setMode();
        setToolbar();
        hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(view.findViewById(R.id.layoutActAccountParent), getActivity());
        return view;
    }

    private void initializeFields() {
        spinType = (Spinner) view.findViewById(R.id.spinAddAccountType);
        spinCurrency = (Spinner) view.findViewById(R.id.spinAddAccountCurrency);

        etName = (EditText) view.findViewById(R.id.editTextAccountName);

        tvAmount = (TextView) view.findViewById(R.id.tvAddAccountAmount);
        tvAmount.setOnClickListener(v -> openNumericDialog(tvAmount.getText().toString()));
    }

    private void setMode() {
        mode = getArguments().getInt("mode", 0);
        switch (mode) {
            case 0:
                tvAmount.setText("0,00");
                openNumericDialog(tvAmount.getText().toString());
                break;
            case 1:
                accFrIntent = (Account) getArguments().getSerializable("account");
                setFields();
                break;
        }
        setSpinner();
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
                getResources().getStringArray(R.array.account_type_array),
                getResources().obtainTypedArray(R.array.account_type_icons)));

        spinCurrency.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                R.id.tvSpinHeadIconText,
                R.id.ivSpinHeadIconText,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                getResources().getStringArray(R.array.account_currency_array),
                getResources().obtainTypedArray(R.array.flag_icons)));

        if (mode == 1) {
            spinType.setSelection(accFrIntent.getType());
            String[] currencyArray = getResources().getStringArray(R.array.account_currency_array);
            String currencyVal = accFrIntent.getCurrency();

            for (int i = 0; i < currencyArray.length; i++) {
                if (currencyArray[i].equals(currencyVal)) {
                    spinCurrency.setSelection(i);
                }
            }

            spinCurrency.setEnabled(false);
        }
    }

    private void setFields() {
        oldName = accFrIntent.getName();
        etName.setText(oldName);
        etName.setSelection(etName.getText().length());

        final int PRECISE = 100;
        final String FORMAT = "###,##0.00";

        String amount = numberFormatManager.doubleToStringFormatterForEdit(accFrIntent.getAmount(), FORMAT, PRECISE);
        setTVTextSize(tvAmount, amount, 10, 15);
        tvAmount.setText(amount);

        idAccount = accFrIntent.getId();
    }

    private void addAccount() {
        if (checkForFillNameField()) {
            String name = etName.getText().toString();

            if (
                //InMemoryRepository.getInstance().checkForAccountNameMatches(name)
                    accountsInfoManager.checkForAccountNameMatches(name)
                    ) {
                shakeEditTextManager.highlightEditText(etName);
                toastManager.showClosableToast(getActivity(), getString(R.string.account_name_exist), ToastManager.SHORT);
            } else {
                double amount = Double.parseDouble(numberFormatManager.prepareStringToParse(tvAmount.getText().toString()));
                String currency = spinCurrency.getSelectedItem().toString();
                int type = spinType.getSelectedItemPosition();

                Account account = Account.builder()
                        .name(name)
                        .amount(amount)
                        .type(type)
                        .currency(currency)
                        .build();
                //InMemoryRepository.getInstance().getDataSource().insertNewAccount(account);
                repository.addNewAccount(account)
                        .subscribe(new Subscriber<Account>() {

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Account account1) {
                                lastActions();
                            }
                        });
            }
        }
    }

    private void editAccount() {
        if (checkForFillNameField()) {
            String name = etName.getText().toString();

            if (
                //InMemoryRepository.getInstance().checkForAccountNameMatches(name)
                    accountsInfoManager.checkForAccountNameMatches(name)
                            && !name.equals(oldName)) {
                shakeEditTextManager.highlightEditText(etName);
                toastManager.showClosableToast(getActivity(), getString(R.string.account_name_exist), ToastManager.SHORT);
            } else {
                String sum = numberFormatManager.prepareStringToParse(tvAmount.getText().toString());
                double amount = Double.parseDouble(sum);
                String currency = spinCurrency.getSelectedItem().toString();
                int type = spinType.getSelectedItemPosition();

                Account account = Account.builder()
                        .id(idAccount)
                        .name(name)
                        .amount(amount)
                        .type(type)
                        .currency(currency)
                        .build();

                repository.updateAccount(account)
                        .subscribe(new Subscriber<Account>() {

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Account account1) {
                                lastActions();
                            }
                        });
                //InMemoryRepository.getInstance().getDataSource().editAccount(account);
            }
        }
    }

    private void lastActions() {
        //InMemoryRepository.getInstance().updateAccountList();
        pushBroadcast();
        popAll();
    }

    private boolean checkForFillNameField() {
        String st = etName.getText().toString().replaceAll("\\s+", "");
        if (st.isEmpty()) {
            shakeEditTextManager.highlightEditText(etName);
            toastManager.showClosableToast(getActivity(), getString(R.string.empty_name_field), ToastManager.SHORT);
            return false;
        }
        return true;
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    @Override
    public void onCommitAmountSubmit(String amount) {
        setTVTextSize(tvAmount, amount, 10, 15);
        tvAmount.setText(amount);
    }

    @Override
    void handleSaveAction() {
        switch (mode) {
            case 0:
                addAccount();
                break;
            case 1:
                editAccount();
                break;
        }
    }
}