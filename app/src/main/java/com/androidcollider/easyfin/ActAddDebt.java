package com.androidcollider.easyfin;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.androidcollider.easyfin.utils.DateFormatUtils;
import com.androidcollider.easyfin.utils.EditTextAmountWatcher;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.HideKeyboardUtils;
import com.androidcollider.easyfin.utils.ShakeEditText;
import com.androidcollider.easyfin.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ActAddDebt extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;

    private TextView tvDate;
    private EditText etName, etSum;
    private RadioButton rbGive;
    private Spinner spinAccount;

    private final String DATEFORMAT = "dd.MM.yyyy";

    private ArrayList<Account> accountList = null;

    private int mode;

    private Debt debtFrIntent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_debt);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);

        switch (mode) {

            case 0: {setToolbar(R.string.new_debt); break;}
            case 1: {setToolbar(R.string.edit_debt);
                     debtFrIntent = (Debt) intent.getSerializableExtra("debt");
                     break;}
        }


        CardView cardView = (CardView) findViewById(R.id.cardAddDebtElements);

        accountList = InfoFromDB.getInstance().getAccountList();

        if (accountList.isEmpty()) {
            cardView.setVisibility(View.GONE);
            showDialogNoAccount();
        }

        else {

            cardView.setVisibility(View.VISIBLE);

            initializeFields();

            setDateTimeField();

            setSpinner();

            setRadioGroupEvents();

            HideKeyboardUtils.setupUI(findViewById(R.id.layoutActAddDebtParent), this);

            if (mode == 1) {
                setViewsToEdit();
            }
        }
    }

    private void initializeFields() {
        etName = (EditText) findViewById(R.id.editTextDebtName);
        etSum = (EditText) findViewById(R.id.editTextDebtSum);
        etSum.addTextChangedListener(new EditTextAmountWatcher(etSum));
        etSum.setTextColor(getResources().getColor(R.color.custom_red));

        tvDate = (TextView) findViewById(R.id.tvAddDebtDate);

        rbGive = (RadioButton) findViewById(R.id.radioButtonDebtGive);
    }

    private void setViewsToEdit() {

        etName.setText(debtFrIntent.getName());
        etName.setSelection(etName.getText().length());

        final int PRECISE = 100;
        final String FORMAT = "0.00";

        etSum.setText(DoubleFormatUtils.doubleToStringFormatter(debtFrIntent.getAmountCurrent(), FORMAT, PRECISE));


        int type = debtFrIntent.getType();

        if (type == 0) {
            rbGive.setChecked(true);
        }

    }

    private void setRadioGroupEvents() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupDebtType);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.radioButtonDebtTake: {
                        etSum.setTextColor(getResources().getColor(R.color.custom_red));
                        break;
                    }
                    case R.id.radioButtonDebtGive: {
                        etSum.setTextColor(getResources().getColor(R.color.custom_green));
                        break;
                    }
                }
            }
        });
    }

    private void setSpinner() {

        spinAccount = (Spinner) findViewById(R.id.spinAddDebtAccount);

        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                this,
                R.layout.spin_head_icon_text,
                accountList
        ));


        if (mode == 1) {

            int idAccount = debtFrIntent.getIdAccount();
            int pos = 0;

            for (int i = 0; i < accountList.size(); i++) {

                if (idAccount == accountList.get(i).getId()) {
                    pos = i;
                    break;
                }
            }

            spinAccount.setSelection(pos);
        }

    }

    private void addDebt() {

        if (checkForFillNameSumFields()) {

            Account account = (Account) spinAccount.getSelectedItem();
            double accountAmount = account.getAmount();

            int type = 1;

            if (rbGive.isChecked()) {
                type = 0;
            }

            double amount = Double.parseDouble(DoubleFormatUtils.prepareStringToParse(etSum.getText().toString()));


            if (type == 0 && Math.abs(amount) > accountAmount) {

                ToastUtils.showClosableToast(this, getString(R.string.not_enough_costs), 1);

            } else {

                String name = etName.getText().toString();
                int accountId = account.getId();
                Long date = DateFormatUtils.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();

                switch (type){
                    case 0: {accountAmount -= amount; break;}
                    case 1: {accountAmount += amount; break;}
                }

                Debt debt = new Debt(name, amount, type, accountId, date, accountAmount);

                InfoFromDB.getInstance().getDataSource().insertNewDebt(debt);

                InfoFromDB.getInstance().updateAccountList();

                pushBroadcast();

                this.finish();
            }
        }
    }

    private void editDebt() {

        if (checkForFillNameSumFields()) {

            Account account = (Account) spinAccount.getSelectedItem();
            double accountAmount = account.getAmount();

            int type = 1;

            if (rbGive.isChecked()) {
                type = 0;
            }

            double amount = Double.parseDouble(DoubleFormatUtils.prepareStringToParse(etSum.getText().toString()));

            int accountId = account.getId();
            int oldAccountId = debtFrIntent.getIdAccount();


            boolean isAccountsTheSame = accountId == oldAccountId;


            double oldAmount = debtFrIntent.getAmountCurrent();
            double oldAccountAmount = 0;
            int oldType = debtFrIntent.getType();

            if (isAccountsTheSame) {

                switch (oldType) {
                    case 0: {accountAmount += oldAmount; break;}
                    case 1: {accountAmount -= oldAmount; break;}
                }
            }

            else {

                for (int i = 0; i < accountList.size(); i++) {

                    if (oldAccountId == accountList.get(i).getId()) {
                        oldAccountAmount = accountList.get(i).getAmount();
                        break;
                    }
                }

                switch (oldType) {
                    case 0: {oldAccountAmount += oldAmount; break;}
                    case 1: {oldAccountAmount -= oldAmount; break;}
                }
            }


            if (type == 0 && Math.abs(amount) > accountAmount) {

                ToastUtils.showClosableToast(this, getString(R.string.not_enough_costs), 1);

            } else {

                String name = etName.getText().toString();
                Long date = DateFormatUtils.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();

                switch (type){
                    case 0: {accountAmount -= amount; break;}
                    case 1: {accountAmount += amount; break;}
                }

                int idDebt = debtFrIntent.getId();

                Debt debt = new Debt(name, amount, type, accountId, date, accountAmount);

                if (isAccountsTheSame) {
                    InfoFromDB.getInstance().getDataSource().editDebt(debt, idDebt);
                }
                else {
                    InfoFromDB.getInstance().getDataSource().editDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId, idDebt);
                }

                InfoFromDB.getInstance().updateAccountList();

                pushBroadcast();

                this.finish();
            }
        }
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

    private boolean checkForFillNameSumFields() {

        String st = etName.getText().toString().replaceAll("\\s+", "");

        if (st.isEmpty()) {
            ShakeEditText.highlightEditText(etName);
            ToastUtils.showClosableToast(this, getString(R.string.empty_name_field), 1);

            return false;
        }

        else {

            if (!DoubleFormatUtils.prepareStringToParse(etSum.getText().toString()).matches(".*\\d.*")) {
                ShakeEditText.highlightEditText(etSum);
                ToastUtils.showClosableToast(this, getString(R.string.empty_amount_field), 1);

                return false;
            }
        }

        return true;
    }

    private void setDateTimeField() {
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        final Calendar newCalendar = Calendar.getInstance();

        final long initTime = newCalendar.getTimeInMillis();

        if (mode == 1) {

            newCalendar.setTime(new Date(debtFrIntent.getDate()));
        }

        tvDate.setText(DateFormatUtils.dateToString(newCalendar.getTime(), DATEFORMAT));

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (newDate.getTimeInMillis() < initTime) {
                    ToastUtils.showClosableToast(ActAddDebt.this, getString(R.string.debt_deadline_past), 1);

                } else {

                    tvDate.setText(DateFormatUtils.dateToString(newDate.getTime(), DATEFORMAT));
                }
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
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

                switch (mode) {
                    case 0: {addDebt(); break;}
                    case 1: {editDebt(); break;}
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

        if (accountList.isEmpty()) {
            saveDebtItem.setVisible(false);}

        return true;
    }

}
