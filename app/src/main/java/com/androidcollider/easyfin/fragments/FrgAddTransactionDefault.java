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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinnerAccountForTransAdapter;
import com.androidcollider.easyfin.adapters.SpinnerTransCategoriesAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.Shake;

import java.util.Calendar;
import java.util.List;



public class FrgAddTransactionDefault extends Fragment implements View.OnClickListener {

    private TextView tvTransactionDate;
    private DatePickerDialog setDatePickerDialog;
    private Spinner spinAddTransCategory, spinAddTransExpense;

    private final String DATEFORMAT = "dd.MM.yyyy";

    private View view;
    private DataSource dataSource;

    private List<Account> accountList = null;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_transaction_default, null);

        tvTransactionDate = (TextView) view.findViewById(R.id.tvTransactionDate);

        setDateTimeField();

        dataSource = new DataSource(getActivity());

        setSpinner();



        return view;
    }




    private void setSpinner() {
        spinAddTransCategory = (Spinner) view.findViewById(R.id.spinAddTransCategory);
        spinAddTransExpense = (Spinner) view.findViewById(R.id.spinAddTransExpense);

        List<String> accounts = dataSource.getAllAccountNames();



        spinAddTransCategory.setAdapter(new SpinnerTransCategoriesAdapter(getActivity(), R.layout.spin_custom_item,
                getResources().getStringArray(R.array.cat_transaction_array)));

        accountList = dataSource.getAllAccountsInfo();

        spinAddTransExpense.setAdapter(new SpinnerAccountForTransAdapter(getActivity(), R.layout.spin_custom_item,
                accounts, accountList));
    }

    private void setDateTimeField() {
        tvTransactionDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        tvTransactionDate.setText(DateFormat.dateToString(newCalendar.getTime(), DATEFORMAT));

        setDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvTransactionDate.setText(DateFormat.dateToString(newDate.getTime(), DATEFORMAT));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvTransactionDate: setDatePickerDialog.show(); break;
        }
    }


    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgMain.PARAM_STATUS_FRAGMENT_MAIN, FrgMain.STATUS_UPDATE_FRAGMENT_MAIN);
        getActivity().sendBroadcast(intentFragmentMain);

        Intent intentFragmentTransaction = new Intent(FrgTransactions.BROADCAST_FRAGMENT_TRANSACTION_ACTION);
        intentFragmentTransaction.putExtra(FrgTransactions.PARAM_STATUS_FRAGMENT_TRANSACTION, FrgTransactions.STATUS_UPDATE_FRAGMENT_TRANSACTION);
        getActivity().sendBroadcast(intentFragmentTransaction);
    }

    private void closeActivity() {
        getActivity().finish();
    }



    public void addTransaction() {
        EditText editTextTransSum = (EditText) view.findViewById(R.id.editTextTransSum);
        RadioButton radioButtonCost = (RadioButton) view.findViewById(R.id.radioButtonCost);

        String sum = editTextTransSum.getText().toString();

        if (! sum.matches(".*\\d.*") || Double.parseDouble(sum) == 0) {
            Shake.highlightEditText(editTextTransSum);
            Toast.makeText(getActivity(), getResources().getString(R.string.transaction_empty_amount_field), Toast.LENGTH_LONG).show();
        }

        else {

            Long date = DateFormat.stringToDate(tvTransactionDate.getText().toString(), DATEFORMAT).getTime();
            //String account_name = spinAddTransExpense.getSelectedItem().toString();

            int pos = spinAddTransExpense.getSelectedItemPosition();

            double amount = Double.parseDouble(sum);
            if (radioButtonCost.isChecked()) {
                amount *= -1;}


            //double accountAmount = dataSource.getAccountAmountForTransaction(id_account);
            double accountAmount = accountList.get(pos).getAmount();

            if (radioButtonCost.isChecked() && Math.abs(amount) > accountAmount) {
                Toast.makeText(getActivity(), getResources().getString(R.string.transaction_not_enough_costs) + " " +
                        Math.abs(amount), Toast.LENGTH_LONG).show();}
            else {
                accountAmount += amount;

                String account_name = accountList.get(pos).getName();
                String category = spinAddTransCategory.getSelectedItem().toString();
                //int id_account = dataSource.getAccountIdByName(account_name);
                int id_account = accountList.get(pos).getId();
                //String account_currency = dataSource.getAccountCurrencyByName(account_name);
                String account_currency = accountList.get(pos).getCurrency();
                //String account_type = dataSource.getAccountTypeByName(account_name);
                String account_type = accountList.get(pos).getType();

                Transaction transaction = new Transaction(date, amount, category, account_name, account_currency, account_type, id_account);
                dataSource.insertNewTransaction(transaction);
                dataSource.updateAccountAmountAfterTransaction(id_account, accountAmount);


                pushBroadcast();

                closeActivity();
            }
        }
    }

}
