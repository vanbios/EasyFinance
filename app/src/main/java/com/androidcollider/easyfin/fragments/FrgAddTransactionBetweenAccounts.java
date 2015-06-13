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

    private TextView tvDate;
    private DatePickerDialog datePickerDialog;
    private Spinner spinAccount1, spinAccount2;


    private View view;
    private DataSource dataSource;

    private List<Account> accountList1 = null;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_transaction_between_accounts, container, false);

        tvDate = (TextView) view.findViewById(R.id.tvTransactionBTWDate);

        setDateTimeField();

        dataSource = new DataSource(getActivity());

        setSpinners();


        return view;
    }

    private void setSpinners() {
        setSpinnerAccount1();
        setSpinnerAccount2();
    }



    private void setSpinnerAccount1() {
        spinAccount1 = (Spinner) view.findViewById(R.id.spinAddTransBTWAccount1);

        List<String> accounts1 = new ArrayList<>();

        accountList1 = dataSource.getAllAccountsInfo();

        for (Account a : accountList1) {
            accounts1.add(a.getName());
        }

        spinAccount1.setAdapter(new SpinnerAccountForTransAdapter(getActivity(), R.layout.spin_custom_item,
                accounts1, accountList1));

        spinAccount1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerAccount2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setSpinnerAccount2() {

        spinAccount2 = (Spinner) view.findViewById(R.id.spinAddTransBTWAccount2);
        spinAccount2.setEnabled(false);
        spinAccount2.setAdapter(null);

        if (accountList1.size() >= 2) {
            int pos = spinAccount1.getSelectedItemPosition();

            String account_name_1 = accountList1.get(pos).getName();
            String currency = accountList1.get(pos).getCurrency();


            List<Account> accountForTransferList = new ArrayList<>();
            List<String> accountsAvailable = new ArrayList<>();


            for (Account account : accountList1) {
                if (! account.getName().equals(account_name_1) && account.getCurrency().equals(currency)) {
                    accountForTransferList.add(account);
                    accountsAvailable.add(account.getName());
                }
            }

            if (accountForTransferList.size() >= 1) {
                spinAccount2.setEnabled(true);

                spinAccount2.setAdapter(new SpinnerAccountForTransAdapter(getActivity(), R.layout.spin_custom_item,
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

        if (! spinAccount2.isEnabled()) {
            showNoAvailableAccountsDialog();
        }
        else {

            EditText editSum = (EditText) view.findViewById(R.id.editTextTransBTWSum);

            String sum = editSum.getText().toString();

            if (!sum.matches(".*\\d.*") || Double.parseDouble(sum) == 0) {
                Shake.highlightEditText(editSum);
                Toast.makeText(getActivity(), getResources().getString(R.string.transaction_empty_amount_field), Toast.LENGTH_LONG).show();

            } else {

                double amount = Double.parseDouble(sum);

                int pos1 = spinAccount1.getSelectedItemPosition();

                double account_amount_1 = accountList1.get(pos1).getAmount();

                if (amount > account_amount_1) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.transaction_not_enough_costs) + " " +
                            Math.abs(amount), Toast.LENGTH_LONG).show();
                } else {

                    String account_name_1 = spinAccount1.getSelectedItem().toString();
                    String account_name_2 = spinAccount2.getSelectedItem().toString();
                    String currency = accountList1.get(pos1).getCurrency();
                    String account_type_1 = accountList1.get(pos1).getType();

                    Long date = DateFormat.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();

                    int account_id_1 = accountList1.get(pos1).getId();

                    String account_type_2 = null;
                    int account_id_2 = 0;
                    double account_amount_2 = 0;

                    for (Account account2 : accountList1) {
                        if (account2.getName().equals(account_name_2)) {
                            account_type_2 = account2.getType();
                            account_id_2 = account2.getId();
                            account_amount_2 = account2.getAmount();
                        }
                        else {account_type_2 = "";}
                    }

                    account_amount_1 -= amount;
                    account_amount_2 += amount;

                    dataSource.updateAccountAmountAfterTransaction(account_id_1, account_amount_1);
                    dataSource.updateAccountAmountAfterTransaction(account_id_2, account_amount_2);

                    TransBTWAccounts transBTWAccounts = new TransBTWAccounts(account_name_1,
                            account_name_2, amount, currency, date, account_type_1, account_type_2);


                    pushBroadcast();

                    getActivity().finish();

                }
            }
        }




    }




    private void setDateTimeField() {
        tvDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        tvDate.setText(DateFormat.dateToString(newCalendar.getTime(), DATEFORMAT));

        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvDate.setText(DateFormat.dateToString(newDate.getTime(), DATEFORMAT));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvTransactionBTWDate: datePickerDialog.show(); break;
        }
    }
}
