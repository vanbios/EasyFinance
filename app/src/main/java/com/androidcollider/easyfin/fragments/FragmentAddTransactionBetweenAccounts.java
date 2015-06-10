package com.androidcollider.easyfin.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.AddExpenseActivity;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinnerAddTransExpenseAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.DateFormat;

import java.util.Calendar;
import java.util.List;

public class FragmentAddTransactionBetweenAccounts extends Fragment implements View.OnClickListener {


    private final String DATEFORMAT = "dd-MM-yyyy";

    private TextView tvTransactionBTWDate;
    private DatePickerDialog datePickerDialogBTW;
    private Spinner spinAddTransBTWExpense1, spinAddTransBTWExpense2;


    private View view;
    private DataSource dataSource;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_transaction_between_accounts, null);

        tvTransactionBTWDate = (TextView) view.findViewById(R.id.tvTransactionBTWDate);

        setDateTimeField();

        dataSource = new DataSource(getActivity());

        setSpinner();


        return view;
    }



    private void setSpinner() {
        spinAddTransBTWExpense1 = (Spinner) view.findViewById(R.id.spinAddTransBTWExpense1);
        spinAddTransBTWExpense2 = (Spinner) view.findViewById(R.id.spinAddTransBTWExpense2);

        List<String> accounts = dataSource.getAllAccountNames();

        if (accounts.size() == 0) {
            showDialogNoExpense();
        }

        List<Account> accountList = dataSource.getAllAccountsInfo();


        spinAddTransBTWExpense1.setAdapter(new SpinnerAddTransExpenseAdapter(getActivity(), R.layout.spinner_item,
                accounts, accountList));



        spinAddTransBTWExpense2.setAdapter(new SpinnerAddTransExpenseAdapter(getActivity(), R.layout.spinner_item,
                accounts, accountList));
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


    private void closeActivity() {
        getActivity().finish();
    }

    private void openAddExpenseActivity() {
        Intent intent = new Intent(getActivity(), AddExpenseActivity.class);
        startActivity(intent);
    }



    private void setDateTimeField() {
        tvTransactionBTWDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        tvTransactionBTWDate.setText(DateFormat.dateToString(newCalendar.getTime(), DATEFORMAT));

        datePickerDialogBTW = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvTransactionBTWDate.setText(DateFormat.dateToString(newDate.getTime(), DATEFORMAT));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvTransactionBTWDate: datePickerDialogBTW.show(); break;
        }
    }
}
