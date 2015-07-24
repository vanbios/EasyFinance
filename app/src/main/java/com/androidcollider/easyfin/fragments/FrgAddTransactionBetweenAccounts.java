package com.androidcollider.easyfin.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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


public class FrgAddTransactionBetweenAccounts extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    private Spinner spinAccountFrom, spinAccountTo;

    private SpinAccountForTransAdapter adapterAccountTo;

    private View view;

    private EditText etExchange;

    private TextView tvAmount;

    private RelativeLayout layoutExchange;

    private ArrayList<Account> accountListFrom, accountListTo = null;

    private DialogFragment numericDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_trans_btw, container, false);

        numericDialog = new FrgNumericDialog();
        numericDialog.setTargetFragment(this, 3);

        setToolbar();

        accountListFrom = InfoFromDB.getInstance().getAccountList();

        if (accountListFrom.size() < 2) {
            showDialogNoAccount();
        }

        else {

            tvAmount = (TextView) view.findViewById(R.id.tvAddTransBTWAmount);
            tvAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numericDialog.show(getActivity().getSupportFragmentManager(), "numericDialog3");
                }
            });

            etExchange = (EditText) view.findViewById(R.id.editTextTransBTWExchange);
            etExchange.addTextChangedListener(new EditTextAmountWatcher(etExchange));

            layoutExchange = (RelativeLayout) view.findViewById(R.id.layoutAddTransBTWExchange);

            setSpinners();

            HideKeyboardUtils.setupUI(view.findViewById(R.id.scrollAddTransBTW), getActivity());
        }

        return view;
    }

    private void setSpinners() {
        spinAccountFrom = (Spinner) view.findViewById(R.id.spinAddTransBTWAccountFrom);
        spinAccountTo = (Spinner) view.findViewById(R.id.spinAddTransBTWAccountTo);

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

            double amount = Double.parseDouble(DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString()));

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
        //}
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

        finish();
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

    private void setToolbar() {

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        if (actionBar != null) {

            ViewGroup actionBarLayout = (ViewGroup) getActivity().getLayoutInflater().inflate(
                    R.layout.save_close_buttons_toolbar, null);

            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(actionBarLayout, layoutParams);

            Toolbar parent = (Toolbar) actionBarLayout.getParent();
            parent.setContentInsetsAbsolute(0, 0);


            Button btnSave = (Button) actionBarLayout.findViewById(R.id.btnToolbarSave);
            Button btnClose = (Button) actionBarLayout.findViewById(R.id.btnToolbarClose);

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addTransactionBTW();
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void showDialogNoAccount() {

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(getString(R.string.dialog_text_transfer_no_accounts))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.close))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addAccount();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        finish();
                    }
                })
                .cancelable(false)
                .show();
    }

    private void addAccount() {
        FrgAddAccount frgAddAccount = new FrgAddAccount();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        frgAddAccount.setArguments(arguments);

        addFragment(frgAddAccount);
    }


    @Override
    public void onCommitAmountSubmit(String amount) {

        //set amount to tvAmount
    }

}