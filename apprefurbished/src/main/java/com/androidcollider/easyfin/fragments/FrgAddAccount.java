package com.androidcollider.easyfin.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.repository.memory.InMemoryRepository;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.HideKeyboardUtils;
import com.androidcollider.easyfin.utils.ShakeEditText;
import com.androidcollider.easyfin.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

public class FrgAddAccount extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    private View view;
    private Spinner spinType, spinCurrency;
    private EditText etName;
    private TextView tvAmount;
    private String oldName;
    private int idAccount, mode;
    private Account accFrIntent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_account, container, false);
        initializeFields();
        setMode();
        setToolbar();
        HideKeyboardUtils.setupUI(view.findViewById(R.id.layoutActAccountParent), getActivity());
        return view;
    }

    private void initializeFields() {
        spinType = (Spinner) view.findViewById(R.id.spinAddAccountType);
        spinCurrency = (Spinner) view.findViewById(R.id.spinAddAccountCurrency);

        etName = (EditText) view.findViewById(R.id.editTextAccountName);

        tvAmount = (TextView) view.findViewById(R.id.tvAddAccountAmount);
        tvAmount.setOnClickListener(v -> openNumericDialog());
    }

    private void setToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            ViewGroup actionBarLayout = (ViewGroup) getActivity().getLayoutInflater().inflate(
                    R.layout.save_close_buttons_toolbar, null);

            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(actionBarLayout, layoutParams);

            Toolbar parent = (Toolbar) actionBarLayout.getParent();
            parent.setContentInsetsAbsolute(0, 0);

            Button btnSave = (Button) actionBarLayout.findViewById(R.id.btnToolbarSave);
            Button btnClose = (Button) actionBarLayout.findViewById(R.id.btnToolbarClose);

            btnSave.setOnClickListener(v -> {
                switch (mode) {
                    case 0:
                        addAccount();
                        break;
                    case 1:
                        editAccount();
                        break;
                }
            });

            btnClose.setOnClickListener(v -> finish());
        }
    }

    private void setMode() {
        mode = getArguments().getInt("mode", 0);
        switch (mode) {
            case 0:
                tvAmount.setText("0,00");
                openNumericDialog();
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

        String amount = DoubleFormatUtils.doubleToStringFormatterForEdit(accFrIntent.getAmount(), FORMAT, PRECISE);
        setTVTextSize(amount);
        tvAmount.setText(amount);

        idAccount = accFrIntent.getId();
    }

    private void addAccount() {
        if (checkForFillNameField()) {
            String name = etName.getText().toString();

            if (InMemoryRepository.getInstance().checkForAccountNameMatches(name)) {
                ShakeEditText.highlightEditText(etName);
                ToastUtils.showClosableToast(getActivity(), getString(R.string.account_name_exist), 1);
            } else {
                double amount = Double.parseDouble(DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString()));
                String currency = spinCurrency.getSelectedItem().toString();
                int type = spinType.getSelectedItemPosition();

                Account account = Account.builder()
                        .name(name)
                        .amount(amount)
                        .type(type)
                        .currency(currency)
                        .build();
                InMemoryRepository.getInstance().getDataSource().insertNewAccount(account);
                lastActions();
            }
        }
    }

    private void editAccount() {
        if (checkForFillNameField()) {
            String name = etName.getText().toString();

            if (InMemoryRepository.getInstance().checkForAccountNameMatches(name) && !name.equals(oldName)) {
                ShakeEditText.highlightEditText(etName);
                ToastUtils.showClosableToast(getActivity(), getString(R.string.account_name_exist), 1);
            } else {
                String sum = DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString());
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
                InMemoryRepository.getInstance().getDataSource().editAccount(account);
                lastActions();
            }
        }
    }

    private void lastActions() {
        InMemoryRepository.getInstance().updateAccountList();
        pushBroadcast();
        popAll();
    }

    private boolean checkForFillNameField() {
        String st = etName.getText().toString().replaceAll("\\s+", "");
        if (st.isEmpty()) {
            ShakeEditText.highlightEditText(etName);
            ToastUtils.showClosableToast(getActivity(), getString(R.string.empty_name_field), 1);
            return false;
        }
        return true;
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    private void openNumericDialog() {
        Bundle args = new Bundle();
        args.putString("value", tvAmount.getText().toString());

        DialogFragment numericDialog = new FrgNumericDialog();
        numericDialog.setTargetFragment(this, 1);
        numericDialog.setArguments(args);
        numericDialog.show(getActivity().getSupportFragmentManager(), "numericDialog1");
    }

    @Override
    public void onCommitAmountSubmit(String amount) {
        setTVTextSize(amount);
        tvAmount.setText(amount);
    }

    private void setTVTextSize(String s) {
        int length = s.length();
        if (length > 10 && length <= 15)
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        else if (length > 15)
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        else
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
    }
}