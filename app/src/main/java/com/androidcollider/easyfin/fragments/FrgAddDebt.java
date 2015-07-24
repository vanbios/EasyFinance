package com.androidcollider.easyfin.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.Debt;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.DateFormatUtils;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.EditTextAmountWatcher;
import com.androidcollider.easyfin.utils.HideKeyboardUtils;
import com.androidcollider.easyfin.utils.ShakeEditText;
import com.androidcollider.easyfin.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class FrgAddDebt extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    private View view;

    private DatePickerDialog datePickerDialog;

    private TextView tvDate, tvAmount;
    private EditText etName;
    //private RadioButton rbGive;
    private Spinner spinAccount;

    private final String DATEFORMAT = "dd MMMM yyyy";

    private ArrayList<Account> accountList = null;

    private int mode, debtType;

    private Debt debtFrIntent;

    private DialogFragment numericDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frg_add_debt_new, container, false);

        numericDialog = new FrgNumericDialog();
        numericDialog.setTargetFragment(this, 4);

        mode = getArguments().getInt("mode", 0);


        if (mode == 1) {
            debtFrIntent = (Debt) getArguments().getSerializable("debt");
        }

        else {
            debtType = getArguments().getInt("type", 0);
        }

        setToolbar();

        CardView cardView = (CardView) view.findViewById(R.id.cardAddDebtElements);

        accountList = InfoFromDB.getInstance().getAccountList();

        if (accountList.isEmpty()) {
            cardView.setVisibility(View.GONE);
            showDialogNoAccount();
        }

        else {

            cardView.setVisibility(View.VISIBLE);

            initializeFields();

            setDateTimeField();

            setSpinner();

            //setRadioGroupEvents();

            HideKeyboardUtils.setupUI(view.findViewById(R.id.layoutActAddDebtParent), getActivity());

            if (mode == 1) {
                setViewsToEdit();
            }

            switch (debtType) {
                case 0: {tvAmount.setTextColor(getResources().getColor(R.color.custom_green)); break;}
                case 1: {tvAmount.setTextColor(getResources().getColor(R.color.custom_red)); break;}
            }
        }

        return view;
    }


    private void initializeFields() {
        etName = (EditText) view.findViewById(R.id.editTextDebtName);
        //etSum = (EditText) view.findViewById(R.id.editTextDebtSum);
        //etSum.addTextChangedListener(new EditTextAmountWatcher(etSum));
        //etSum.setTextColor(getResources().getColor(R.color.custom_red));

        tvDate = (TextView) view.findViewById(R.id.tvAddDebtDate);

        tvAmount = (TextView) view.findViewById(R.id.tvAddDebtAmount);
        tvAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numericDialog.show(getActivity().getSupportFragmentManager(), "numericDialog4");
            }
        });

        //rbGive = (RadioButton) view.findViewById(R.id.radioButtonDebtGive);
    }

    private void setViewsToEdit() {

        etName.setText(debtFrIntent.getName());
        etName.setSelection(etName.getText().length());

        final int PRECISE = 100;
        final String FORMAT = "###,##0.00";

        tvAmount.setText(DoubleFormatUtils.doubleToStringFormatter(debtFrIntent.getAmountCurrent(), FORMAT, PRECISE));

        //etSum.setText(DoubleFormatUtils.doubleToStringFormatter(debtFrIntent.getAmountCurrent(), FORMAT, PRECISE));


        debtType = debtFrIntent.getType();

        /*if (type == 0) {
            rbGive.setChecked(true);
        }*/
    }

    /*private void setRadioGroupEvents() {
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupDebtType);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.radioButtonDebtTake: {
                        //etSum.setTextColor(getResources().getColor(R.color.custom_red));
                        break;
                    }
                    case R.id.radioButtonDebtGive: {
                        //etSum.setTextColor(getResources().getColor(R.color.custom_green));
                        break;
                    }
                }
            }
        });
    }*/

    private void setSpinner() {

        spinAccount = (Spinner) view.findViewById(R.id.spinAddDebtAccount);

        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountList
        ));


        if (mode == 1) {

            int idAccount = debtFrIntent.getIdAccount();
            int pos = 0;

            for (int i = 0; i < accountList.size(); i++) {

                if (idAccount == accountList.get(i).getId()) {
                    pos = i;
                    break;
                }
            }

            spinAccount.setSelection(pos);
        }
    }

    private void addDebt() {

        if (checkForFillNameSumFields()) {

            Account account = (Account) spinAccount.getSelectedItem();
            double accountAmount = account.getAmount();

            int type = debtType;

            /*if (rbGive.isChecked()) {
                type = 0;
            }*/

            double amount = Double.parseDouble(DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString()));


            if (type == 0 && Math.abs(amount) > accountAmount) {

                ToastUtils.showClosableToast(getActivity(), getString(R.string.not_enough_costs), 1);

            } else {

                String name = etName.getText().toString();
                int accountId = account.getId();
                Long date = DateFormatUtils.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();

                switch (type){
                    case 0: {accountAmount -= amount; break;}
                    case 1: {accountAmount += amount; break;}
                }

                Debt debt = new Debt(name, amount, type, accountId, date, accountAmount);

                InfoFromDB.getInstance().getDataSource().insertNewDebt(debt);

                InfoFromDB.getInstance().updateAccountList();

                pushBroadcast();

                this.finish();
            }
        }
    }

    private void editDebt() {

        if (checkForFillNameSumFields()) {

            Account account = (Account) spinAccount.getSelectedItem();
            double accountAmount = account.getAmount();

            int type = debtType;

            /*if (rbGive.isChecked()) {
                type = 0;
            }*/

            double amount = Double.parseDouble(DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString()));

            int accountId = account.getId();
            int oldAccountId = debtFrIntent.getIdAccount();


            boolean isAccountsTheSame = accountId == oldAccountId;


            double oldAmount = debtFrIntent.getAmountCurrent();
            double oldAccountAmount = 0;
            int oldType = debtFrIntent.getType();

            if (isAccountsTheSame) {

                switch (oldType) {
                    case 0: {accountAmount += oldAmount; break;}
                    case 1: {accountAmount -= oldAmount; break;}
                }
            }

            else {

                for (int i = 0; i < accountList.size(); i++) {

                    if (oldAccountId == accountList.get(i).getId()) {
                        oldAccountAmount = accountList.get(i).getAmount();
                        break;
                    }
                }

                switch (oldType) {
                    case 0: {oldAccountAmount += oldAmount; break;}
                    case 1: {oldAccountAmount -= oldAmount; break;}
                }
            }


            if (type == 0 && Math.abs(amount) > accountAmount) {

                ToastUtils.showClosableToast(getActivity(), getString(R.string.not_enough_costs), 1);

            } else {

                String name = etName.getText().toString();
                Long date = DateFormatUtils.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();

                switch (type){
                    case 0: {accountAmount -= amount; break;}
                    case 1: {accountAmount += amount; break;}
                }

                int idDebt = debtFrIntent.getId();

                Debt debt = new Debt(name, amount, type, accountId, date, accountAmount);

                if (isAccountsTheSame) {
                    InfoFromDB.getInstance().getDataSource().editDebt(debt, idDebt);
                }
                else {
                    InfoFromDB.getInstance().getDataSource().editDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId, idDebt);
                }

                InfoFromDB.getInstance().updateAccountList();

                pushBroadcast();

                this.finish();
            }
        }
    }

    private void pushBroadcast() {
        Intent intentFrgMain = new Intent(FrgHome.BROADCAST_FRG_MAIN_ACTION);
        intentFrgMain.putExtra(FrgHome.PARAM_STATUS_FRG_MAIN, FrgHome.STATUS_UPDATE_FRG_MAIN_BALANCE);
        getActivity().sendBroadcast(intentFrgMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        getActivity().sendBroadcast(intentFrgAccounts);

        Intent intentDebt = new Intent(FrgDebts.BROADCAST_DEBT_ACTION);
        intentDebt.putExtra(FrgDebts.PARAM_STATUS_DEBT, FrgDebts.STATUS_UPDATE_DEBT);
        getActivity().sendBroadcast(intentDebt);
    }

    private boolean checkForFillNameSumFields() {

        String st = etName.getText().toString().replaceAll("\\s+", "");

        if (st.isEmpty()) {
            ShakeEditText.highlightEditText(etName);
            ToastUtils.showClosableToast(getActivity(), getString(R.string.empty_name_field), 1);

            return false;
        }

        /*else {

            if (!DoubleFormatUtils.prepareStringToParse(etSum.getText().toString()).matches(".*\\d.*")) {
                ShakeEditText.highlightEditText(etSum);
                ToastUtils.showClosableToast(getActivity(), getString(R.string.empty_amount_field), 1);

                return false;
            }
        }*/

        return true;
    }

    private void setDateTimeField() {

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        final Calendar newCalendar = Calendar.getInstance();

        final long initTime = newCalendar.getTimeInMillis();

        if (mode == 1) {

            newCalendar.setTime(new Date(debtFrIntent.getDate()));
        }

        tvDate.setText(DateFormatUtils.dateToString(newCalendar.getTime(), DATEFORMAT));

        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (newDate.getTimeInMillis() < initTime) {
                    ToastUtils.showClosableToast(getActivity(), getString(R.string.debt_deadline_past), 1);

                } else {

                    tvDate.setText(DateFormatUtils.dateToString(newDate.getTime(), DATEFORMAT));
                }
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void showDialogNoAccount() {

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(getString(R.string.dialog_text_debt_no_account))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.close))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        goToAddNewAccount();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) { finish();}
                })
                .cancelable(false)
                .show();
    }

    private void goToAddNewAccount() {
        finish();
        FrgAddAccount frgAddAccount = new FrgAddAccount();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        frgAddAccount.setArguments(arguments);

        addFragment(frgAddAccount);
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
                        case 0: {addDebt(); break;}
                        case 1: {editDebt(); break;}
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


    @Override
    public void onCommitAmountSubmit(String amount) {

        //set amount to tvAmount
    }

}
