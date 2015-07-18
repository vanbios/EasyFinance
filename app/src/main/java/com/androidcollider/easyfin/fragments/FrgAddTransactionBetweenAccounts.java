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

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransAdapter;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.EditTextAmountWatcher;
import com.androidcollider.easyfin.utils.ExchangeUtils;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.HideKeyboardUtils;
import com.androidcollider.easyfin.utils.ShakeEditText;
import com.androidcollider.easyfin.utils.ToastUtils;

import java.util.ArrayList;


public class FrgAddTransactionBetweenAccounts extends Fragment {

    private Spinner spinAccountFrom, spinAccountTo;

    private SpinAccountForTransAdapter adapterAccountTo;

    private View view;

    private EditText etExchange, etSum;

    private RelativeLayout layoutExchange;

    private ArrayList<Account> accountListFrom, accountListTo = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_transaction_between_accounts, container, false);

        etExchange = (EditText) view.findViewById(R.id.editTextTransBTWExchange);
        etExchange.addTextChangedListener(new EditTextAmountWatcher(etExchange));

        etSum = (EditText) view.findViewById(R.id.editTextTransBTWSum);
        etSum.addTextChangedListener(new EditTextAmountWatcher(etSum));

        layoutExchange = (RelativeLayout) view.findViewById(R.id.layoutAddTransBTWExchange);

        setSpinners();

        HideKeyboardUtils.setupUI(view.findViewById(R.id.scrollAddTransBTW), getActivity());

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

            etExchange.setText(DoubleFormatUtils.doubleToStringFormatter(exchangeRate, FORMAT, PRECISE));

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
        Intent intentFragmentMain = new Intent(FrgHome.BROADCAST_FRG_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgHome.PARAM_STATUS_FRG_MAIN, FrgHome.STATUS_UPDATE_FRG_MAIN_BALANCE);
        getActivity().sendBroadcast(intentFragmentMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        getActivity().sendBroadcast(intentFrgAccounts);
    }

    public void addTransactionBTW() {

        if (checkEditTextForCorrect(etSum, R.string.empty_amount_field)) {

            double amount = Double.parseDouble(DoubleFormatUtils.prepareStringToParse(etSum.getText().toString()));

            Account accountFrom = (Account) spinAccountFrom.getSelectedItem();
            double accountAmountFrom = accountFrom.getAmount();

            if (amount > accountAmountFrom) {
                ToastUtils.showClosableToast(getActivity(), getString(R.string.not_enough_costs), 1);

            } else {

                int accountIdFrom = accountFrom.getId();

                Account accountTo = (Account) spinAccountTo.getSelectedItem();

                int accountIdTo = accountTo.getId();
                double accountAmountTo = accountTo.getAmount();


                if (layoutExchange.getVisibility() == View.VISIBLE) {

                    if (checkEditTextForCorrect(etExchange, R.string.empty_exchange_field)) {

                        double exchange = Double.parseDouble(DoubleFormatUtils.prepareStringToParse(etExchange.getText().toString()));

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

        InfoFromDB.getInstance().getDataSource().updateAccountsAmountAfterTransfer(idFrom,
                accountAmountFrom, idTo, accountAmountTo);

        InfoFromDB.getInstance().updateAccountList();

        pushBroadcast();

        getActivity().finish();
    }

    private boolean checkEditTextForCorrect(EditText et, int strRes) {

        String s = DoubleFormatUtils.prepareStringToParse(et.getText().toString());

        if (!s.matches(".*\\d.*") || Double.parseDouble(s) == 0) {
            ShakeEditText.highlightEditText(et);
            ToastUtils.showClosableToast(getActivity(), getString(strRes), 1);

        return false;
        }

        return true;
    }

}