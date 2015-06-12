package com.androidcollider.easyfin.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinnerAccountForTransAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.TransBTWAccounts;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.Shake;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FrgAddTransactionBetweenAccounts extends Fragment implements View.OnClickListener {


    private final String DATEFORMAT = "dd.MM.yyyy";

    private TextView tvTransactionBTWDate;
    private DatePickerDialog datePickerDialogBTW;
    private Spinner spinAddTransBTWExpense1, spinAddTransBTWExpense2;


    private View view;
    private DataSource dataSource;

    private List<Account> accountList1;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_transaction_between_accounts, null);

        tvTransactionBTWDate = (TextView) view.findViewById(R.id.tvTransactionBTWDate);

        setDateTimeField();

        dataSource = new DataSource(getActivity());

        setSpinners();


        return view;
    }

    private void setSpinners() {
        setSpinnerExpense1();
        setSpinnerExpense2();
    }



    private void setSpinnerExpense1() {
        spinAddTransBTWExpense1 = (Spinner) view.findViewById(R.id.spinAddTransBTWAccount1);

        List<String> accounts1 = new ArrayList<>();

        accountList1 = dataSource.getAllAccountsInfo();

        for (Account acc : accountList1) {
            accounts1.add(acc.getName());
        }

        spinAddTransBTWExpense1.setAdapter(new SpinnerAccountForTransAdapter(getActivity(), R.layout.spin_custom_item,
                accounts1, accountList1));

        spinAddTransBTWExpense1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerExpense2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setSpinnerExpense2() {

        spinAddTransBTWExpense2 = (Spinner) view.findViewById(R.id.spinAddTransBTWAccount2);
        spinAddTransBTWExpense2.setEnabled(false);
        spinAddTransBTWExpense2.setAdapter(null);

        if (accountList1.size() >= 2) {
            int pos = spinAddTransBTWExpense1.getSelectedItemPosition();

            String expense_first_name = accountList1.get(pos).getName();
            String expense_currency = accountList1.get(pos).getCurrency();


            List<Account> accountForTransferList = new ArrayList<>();
            List<String> accountsAvailable = new ArrayList<>();


            for (Account account : accountList1) {
                if (! account.getName().equals(expense_first_name) && account.getCurrency().equals(expense_currency)) {
                    accountForTransferList.add(account);
                    accountsAvailable.add(account.getName());
                }
            }

            if (accountForTransferList.size() >= 1) {
                spinAddTransBTWExpense2.setEnabled(true);

                spinAddTransBTWExpense2.setAdapter(new SpinnerAccountForTransAdapter(getActivity(), R.layout.spin_custom_item,
                        accountsAvailable, accountForTransferList));
            }
            else {
                showNoAvailableAccountsDialog();
            }
        }


    }


    private void showNoAvailableAccountsDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(getActivity().getResources().getString(R.string.dialog_title_no_available_account))
                .content(getActivity().getResources().getString(R.string.dialog_text_no_available_account))
                .positiveText(getActivity().getResources().getString(R.string.get_it))
                .show();
    }


    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgMain.PARAM_STATUS_FRAGMENT_MAIN, FrgMain.STATUS_UPDATE_FRAGMENT_MAIN);
        getActivity().sendBroadcast(intentFragmentMain);

        Intent intentFragmentTransaction = new Intent(FrgTransactions.BROADCAST_FRAGMENT_TRANSACTION_ACTION);
        intentFragmentTransaction.putExtra(FrgTransactions.PARAM_STATUS_FRAGMENT_TRANSACTION, FrgTransactions.STATUS_UPDATE_FRAGMENT_TRANSACTION);
        getActivity().sendBroadcast(intentFragmentTransaction);
    }


    public void addTransactionBTW() {

        if (! spinAddTransBTWExpense2.isEnabled()) {
            showNoAvailableAccountsDialog();
        }
        else {

            EditText editTextTransBTWSum = (EditText) view.findViewById(R.id.editTextTransBTWSum);

            String sum = editTextTransBTWSum.getText().toString();

            if (!sum.matches(".*\\d.*") || Double.parseDouble(sum) == 0) {
                Shake.highlightEditText(editTextTransBTWSum);
                Toast.makeText(getActivity(), getResources().getString(R.string.transaction_empty_amount_field), Toast.LENGTH_LONG).show();

            } else {

                double amount = Double.parseDouble(sum);

                int pos1 = spinAddTransBTWExpense1.getSelectedItemPosition();

                double expense_amount_1 = accountList1.get(pos1).getAmount();

                if (amount > expense_amount_1) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.transaction_not_enough_costs) + " " +
                            Math.abs(amount), Toast.LENGTH_LONG).show();
                } else {

                    String expense_name_1 = spinAddTransBTWExpense1.getSelectedItem().toString();
                    String expense_name_2 = spinAddTransBTWExpense2.getSelectedItem().toString();
                    String expense_currency = accountList1.get(pos1).getCurrency();
                    String expense_type_1 = accountList1.get(pos1).getType();

                    Long date = DateFormat.stringToDate(tvTransactionBTWDate.getText().toString(), DATEFORMAT).getTime();

                    int expense_id_1 = accountList1.get(pos1).getId();

                    String expense_type_2 = null;
                    int expense_id_2 = 0;
                    double expense_amount_2 = 0;

                    for (Account account2 : accountList1) {
                        if (account2.getName().equals(expense_name_2)) {
                            expense_type_2 = account2.getType();
                            expense_id_2 = account2.getId();
                            expense_amount_2 = account2.getAmount();
                        }
                        else {expense_type_2 = "";}
                    }

                    expense_amount_1 -= amount;
                    expense_amount_2 += amount;

                    dataSource.updateAccountAmountAfterTransaction(expense_id_1, expense_amount_1);
                    dataSource.updateAccountAmountAfterTransaction(expense_id_2, expense_amount_2);

                    TransBTWAccounts transBTWAccounts = new TransBTWAccounts(expense_name_1,
                            expense_name_2, amount, expense_currency, date, expense_type_1, expense_type_2);


                    pushBroadcast();

                    closeActivity();

                }
            }
        }




    }










    private void closeActivity() {
        getActivity().finish();
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
