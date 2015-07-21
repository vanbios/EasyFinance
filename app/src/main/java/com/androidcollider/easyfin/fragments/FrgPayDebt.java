package com.androidcollider.easyfin.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.Debt;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.EditTextAmountWatcher;
import com.androidcollider.easyfin.utils.HideKeyboardUtils;
import com.androidcollider.easyfin.utils.ShakeEditText;
import com.androidcollider.easyfin.utils.ToastUtils;

import java.util.ArrayList;



public class FrgPayDebt extends CommonFragmentAddEdit {

    private View view;

    private TextView tvDebtName;
    private EditText etSum;
    private Spinner spinAccount;

    private Debt debt;

    private ArrayList<Account> accountsAvailableList = null;

    private int mode;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frg_pay_debt, container, false);

        mode = getArguments().getInt("mode", 0);
        debt = (Debt) getArguments().getSerializable("debt");

        setToolbar();

        fillAvailableAccountsList();

        CardView cardView = (CardView) view.findViewById(R.id.cardPayDebtElements);

        if (accountsAvailableList.isEmpty()) {
            cardView.setVisibility(View.GONE);
            showDialogNoAccount();
        }

        else {

            cardView.setVisibility(View.VISIBLE);

            initializeView();

            setView();

            HideKeyboardUtils.setupUI(view.findViewById(R.id.layoutActPayDebtParent), getActivity());
        }

        return view;
    }


    private void initializeView() {
        tvDebtName = (TextView) view.findViewById(R.id.tvPayDebtName);
        //etSum = (EditText) view.findViewById(R.id.editTextPayDebtSum);
        //etSum.addTextChangedListener(new EditTextAmountWatcher(etSum));
        spinAccount = (Spinner) view.findViewById(R.id.spinPayDebtAccount);
    }


    private void setView() {

        tvDebtName.setText(debt.getName());

        if (mode == 1 || mode == 2) {

            final int PRECISE = 100;
            final String FORMAT = "0.00";

            //etSum.setText(DoubleFormatUtils.doubleToStringFormatter(debt.getAmountCurrent(), FORMAT, PRECISE));
        }

        if (mode == 1) {
            //etSum.setEnabled(false);
        }


        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountsAvailableList));

        int idAccount = debt.getIdAccount();

        int pos = 0;

        for (int i = 0; i < accountsAvailableList.size(); i++) {

            if (idAccount == accountsAvailableList.get(i).getId()) {
                pos = i;
            }
        }

        spinAccount.setSelection(pos);
    }

    private void fillAvailableAccountsList() {

        ArrayList<Account> accountList = InfoFromDB.getInstance().getAccountList();

        accountsAvailableList = new ArrayList<>();

        String currency = debt.getCurrency();

        double amount = debt.getAmountCurrent();

        int type = debt.getType();


        if (mode == 1 && type == 1) {

            for (Account account : accountList) {
                if (account.getCurrency().equals(currency) && account.getAmount() >= amount) {

                    accountsAvailableList.add(account);
                }
            }
        }

        else {

            for (Account account : accountList) {
                if (account.getCurrency().equals(currency)) {

                    accountsAvailableList.add(account);
                }
            }
        }
    }



    private void payAllDebt(){

        int idDebt = debt.getId();
        double amountDebt = debt.getAmountCurrent();
        int type = debt.getType();

        Account account = (Account) spinAccount.getSelectedItem();

        int idAccount = account.getId();
        double amountAccount = account.getAmount();

        if (type == 1) {
            amountAccount -= amountDebt;
        }
        else {
            amountAccount += amountDebt;
        }

        InfoFromDB.getInstance().getDataSource().payAllDebt(idAccount, amountAccount, idDebt);

        lastActions();
    }

    private void payPartDebt(){

        String sum = DoubleFormatUtils.prepareStringToParse(etSum.getText().toString());

        if (checkForFillSumField(sum)) {

            double amountDebt = Double.parseDouble(sum);
            double amountAllDebt = debt.getAmountCurrent();

            if (amountDebt > amountAllDebt) {
                ShakeEditText.highlightEditText(etSum);
                ToastUtils.showClosableToast(getActivity(), getString(R.string.debt_sum_more_then_amount), 1);

            } else {

                int type = debt.getType();

                Account account = (Account) spinAccount.getSelectedItem();

                double amountAccount = account.getAmount();


                if (type == 1 && amountDebt > amountAccount) {
                    ShakeEditText.highlightEditText(etSum);
                    ToastUtils.showClosableToast(getActivity(), getString(R.string.not_enough_costs), 1);

                } else {

                    int idDebt = debt.getId();
                    int idAccount = account.getId();


                    if (type == 1) {
                        amountAccount -= amountDebt;
                    } else {
                        amountAccount += amountDebt;
                    }


                    if (amountDebt == amountAllDebt) {

                        InfoFromDB.getInstance().getDataSource().payAllDebt(idAccount, amountAccount, idDebt);}

                    else {

                        double newDebtAmount = amountAllDebt - amountDebt;

                        InfoFromDB.getInstance().getDataSource().payPartDebt(idAccount, amountAccount, idDebt, newDebtAmount);
                    }

                    lastActions();
                }
            }
        }
    }

    private void takeMoreDebt() {

        String sum = DoubleFormatUtils.prepareStringToParse(etSum.getText().toString());

        if (checkForFillSumField(sum)) {

            double amountDebt = Double.parseDouble(sum);
            double amountDebtCurrent = debt.getAmountCurrent();
            double amountDebtAll = debt.getAmountAll();


            int type = debt.getType();

            Account account = (Account) spinAccount.getSelectedItem();

            double amountAccount = account.getAmount();


            if (type == 0 && amountDebt > amountAccount) {
                ShakeEditText.highlightEditText(etSum);
                ToastUtils.showClosableToast(getActivity(), getString(R.string.not_enough_costs), 1);

            } else {

                int idDebt = debt.getId();
                int idAccount = account.getId();


                switch (type) {
                    case 0: {amountAccount -= amountDebt; break;}
                    case 1: {amountAccount += amountDebt; break;}
                }


                double newDebtCurrentAmount = amountDebtCurrent + amountDebt;
                double newDebtAllAmount = amountDebtAll + amountDebt;

                InfoFromDB.getInstance().getDataSource().takeMoreDebt(idAccount, amountAccount,
                        idDebt, newDebtCurrentAmount, newDebtAllAmount);


                lastActions();
            }
        }
    }

    private boolean checkForFillSumField(String s) {

        if (! s.matches(".*\\d.*") || Double.parseDouble(s) == 0) {
            ShakeEditText.highlightEditText(etSum);
            ToastUtils.showClosableToast(getActivity(), getString(R.string.empty_amount_field), 1);

            return false;
        }

        return true;
    }

    private void lastActions() {
        InfoFromDB.getInstance().updateAccountList();
        pushBroadcast();
        this.finish();
    }

    private void pushBroadcast() {
        Intent intentFrgMain = new Intent(FrgHome.BROADCAST_FRG_MAIN_ACTION);
        intentFrgMain.putExtra(FrgHome.PARAM_STATUS_FRG_MAIN, FrgHome.STATUS_UPDATE_FRG_MAIN_BALANCE);
        getActivity().sendBroadcast(intentFrgMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        getActivity().sendBroadcast(intentFrgAccounts);

        Intent intentDebt = new Intent(FrgDebts.BROADCAST_DEBT_ACTION);
        intentDebt.putExtra(FrgDebts.PARAM_STATUS_DEBT, FrgDebts.STATUS_UPDATE_DEBT);
        getActivity().sendBroadcast(intentDebt);
    }



    private void showDialogNoAccount() {

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(getString(R.string.debt_no_available_accounts_warning))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.close))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        goToAddNewAccount();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) { finish();}
                })
                .cancelable(false)
                .show();
    }

    private void goToAddNewAccount() {
        finish();
        FrgAddAccount frgAddAccount = new FrgAddAccount();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        frgAddAccount.setArguments(arguments);

        addFragment(frgAddAccount);
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

                    switch (mode) {
                        case 1: {payAllDebt(); break;}
                        case 2: {payPartDebt(); break;}
                        case 3: {takeMoreDebt(); break;}
                    }
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

}
