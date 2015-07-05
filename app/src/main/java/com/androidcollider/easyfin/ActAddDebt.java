package com.androidcollider.easyfin;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.fragments.FrgAccounts;
import com.androidcollider.easyfin.fragments.FrgMain;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.Debt;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.EditTextAmountWatcher;
import com.androidcollider.easyfin.utils.FormatUtils;
import com.androidcollider.easyfin.utils.Shake;

import java.util.ArrayList;
import java.util.Calendar;

public class ActAddDebt extends AppCompatActivity implements View.OnClickListener{

    private DatePickerDialog datePickerDialog;

    private TextView tvDate;
    private EditText etName, etSum;
    Spinner spinType, spinAccount;

    private final String DATEFORMAT = "dd.MM.yyyy";

    private ArrayList<Account> accountList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_debt);

        setToolbar(R.string.new_debt);

        CardView cardView = (CardView) findViewById(R.id.cardAddDebtElements);

        accountList = InfoFromDB.getInstance().getAccountList();

        if (accountList.isEmpty()) {
            cardView.setVisibility(View.GONE);
            showDialogNoAccount();
        }

        else {

            cardView.setVisibility(View.VISIBLE);

            etName = (EditText) findViewById(R.id.editTextDebtName);
            etSum = (EditText) findViewById(R.id.editTextDebtSum);
            etSum.addTextChangedListener(new EditTextAmountWatcher(etSum));

            tvDate = (TextView) findViewById(R.id.tvAddDebtDate);

            setDateTimeField();

            setSpinner();
        }
    }

    private void setSpinner() {
        spinType = (Spinner) findViewById(R.id.spinAddDebtType);
        spinAccount = (Spinner) findViewById(R.id.spinAddDebtAccount);

        ArrayAdapter<?> adapterType = ArrayAdapter.createFromResource(
                this,
                R.array.debt_type_array,
                R.layout.spin_head_text);

        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinType.setAdapter(adapterType);


        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                this,
                R.layout.spin_head_icon_text,
                accountList
        ));

        spinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: {
                        etSum.setTextColor(getResources().getColor(R.color.custom_green));
                        break;}
                    case 1: {
                        etSum.setTextColor(getResources().getColor(R.color.custom_red));
                        break;}
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void addDebt() {

        if (checkForFillNameSumFields()) {

            Account account = (Account) spinAccount.getSelectedItem();
            double accountAmount = account.getAmount();

            int type = spinType.getSelectedItemPosition();
            double amount = Double.parseDouble(FormatUtils.prepareStringToParse(etSum.getText().toString()));


            if (type == 0 && Math.abs(amount) > accountAmount) {
                Toast.makeText(this, getResources().getString(R.string.not_enough_costs), Toast.LENGTH_SHORT).show();

            } else {

                String name = etName.getText().toString();
                int accountId = account.getId();
                Long date = DateFormat.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();

                switch (type){
                    case 0: {accountAmount -= amount; break;}
                    case 1: {accountAmount += amount; break;}
                }

                Debt debt = new Debt(name, amount, type, accountId, date, accountAmount);

                MainActivity.dataSource.insertNewDebt(debt);

                InfoFromDB.getInstance().updateAccountList();

                pushBroadcast();

                this.finish();
            }
        }
    }

    private void pushBroadcast() {
        Intent intentFrgMain = new Intent(FrgMain.BROADCAST_FRG_MAIN_ACTION);
        intentFrgMain.putExtra(FrgMain.PARAM_STATUS_FRG_MAIN, FrgMain.STATUS_UPDATE_FRG_MAIN_BALANCE);
        sendBroadcast(intentFrgMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        sendBroadcast(intentFrgAccounts);

        Intent intentDebt = new Intent(ActDebt.BROADCAST_DEBT_ACTION);
        intentDebt.putExtra(ActDebt.PARAM_STATUS_DEBT, ActDebt.STATUS_UPDATE_DEBT);
        sendBroadcast(intentDebt);
    }

    private boolean checkForFillNameSumFields() {

        String st = etName.getText().toString().replaceAll("\\s+", "");

        if (st.isEmpty()) {
            Shake.highlightEditText(etName);
            Toast.makeText(this, getResources().getString(R.string.empty_name_field), Toast.LENGTH_SHORT).show();

            return false;
        }

        else {

            if (!FormatUtils.prepareStringToParse(etSum.getText().toString()).matches(".*\\d.*")) {
                Shake.highlightEditText(etSum);
                Toast.makeText(this, getResources().getString(R.string.empty_amount_field), Toast.LENGTH_SHORT).show();

                return false;
            }
        }

        return true;
    }

    private void setDateTimeField() {
        tvDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        tvDate.setText(DateFormat.dateToString(newCalendar.getTime(), DATEFORMAT));

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

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
            case R.id.tvAddDebtDate: datePickerDialog.show(); break;
        }
    }

    private void setToolbar (int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolBar.inflateMenu(R.menu.toolbar_debt_menu);
    }

    private void showDialogNoAccount() {

        new MaterialDialog.Builder(this)
                .title(getString(R.string.no_account))
                .content(getString(R.string.dialog_text_debt_no_account))
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;}
            case R.id.debt_action_save: {
                addDebt();
                return true;}

        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_debt_menu, menu);
        MenuItem saveDebtItem = menu.findItem(R.id.debt_action_save);
        saveDebtItem.setEnabled(true);

        if (accountList.isEmpty()) {
            saveDebtItem.setVisible(false);}

        return true;
    }

}
