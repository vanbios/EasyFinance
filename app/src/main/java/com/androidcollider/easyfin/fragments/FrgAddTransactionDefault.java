package com.androidcollider.easyfin.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.ActTransaction;
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


public class FrgAddTransactionDefault extends Fragment implements View.OnClickListener {

    private TextView tvDate;
    private DatePickerDialog datePickerDialog;
    private Spinner spinCategory, spinAccount;
    private EditText etSum;

    private final String DATEFORMAT = "dd.MM.yyyy";

    private View view;

    private ArrayList<Account> accountList = null;

    private final int mode = ActTransaction.intent.getIntExtra("mode", 0);

    private Transaction transFromIntent;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_transaction_default, container, false);

        etSum = (EditText) view.findViewById(R.id.editTextTransSum);
        etSum.setTextColor(getResources().getColor(R.color.custom_red));

        etSum.addTextChangedListener(new EditTextAmountWatcher(etSum));

        setRadioGroupEvents();

        if (mode == 1) {
            transFromIntent = (Transaction) ActTransaction.intent.getSerializableExtra("transaction");

            final int PRECISE = 100;
            final String FORMAT = "0.00";

            double amount = transFromIntent.getAmount();
            etSum.setText(DoubleFormatUtils.doubleToStringFormatter(Math.abs(amount), FORMAT, PRECISE));

            if (!DoubleFormatUtils.isDoubleNegative(amount)) {
                RadioButton rbPlus = (RadioButton) view.findViewById(R.id.radioButtonIncome);
                rbPlus.setChecked(true);
            }
        }

        accountList = InfoFromDB.getInstance().getAccountList();

        tvDate = (TextView) view.findViewById(R.id.tvTransactionDate);

        setDateTimeField();

        setSpinner();

        HideKeyboardUtils.setupUI(view.findViewById(R.id.scrollAddTransDef), getActivity());

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
            //String category = transFromIntent.getCategory();

            for (int i = 0; i < accountList.size(); i++) {

                Account account = accountList.get(i);

                if (account.getName().equals(accountName)) {
                    spinAccount.setSelection(i);
                    break;
                }
            }
            spinAccount.setEnabled(false);

            /*for (int j = 0; j < categoryArray.length; j++) {

                if (categoryArray[j].equals(category)) {
                    spinCategory.setSelection(j);
                    break;
                }
            }*/

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
            //Toast.makeText(getActivity(), getResources().getString(R.string.empty_amount_field), Toast.LENGTH_SHORT).show();
            ToastUtils.showClosableToast(getActivity(),
                    getResources().getString(R.string.empty_amount_field), 1);
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
                /*Toast.makeText(getActivity(),
                        getResources().getString(R.string.not_enough_costs), Toast.LENGTH_SHORT).show();*/
            ToastUtils.showClosableToast(getActivity(),
                    getResources().getString(R.string.not_enough_costs), 1);}
            else {
                accountAmount += amount;

                //String category = spinCategory.getSelectedItem().toString();
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

                getActivity().finish();
            }
        }
    }

    private void setDateTimeField() {
        tvDate.setOnClickListener(this);

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
                    //Toast.makeText(getActivity(), R.string.transaction_date_future, Toast.LENGTH_SHORT).show();
                    ToastUtils.showClosableToast(getActivity(),
                            getResources().getString(R.string.transaction_date_future), 1);

                } else {

                    tvDate.setText(DateFormatUtils.dateToString(newDate.getTime(), DATEFORMAT));
                }
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvTransactionDate: {datePickerDialog.show(); break;}
        }
    }

    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgMain.BROADCAST_FRG_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgMain.PARAM_STATUS_FRG_MAIN, FrgMain.STATUS_UPDATE_FRG_MAIN);
        getActivity().sendBroadcast(intentFragmentMain);

        Intent intentFragmentTransaction = new Intent(FrgTransactions.BROADCAST_FRG_TRANSACTION_ACTION);
        intentFragmentTransaction.putExtra(FrgTransactions.PARAM_STATUS_FRG_TRANSACTION, FrgTransactions.STATUS_UPDATE_FRG_TRANSACTION);
        getActivity().sendBroadcast(intentFragmentTransaction);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        getActivity().sendBroadcast(intentFrgAccounts);
    }

}
