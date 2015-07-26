package com.androidcollider.easyfin.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.TypedArray;
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
import android.widget.DatePicker;
import android.widget.ScrollView;
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
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class FrgAddTransactionDefault extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    private TextView tvDate, tvAmount;
    private DatePickerDialog datePickerDialog;
    private Spinner spinCategory, spinAccount;

    private final String DATEFORMAT = "dd MMMM yyyy";

    private View view;

    private ArrayList<Account> accountList = null;

    private int mode, transType;

    private Transaction transFromIntent;

    private final String prefixExpense = "- ", prefixIncome = "+ ";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frg_add_trans_def, container, false);

        setToolbar();

        accountList = InfoFromDB.getInstance().getAccountList();

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollAddTransDef);

        if (accountList.isEmpty()) {
            scrollView.setVisibility(View.GONE);
            showDialogNoAccount();
        }

        else {

            scrollView.setVisibility(View.VISIBLE);

            mode = getArguments().getInt("mode", 0);

            tvAmount = (TextView) view.findViewById(R.id.tvAddTransDefAmount);
            tvAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openNumericDialog();
                }
            });


            switch (mode) {

                case 0: {

                    transType = getArguments().getInt("type", 0);

                    switch (transType) {

                        case 0: {
                            tvAmount.setText(prefixExpense + "0,00");
                            break;
                        }

                        case 1: {
                            tvAmount.setText(prefixIncome + "0,00");
                            break;
                        }
                    }

                    openNumericDialog();

                    break;
                }

                case 1: {

                    transFromIntent = (Transaction) getArguments().getSerializable("transaction");

                    final int PRECISE = 100;
                    final String FORMAT = "###,##0.00";

                    double amount = transFromIntent.getAmount();

                    if (!DoubleFormatUtils.isDoubleNegative(amount)) {

                        transType = 1;
                        String amountS = DoubleFormatUtils.doubleToStringFormatterForEdit(amount, FORMAT, PRECISE);
                        setTVTextSize(amountS);
                        tvAmount.setText(prefixIncome + amountS);
                    }

                    else {

                        transType = 0;
                        String amountS = DoubleFormatUtils.doubleToStringFormatterForEdit(Math.abs(amount), FORMAT, PRECISE);
                        setTVTextSize(amountS);
                        tvAmount.setText(prefixExpense + amountS);
                    }

                    break;
                }
            }


            tvDate = (TextView) view.findViewById(R.id.tvTransactionDate);

            setDateTimeField();

            setSpinner();


            switch (transType) {
                case 0: {
                    tvAmount.setTextColor(getResources().getColor(R.color.custom_red));
                    break;
                }
                case 1: {
                    tvAmount.setTextColor(getResources().getColor(R.color.custom_green));
                    break;
                }
            }
        }

        return view;
    }

    private void setSpinner() {

        spinCategory = (Spinner) view.findViewById(R.id.spinAddTransCategory);
        spinAccount = (Spinner) view.findViewById(R.id.spinAddTransDefAccount);

        String[] categoryArray;
        TypedArray categoryIcons;


        switch (transType) {

            case 1: {
                categoryArray = getResources().getStringArray(R.array.transaction_category_income_array);
                categoryIcons = getResources().obtainTypedArray(R.array.transaction_category_income_icons);
                break;
            }

            default: {
                categoryArray = getResources().getStringArray(R.array.transaction_category_expense_array);
                categoryIcons = getResources().obtainTypedArray(R.array.transaction_category_expense_icons);
                break;
            }
        }

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

            spinCategory.setSelection(transFromIntent.getCategory());
        }
    }

    public void addTransaction() {

        String sum = DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString());

        if (checkSumField(sum)) {

            double amount = Double.parseDouble(sum);

            boolean isExpense = transType == 0;

            if (isExpense) {
                amount *= -1;
            }


            Account account = (Account) spinAccount.getSelectedItem();

            double accountAmount = account.getAmount();


            if (checkIsEnoughCosts(isExpense, amount, accountAmount)) {

                accountAmount += amount;

                int category = spinCategory.getSelectedItemPosition();
                Long date = DateFormatUtils.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();
                int idAccount = account.getId();


                Transaction transaction = new Transaction(date, amount, category, idAccount, accountAmount);
                InfoFromDB.getInstance().getDataSource().insertNewTransaction(transaction);

                lastActions();
            }
        }
    }

    private void editTransaction() {

        String sum = DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString());

        if (checkSumField(sum)) {

            double amount = Double.parseDouble(sum);

            boolean isExpense = transType == 0;

            if (isExpense) {
                amount *= -1;
            }


            Account account = (Account) spinAccount.getSelectedItem();
            double accountAmount = account.getAmount();
            int accountId = account.getId();

            int oldAccountId = transFromIntent.getIdAccount();

            boolean isAccountTheSame = accountId == oldAccountId;

            double oldAmount = transFromIntent.getAmount();

            double oldAccountAmount = 0;


            if (isAccountTheSame) {

                accountAmount -= oldAmount;
            }

            else {

                for (int i = 0; i < accountList.size(); i++) {

                    if (oldAccountId == accountList.get(i).getId()) {

                        oldAccountAmount = accountList.get(i).getAmount() - oldAmount;
                    }
                }
            }


            if (checkIsEnoughCosts(isExpense, amount, accountAmount)) {

                accountAmount += amount;

                int category = spinCategory.getSelectedItemPosition();
                Long date = DateFormatUtils.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();
                int idAccount = account.getId();


                int idTrans = transFromIntent.getId();
                Transaction transaction = new Transaction(date, amount, category, idAccount, accountAmount, idTrans);

                if (isAccountTheSame) {
                    InfoFromDB.getInstance().getDataSource().editTransaction(transaction);
                }

                else {

                    InfoFromDB.getInstance().getDataSource().editTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId);
                }

                lastActions();
            }
        }
    }

    private boolean checkSumField(String sum) {
        if (! sum.matches(".*\\d.*") || Double.parseDouble(sum) == 0) {
            ToastUtils.showClosableToast(getActivity(), getString(R.string.empty_amount_field), 1);
            return false;
        }
        return true;
    }

    private boolean checkIsEnoughCosts(boolean isExpense, double amount, double accountAmount) {
        if (isExpense && Math.abs(amount) > accountAmount) {
            ToastUtils.showClosableToast(getActivity(), getString(R.string.not_enough_costs), 1);
        return false;
        }
        return true;
    }

    private void lastActions() {
        InfoFromDB.getInstance().updateAccountList();
        pushBroadcast();
        finish();
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

                    switch (mode) {
                        case 0: {addTransaction(); break;}
                        case 1: {editTransaction(); break;}
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

    private void showDialogNoAccount() {

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(getString(R.string.dialog_text_transaction_no_account))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.close))
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

    private void openNumericDialog() {
        Bundle args = new Bundle();
        args.putString("value", tvAmount.getText().toString());

        DialogFragment numericDialog = new FrgNumericDialog();
        numericDialog.setTargetFragment(this, 2);
        numericDialog.setArguments(args);
        numericDialog.show(getActivity().getSupportFragmentManager(), "numericDialog2");
    }

    @Override
    public void onCommitAmountSubmit(String amount) {

        setTVTextSize(amount);

        switch (transType) {

            case 0: {
                tvAmount.setText(prefixExpense + amount);
                break;
            }
            case 1: {
                tvAmount.setText(prefixIncome + amount);
                break;
            }
        }
    }

    private void setTVTextSize(String s) {

        int length = s.length();

        if (length > 9 && length <= 14) {
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        }

        else if (length > 14) {
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        }

        else {
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        }
    }

}
