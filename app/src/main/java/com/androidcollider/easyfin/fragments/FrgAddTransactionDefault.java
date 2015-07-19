package com.androidcollider.easyfin.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.DateFormatUtils;
import com.androidcollider.easyfin.utils.EditTextAmountWatcher;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.HideKeyboardUtils;
import com.androidcollider.easyfin.utils.ShakeEditText;
import com.androidcollider.easyfin.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class FrgAddTransactionDefault extends CommonFragmentAddEdit {

    private TextView tvDate;
    private DatePickerDialog datePickerDialog;
    private Spinner spinCategory, spinAccount;
    private EditText etSum;

    private final String DATEFORMAT = "dd.MM.yyyy";

    private View view;

    private ArrayList<Account> accountList = null;

    private int mode;

    private Transaction transFromIntent;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_transaction_default, container, false);

        mode = getArguments().getInt("mode", 0);

        setToolbar();

        accountList = InfoFromDB.getInstance().getAccountList();

        if (accountList.isEmpty()) {
            showDialogNoAccount();
        }

        else {

            etSum = (EditText) view.findViewById(R.id.editTextTransSum);
            etSum.setTextColor(getResources().getColor(R.color.custom_red));

            etSum.addTextChangedListener(new EditTextAmountWatcher(etSum));

            setRadioGroupEvents();

            if (mode == 1) {
                transFromIntent = (Transaction) getArguments().getSerializable("transaction");

                final int PRECISE = 100;
                final String FORMAT = "0.00";

                double amount = transFromIntent.getAmount();
                etSum.setText(DoubleFormatUtils.doubleToStringFormatter(Math.abs(amount), FORMAT, PRECISE));

                if (!DoubleFormatUtils.isDoubleNegative(amount)) {
                    RadioButton rbPlus = (RadioButton) view.findViewById(R.id.radioButtonIncome);
                    rbPlus.setChecked(true);
                }
            }

            tvDate = (TextView) view.findViewById(R.id.tvTransactionDate);

            setDateTimeField();

            setSpinner();

            HideKeyboardUtils.setupUI(view.findViewById(R.id.scrollAddTransDef), getActivity());

        }

        return view;
    }

    private void setSpinner() {
        spinCategory = (Spinner) view.findViewById(R.id.spinAddTransCategory);
        spinAccount = (Spinner) view.findViewById(R.id.spinAddTransDefAccount);

        String[] categoryArray = getResources().getStringArray(R.array.transaction_category_array);


        spinCategory.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                R.id.tvSpinHeadIconText,
                R.id.ivSpinHeadIconText,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                categoryArray,
                getResources().obtainTypedArray(R.array.transaction_categories_icons)));

        spinCategory.setSelection(categoryArray.length - 1);


        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountList));

        if (mode == 1) {

            String accountName = transFromIntent.getAccountName();

            for (int i = 0; i < accountList.size(); i++) {

                Account account = accountList.get(i);

                if (account.getName().equals(accountName)) {
                    spinAccount.setSelection(i);
                    break;
                }
            }
            spinAccount.setEnabled(false);

            spinCategory.setSelection(transFromIntent.getCategory());
        }
    }

    private void setRadioGroupEvents() {
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupTransDef);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.radioButtonCost: {
                        etSum.setTextColor(getResources().getColor(R.color.custom_red));
                        break;
                    }
                    case R.id.radioButtonIncome: {
                        etSum.setTextColor(getResources().getColor(R.color.custom_green));
                        break;
                    }
                }
            }
        });
    }

    public void addTransaction() {

        String sum = DoubleFormatUtils.prepareStringToParse(etSum.getText().toString());

        if (! sum.matches(".*\\d.*") || Double.parseDouble(sum) == 0) {
            ShakeEditText.highlightEditText(etSum);
            ToastUtils.showClosableToast(getActivity(), getString(R.string.empty_amount_field), 1);
        }

        else {

            RadioButton rbCost = (RadioButton) view.findViewById(R.id.radioButtonCost);

            Long date = DateFormatUtils.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();

            double amount = Double.parseDouble(sum);
            if (rbCost.isChecked()) {
                amount *= -1;}


            Account account = (Account) spinAccount.getSelectedItem();

            double accountAmount = account.getAmount();

            if (mode == 1) {
                accountAmount -= transFromIntent.getAmount();
            }


            if (rbCost.isChecked() && Math.abs(amount) > accountAmount) {
            ToastUtils.showClosableToast(getActivity(), getString(R.string.not_enough_costs), 1);}
            else {
                accountAmount += amount;

                int category = spinCategory.getSelectedItemPosition();
                int idAccount = account.getId();


                if (mode == 1) {
                    int idTrans = transFromIntent.getId();
                    Transaction transaction = new Transaction(date, amount, category, idAccount, accountAmount, idTrans);
                    InfoFromDB.getInstance().getDataSource().editTransaction(transaction);
                }

                else {
                    Transaction transaction = new Transaction(date, amount, category, idAccount, accountAmount);
                    InfoFromDB.getInstance().getDataSource().insertNewTransaction(transaction);
                }

                InfoFromDB.getInstance().updateAccountList();

                pushBroadcast();

                finish();
            }
        }
    }

    private void setDateTimeField() {
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();

        if (mode == 1) {

            newCalendar.setTime(new Date(transFromIntent.getDate()));
        }

        tvDate.setText(DateFormatUtils.dateToString(newCalendar.getTime(), DATEFORMAT));

        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                if (newDate.getTimeInMillis() > System.currentTimeMillis()) {
                    ToastUtils.showClosableToast(getActivity(), getString(R.string.transaction_date_future), 1);

                } else {

                    tvDate.setText(DateFormatUtils.dateToString(newDate.getTime(), DATEFORMAT));
                }
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgHome.BROADCAST_FRG_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgHome.PARAM_STATUS_FRG_MAIN, FrgHome.STATUS_UPDATE_FRG_MAIN);
        getActivity().sendBroadcast(intentFragmentMain);

        Intent intentFragmentTransaction = new Intent(FrgTransactions.BROADCAST_FRG_TRANSACTION_ACTION);
        intentFragmentTransaction.putExtra(FrgTransactions.PARAM_STATUS_FRG_TRANSACTION, FrgTransactions.STATUS_UPDATE_FRG_TRANSACTION);
        getActivity().sendBroadcast(intentFragmentTransaction);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        getActivity().sendBroadcast(intentFrgAccounts);
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

                    addTransaction();
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

    private void showDialogNoAccount() {

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(getString(R.string.dialog_text_transaction_no_account))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.return_to_main))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addAccount();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        finish();
                    }
                })
                .cancelable(false)
                .show();
    }

    private void addAccount() {
        FrgAddAccount frgAddAccount = new FrgAddAccount();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        frgAddAccount.setArguments(arguments);

        addFragment(frgAddAccount);
    }

}
