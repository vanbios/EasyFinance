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

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinnerAccountForTransAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.Shake;

import java.util.ArrayList;



public class FrgAddTransactionBetweenAccounts extends Fragment {

    private Spinner spinAccountFrom, spinAccountTo;

    private SpinnerAccountForTransAdapter adapterAccountTo;

    private View view;

    private ArrayList<Account> accountListFrom = null;
    private ArrayList<Account> accountListTo = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_transaction_between_accounts, container, false);

        setSpinners();

        return view;
    }



    private void setSpinners() {
        spinAccountFrom = (Spinner) view.findViewById(R.id.spinAddTransBTWAccountFrom);
        spinAccountTo = (Spinner) view.findViewById(R.id.spinAddTransBTWAccountTo);

        accountListFrom = InfoFromDB.getInstance().getAccountList();
        accountListTo = new ArrayList<>();

        spinAccountFrom.setAdapter(new SpinnerAccountForTransAdapter(getActivity(),
                R.layout.spin_custom_item, accountListFrom));


        accountListTo.addAll(accountListFrom);
        accountListTo.remove(spinAccountFrom.getSelectedItemPosition());

        adapterAccountTo = new SpinnerAccountForTransAdapter(getActivity(),
                R.layout.spin_custom_item, accountListTo);

        spinAccountTo.setAdapter(adapterAccountTo);


        spinAccountFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSpinnerTo();
                setCurrencyMode(checkForMultiCurrency());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinAccountTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setCurrencyMode(checkForMultiCurrency());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void updateSpinnerTo() {
        accountListTo.clear();
        accountListTo.addAll(accountListFrom);
        accountListTo.remove(spinAccountFrom.getSelectedItemPosition());

        adapterAccountTo.notifyDataSetChanged();
    }


    private void setCurrencyMode(boolean mode) {
        if (mode) {
            //   enable stuff for multiple transfer
        }
        else {
            //   disable stuff for multiple transfer
            }
    }


    private boolean checkForMultiCurrency() {
        Account accountFrom = (Account) spinAccountFrom.getSelectedItem();
        Account accountTo = (Account) spinAccountTo.getSelectedItem();

        return !accountFrom.getCurrency().equals(accountTo.getCurrency());
    }



    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgMain.PARAM_STATUS_FRAGMENT_MAIN, FrgMain.STATUS_UPDATE_FRAGMENT_MAIN);
        getActivity().sendBroadcast(intentFragmentMain);
    }


    public void addTransactionBTW() {

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

                        new DataSource(getActivity()).updateAccountsAmountAfterTransfer(accountIdFrom,
                                accountAmountFrom, accountIdTo, accountAmountTo);

                        InfoFromDB.getInstance().updateAccountList();


                        pushBroadcast();

                        getActivity().finish();
                    }
                }
            }
}
