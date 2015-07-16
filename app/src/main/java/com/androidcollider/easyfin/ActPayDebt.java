package com.androidcollider.easyfin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.fragments.FrgAccounts;
import com.androidcollider.easyfin.fragments.FrgDebts;
import com.androidcollider.easyfin.fragments.FrgHome;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.Debt;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.EditTextAmountWatcher;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.HideKeyboardUtils;
import com.androidcollider.easyfin.utils.ShakeEditText;
import com.androidcollider.easyfin.utils.ToastUtils;

import java.util.ArrayList;

public class ActPayDebt extends AppCompatActivity {

    private TextView tvDebtName;
    private EditText etSum;
    private Spinner spinAccount;

    private Debt debt;

    private ArrayList<Account> accountsAvailableList = null;

    private int mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pay_debt);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 1);
        debt = (Debt) intent.getSerializableExtra("debt");

        switch (mode) {
            case 1: {setToolbar(R.string.pay_all_debt); break;}
            case 2: {setToolbar(R.string.pay_part_debt); break;}
            case 3: {setToolbar(R.string.take_more_debt); break;}
        }

        fillAvailableAccountsList();

        CardView cardView = (CardView) findViewById(R.id.cardPayDebtElements);

        if (accountsAvailableList.isEmpty()) {
            cardView.setVisibility(View.GONE);
            showDialogNoAccount();
        }

        else {

            cardView.setVisibility(View.VISIBLE);

            initializeView();

            setView();

            HideKeyboardUtils.setupUI(findViewById(R.id.layoutActPayDebtParent), this);
        }
    }

    private void initializeView() {
        tvDebtName = (TextView) findViewById(R.id.tvPayDebtName);
        etSum = (EditText) findViewById(R.id.editTextPayDebtSum);
        etSum.addTextChangedListener(new EditTextAmountWatcher(etSum));
        spinAccount = (Spinner) findViewById(R.id.spinPayDebtAccount);
    }

    private void setView() {

        tvDebtName.setText(debt.getName());

        if (mode == 1 || mode == 2) {

            final int PRECISE = 100;
            final String FORMAT = "0.00";

            etSum.setText(DoubleFormatUtils.doubleToStringFormatter(debt.getAmountCurrent(), FORMAT, PRECISE));
            //etSum.setSelection(etSum.getText().length());
        }

        if (mode == 1) {
            etSum.setEnabled(false);
        }


        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                this,
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
                //Toast.makeText(this, getResources().getString(R.string.debt_sum_more_then_amount), Toast.LENGTH_SHORT).show();
                ToastUtils.showClosableToast(this, getResources().getString(R.string.debt_sum_more_then_amount), 1);

            } else {

                int type = debt.getType();

                Account account = (Account) spinAccount.getSelectedItem();

                double amountAccount = account.getAmount();


                if (type == 1 && amountDebt > amountAccount) {
                    ShakeEditText.highlightEditText(etSum);
                    //Toast.makeText(this, getResources().getString(R.string.not_enough_costs), Toast.LENGTH_SHORT).show();
                    ToastUtils.showClosableToast(this, getResources().getString(R.string.not_enough_costs), 1);

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
                //Toast.makeText(this, getResources().getString(R.string.not_enough_costs), Toast.LENGTH_SHORT).show();
                ToastUtils.showClosableToast(this, getResources().getString(R.string.not_enough_costs), 1);

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
            //Toast.makeText(this, getResources().getString(R.string.empty_amount_field), Toast.LENGTH_SHORT).show();
            ToastUtils.showClosableToast(this, getResources().getString(R.string.empty_amount_field), 1);

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
        sendBroadcast(intentFrgMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        sendBroadcast(intentFrgAccounts);

        Intent intentDebt = new Intent(FrgDebts.BROADCAST_DEBT_ACTION);
        intentDebt.putExtra(FrgDebts.PARAM_STATUS_DEBT, FrgDebts.STATUS_UPDATE_DEBT);
        sendBroadcast(intentDebt);
    }

    private void setToolbar (int id) {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(id);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolBar.inflateMenu(R.menu.toolbar_debt_menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home: {
                this.finish();
                return true;
            }

            case R.id.debt_action_save: {

                switch (mode) {
                    case 1: {
                        payAllDebt();
                        break;
                    }
                    case 2: {
                        payPartDebt();
                        break;
                    }
                    case 3: {
                        takeMoreDebt();
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_debt_menu, menu);
        MenuItem saveDebtItem = menu.findItem(R.id.debt_action_save);
        saveDebtItem.setEnabled(true);

        if (accountsAvailableList.isEmpty()) {
            saveDebtItem.setVisible(false);}

        return true;
    }

    private void showDialogNoAccount() {

        new MaterialDialog.Builder(this)
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
                    public void onNegative(MaterialDialog dialog) { closeAct();}
                })
                .cancelable(false)
                .show();
    }

    private void closeAct() {this.finish();}

    private void goToAddNewAccount() {
        this.finish();
        openAddAccountActivity();
    }

    private void openAddAccountActivity() {
        Intent intent = new Intent(this, ActAccount.class);
        startActivity(intent);
    }

}
