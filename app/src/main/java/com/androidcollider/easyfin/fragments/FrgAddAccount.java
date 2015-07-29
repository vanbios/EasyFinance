package com.androidcollider.easyfin.fragments;


import android.content.Intent;
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
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.HideKeyboardUtils;
import com.androidcollider.easyfin.utils.ShakeEditText;
import com.androidcollider.easyfin.utils.SharedPref;
import com.androidcollider.easyfin.utils.ToastUtils;



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
        tvAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openNumericDialog();
            }
        });
    }

    private void setToolbar() {

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

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

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (mode) {
                        case 0: {addAccount(); break;}
                        case 1: {editAccount(); break;}
                    }
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void setMode () {
        mode = getArguments().getInt("mode", 0);

        switch (mode) {

            case 0: {
                tvAmount.setText("0,00");
                openNumericDialog();
                break;}

            case 1: {
                accFrIntent = (Account) getArguments().getSerializable("account");
                setFields();
                break;
            }
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

        if(checkForFillNameField()) {

            String name = etName.getText().toString();

            if (InfoFromDB.getInstance().checkForAccountNameMatches(name)) {
                ShakeEditText.highlightEditText(etName);
                ToastUtils.showClosableToast(getActivity(), getString(R.string.account_name_exist), 1);
            }

            else {

                double amount = Double.parseDouble(DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString()));
                String currency = spinCurrency.getSelectedItem().toString();
                int type = spinType.getSelectedItemPosition();

                Account account = new Account(name, amount, type, currency);

                InfoFromDB.getInstance().getDataSource().insertNewAccount(account);

                lastActions();
            }
        }
    }

    private void editAccount() {

        if (checkForFillNameField()) {

            String name = etName.getText().toString();

            if (InfoFromDB.getInstance().checkForAccountNameMatches(name) && ! name.equals(oldName)) {
                ShakeEditText.highlightEditText(etName);
                ToastUtils.showClosableToast(getActivity(), getString(R.string.account_name_exist), 1);
            }

            else {

                String sum = DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString());
                double amount = Double.parseDouble(sum);
                String currency = spinCurrency.getSelectedItem().toString();
                int type = spinType.getSelectedItemPosition();

                Account account = new Account(idAccount, name, amount, type, currency);

                InfoFromDB.getInstance().getDataSource().editAccount(account);

                lastActions();
            }
        }
    }

    private void lastActions() {
        InfoFromDB.getInstance().updateAccountList();
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
        Intent intentFrgMain = new Intent(FrgHome.BROADCAST_FRG_MAIN_ACTION);
        intentFrgMain.putExtra(FrgHome.PARAM_STATUS_FRG_MAIN, FrgHome.STATUS_UPDATE_FRG_MAIN_BALANCE);
        getActivity().sendBroadcast(intentFrgMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        getActivity().sendBroadcast(intentFrgAccounts);


        if (mode == 0) {
            SharedPref sp = new SharedPref(getActivity());
            if(!sp.isSnackBarAccountDisable()) {

                Intent intentMainSnack = new Intent(FrgMain.BROADCAST_MAIN_SNACK_ACTION);
                intentMainSnack.putExtra(FrgMain.PARAM_STATUS_MAIN_SNACK, FrgMain.STATUS_MAIN_SNACK);
                getActivity().sendBroadcast(intentMainSnack);
            }
        }
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

        if (length > 10 && length <= 15) {
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        }

        else if (length > 15) {
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        }

        else {
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        }
    }

}
