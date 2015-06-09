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

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.AddExpenseActivity;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinnerAddTransExpenseAdapter;
import com.androidcollider.easyfin.adapters.SpinnerCategoriesAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.Shake;

import java.util.Calendar;
import java.util.List;



public class FragmentAddTransactionDefault extends Fragment implements View.OnClickListener {

    private TextView tvTransactionDate;
    private DatePickerDialog setDatePickerDialog;
    private Spinner spinAddTransCategory, spinAddTransExpense;

    private final String DATEFORMAT = "dd-MM-yyyy";

    private static final String ARGUMENT_PAGE_NUMBER = "argument_page_number";
    int pageNumber;
    private View view;
    private DataSource dataSource;



    /*public static FragmentTransaction newInstance(int page) {
        FragmentTransaction fragmentTransaction = new FragmentTransaction();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        fragmentTransaction.setArguments(arguments);
        return fragmentTransaction;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_transaction_default, null);

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

        if (accounts.size() == 0) {
            showDialogNoExpense();
        }


        spinAddTransCategory.setAdapter(new SpinnerCategoriesAdapter(getActivity(), R.layout.spinner_item,
                getResources().getStringArray(R.array.cat_transaction_array)));

        List<Account> accountList = dataSource.getAllAccountsInfo();

        spinAddTransExpense.setAdapter(new SpinnerAddTransExpenseAdapter(getActivity(), R.layout.spinner_item,
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
        Intent intentFragmentMain = new Intent(FragmentMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FragmentMain.PARAM_STATUS_FRAGMENT_MAIN, FragmentMain.STATUS_UPDATE_FRAGMENT_MAIN);
        getActivity().sendBroadcast(intentFragmentMain);

        Intent intentFragmentTransaction = new Intent(FragmentTransaction.BROADCAST_FRAGMENT_TRANSACTION_ACTION);
        intentFragmentTransaction.putExtra(FragmentTransaction.PARAM_STATUS_FRAGMENT_TRANSACTION, FragmentTransaction.STATUS_UPDATE_FRAGMENT_TRANSACTION);
        getActivity().sendBroadcast(intentFragmentTransaction);
    }

    private void closeActivity() {
        getActivity().finish();
    }

    private void openAddExpenseActivity() {
        Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
        startActivity(intent);
    }

    private void showDialogNoExpense() {

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_expense))
                .content(getString(R.string.dialog_text_no_expense))
                .positiveText(getString(R.string.new_expense))
                .negativeText(getString(R.string.return_to_main))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        goToAddNewExpense();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        returnToMain();
                    }
                })
                .show();
    }

    public void returnToMain() {
        closeActivity();
    }

    public void goToAddNewExpense() {
        closeActivity();
        openAddExpenseActivity();
    }



    public void addTransaction() {
        EditText editTextTransSum = (EditText) view.findViewById(R.id.editTextTransSum);
        RadioButton radioButtonCost = (RadioButton) view.findViewById(R.id.radioButtonCost);

        if (! editTextTransSum.getText().toString().matches(".*\\d.*")) {
            Shake.highlightEditText(editTextTransSum);
            Toast.makeText(getActivity(), getResources().getString(R.string.transaction_empty_amount_field), Toast.LENGTH_LONG).show();
        }

        else {

            Long date = DateFormat.stringToDate(tvTransactionDate.getText().toString(), DATEFORMAT).getTime();
            String account_name = spinAddTransExpense.getSelectedItem().toString();
            double amount = Double.parseDouble(editTextTransSum.getText().toString());
            if (radioButtonCost.isChecked()) {
                amount *= -1;}
            String category = spinAddTransCategory.getSelectedItem().toString();

            int id_account = dataSource.getAccountIdByName(account_name);
            String account_currency = dataSource.getAccountCurrencyByName(account_name);

            double accountAmount = dataSource.getAccountAmountForTransaction(id_account);

            if (radioButtonCost.isChecked() && Math.abs(amount) > accountAmount) {
                Toast.makeText(getActivity(), getResources().getString(R.string.transaction_not_enough_costs) + " " +
                        Math.abs(amount), Toast.LENGTH_LONG).show();}
            else {
                accountAmount += amount;

                Transaction transaction = new Transaction(date, amount, category, account_name, account_currency, id_account);
                dataSource.insertNewTransaction(transaction);
                dataSource.updateAccountAmountAfterTransaction(id_account, accountAmount);


                pushBroadcast();

                closeActivity();
            }
        }
    }







}
