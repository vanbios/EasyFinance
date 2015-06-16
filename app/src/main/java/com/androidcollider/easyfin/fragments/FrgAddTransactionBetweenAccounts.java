package com.androidcollider.easyfin.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinnerAccountForTransAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.Shake;

import java.util.ArrayList;
import java.util.List;

public class FrgAddTransactionBetweenAccounts extends Fragment {

    private Spinner spinAccountFrom, spinAccountTo;

    private View view;
    private DataSource dataSource;

    private List<Account> accountListFrom = null;
    private List<Account> accountListTo = null;

    int spin2SetCount = 0;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_transaction_between_accounts, container, false);

        dataSource = new DataSource(getActivity());

        setSpinnerFrom();

        return view;
    }




    private void setSpinnerFrom() {
        spinAccountFrom = (Spinner) view.findViewById(R.id.spinAddTransBTWAccountFrom);

        accountListFrom = dataSource.getAllAccountsInfo();
        accountListTo = new ArrayList<>();

        spinAccountFrom.setAdapter(new SpinnerAccountForTransAdapter(getActivity(),
                R.layout.spin_custom_item, accountListFrom));

        setSpinnerTo();


        spinAccountFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!setSpinnerTo() && spin2SetCount > 2) {
                    showNoAvailableAccountsDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private boolean setSpinnerTo() {

        spinAccountTo = (Spinner) view.findViewById(R.id.spinAddTransBTWAccountTo);
        spinAccountTo.setEnabled(false);
        spinAccountTo.setAdapter(null);
        spin2SetCount++;


            Account accountFrom = (Account) spinAccountFrom.getSelectedItem();

            int idAccountFrom = accountFrom.getId();
            String currency = accountFrom.getCurrency();


            accountListTo.clear();

            for (Account account : accountListFrom) {
                if (account.getId() != idAccountFrom && account.getCurrency().equals(currency)) {
                    accountListTo.add(account);
                }
            }

            if (accountListTo.size() > 0) {
                spinAccountTo.setEnabled(true);

                spinAccountTo.setAdapter(new SpinnerAccountForTransAdapter(getActivity(),
                        R.layout.spin_custom_item, accountListTo));
            }

            else {
                return false;
            }

        return true;
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
    }


    public void addTransactionBTW() {

         if (!spinAccountTo.isEnabled()) {
                showNoAvailableAccountsDialog();

            } else {

                EditText etSum = (EditText) view.findViewById(R.id.editTextTransBTWSum);

                String sum = etSum.getText().toString();

                if (!sum.matches(".*\\d.*") || Double.parseDouble(sum) == 0) {
                    Shake.highlightEditText(etSum);
                    Toast.makeText(getActivity(), getResources().getString(R.string.transaction_empty_amount_field), Toast.LENGTH_LONG).show();

                } else {

                    double amount = Double.parseDouble(sum);

                    Account accountFrom = (Account) spinAccountFrom.getSelectedItem();
                    double accountAmountFrom = accountFrom.getAmount();

                    if (amount > accountAmountFrom) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.transaction_not_enough_costs) + " " +
                                Math.abs(amount), Toast.LENGTH_LONG).show();
                    } else {

                        int accountIdFrom = accountFrom.getId();

                        Account accountTo = (Account) spinAccountTo.getSelectedItem();

                        int accountIdTo = accountTo.getId();
                        double accountAmountTo = accountTo.getAmount();


                        accountAmountFrom -= amount;
                        accountAmountTo += amount;

                        dataSource.updateAccountsAmountAfterTransfer(accountIdFrom, accountAmountFrom, accountIdTo, accountAmountTo);


                        pushBroadcast();

                        getActivity().finish();
                        }
                    }
                }
            }
}
