package com.androidcollider.easyfin.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.ExchangeUtils;
import com.androidcollider.easyfin.utils.FormatUtils;
import com.androidcollider.easyfin.utils.Shake;

import java.util.ArrayList;


public class FrgAddTransactionBetweenAccounts extends Fragment {

    private Spinner spinAccountFrom, spinAccountTo;

    private SpinAccountForTransAdapter adapterAccountTo;

    private View view;

    private EditText etExchange;

    private RelativeLayout layoutExchange;

    private ArrayList<Account> accountListFrom, accountListTo = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_transaction_between_accounts, container, false);

        etExchange = (EditText) view.findViewById(R.id.editTextTransBTWExchange);
        layoutExchange = (RelativeLayout) view.findViewById(R.id.layoutAddTransBTWExchange);

        setSpinners();

        return view;
    }



    private void setSpinners() {
        spinAccountFrom = (Spinner) view.findViewById(R.id.spinAddTransBTWAccountFrom);
        spinAccountTo = (Spinner) view.findViewById(R.id.spinAddTransBTWAccountTo);

        accountListFrom = InfoFromDB.getInstance().getAccountList();
        accountListTo = new ArrayList<>();

        spinAccountFrom.setAdapter(new SpinAccountForTransAdapter(getActivity(),
                R.layout.spin_head_text, accountListFrom));


        accountListTo.addAll(accountListFrom);
        accountListTo.remove(spinAccountFrom.getSelectedItemPosition());

        adapterAccountTo = new SpinAccountForTransAdapter(getActivity(),
                R.layout.spin_head_text, accountListTo);

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

            layoutExchange.setVisibility(View.VISIBLE);

            Account accountFrom = (Account) spinAccountFrom.getSelectedItem();
            String currFrom = accountFrom.getCurrency();
            Account accountTo = (Account) spinAccountTo.getSelectedItem();
            String currTo = accountTo.getCurrency();

            double exchangeRate = ExchangeUtils.getExchangeRate(currFrom, currTo);

            final int PRECISE = 100;
            final String FORMAT = "0.00";

            etExchange.setText(FormatUtils.doubleFormatter(exchangeRate, FORMAT, PRECISE));

            etExchange.setSelection(etExchange.getText().length());
        }

        else {
            layoutExchange.setVisibility(View.GONE);
            }
    }


    private boolean checkForMultiCurrency() {
        Account accountFrom = (Account) spinAccountFrom.getSelectedItem();
        Account accountTo = (Account) spinAccountTo.getSelectedItem();

        return !accountFrom.getCurrency().equals(accountTo.getCurrency());
    }


    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgMain.BROADCAST_FRG_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgMain.PARAM_STATUS_FRG_MAIN, FrgMain.STATUS_UPDATE_FRG_MAIN_BALANCE);
        getActivity().sendBroadcast(intentFragmentMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        getActivity().sendBroadcast(intentFrgAccounts);
    }


    public void addTransactionBTW() {

        EditText etSum = (EditText) view.findViewById(R.id.editTextTransBTWSum);

        if (checkEditTextForCorrect(etSum, R.string.transaction_empty_amount_field)) {

            double amount = Double.parseDouble(etSum.getText().toString());

            Account accountFrom = (Account) spinAccountFrom.getSelectedItem();
            double accountAmountFrom = accountFrom.getAmount();

            if (amount > accountAmountFrom) {
                Toast.makeText(getActivity(), getResources().getString(R.string.transaction_not_enough_costs) + " " +
                        Math.abs(amount), Toast.LENGTH_SHORT).show();

            } else {

                int accountIdFrom = accountFrom.getId();

                Account accountTo = (Account) spinAccountTo.getSelectedItem();

                int accountIdTo = accountTo.getId();
                double accountAmountTo = accountTo.getAmount();


                if (etExchange.getVisibility() == View.VISIBLE) {

                    if (checkEditTextForCorrect(etExchange, R.string.transaction_empty_exchange_field)) {

                        double exchange = Double.parseDouble(etExchange.getText().toString());

                        double amountTo = amount / exchange;

                        lastActions(amount, amountTo, accountIdFrom, accountIdTo, accountAmountFrom, accountAmountTo);
                    }

                } else {

                    lastActions(amount, amount, accountIdFrom, accountIdTo, accountAmountFrom, accountAmountTo);
                }
            }
        }
    }

    private void lastActions(double amount, double amountTo,
                             int idFrom, int idTo,
                             double accAmountFrom, double accAmountTo) {

        double accountAmountFrom = accAmountFrom - amount;
        double accountAmountTo = accAmountTo + amountTo;

        new DataSource(getActivity()).updateAccountsAmountAfterTransfer(idFrom,
                accountAmountFrom, idTo, accountAmountTo);

        InfoFromDB.getInstance().updateAccountList();

        pushBroadcast();

        getActivity().finish();
    }


    private boolean checkEditTextForCorrect(EditText et, int strRes) {

        String s = et.getText().toString();

        if (!s.matches(".*\\d.*") || Double.parseDouble(s) == 0) {
            Shake.highlightEditText(et);
            Toast.makeText(getActivity(), getResources().getString(strRes), Toast.LENGTH_SHORT).show();

        return false;}

        return true;
    }
}