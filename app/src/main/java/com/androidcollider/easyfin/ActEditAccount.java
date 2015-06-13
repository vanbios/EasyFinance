package com.androidcollider.easyfin;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.adapters.SpinnerAccountCurrencyAdapter;
import com.androidcollider.easyfin.adapters.SpinnerAccountTypeAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FrgMain;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.FormatUtils;
import com.androidcollider.easyfin.utils.Shake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class ActEditAccount extends AppCompatActivity {

    Spinner spinExpenseTypeChange, spinExpenseCurrencyChange;

    EditText editTextExpenseNameChange, editTextExpenseSumChange;

    DataSource dataSource;

    private String OLDNAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_account);

        setToolbar(R.string.change_account);

        OLDNAME = getIntent().getStringExtra("name");

        setSpinner();

        editTextExpenseNameChange = (EditText) findViewById(R.id.editTextEditAccountName);
        editTextExpenseSumChange = (EditText) findViewById(R.id.editTextEditAccountSum);

        editTextExpenseNameChange.setText(getIntent().getStringExtra("name"));
        editTextExpenseNameChange.setSelection(editTextExpenseNameChange.getText().length());

        final int PRECISE = 100;
        final String FORMAT = "0.00";

        editTextExpenseSumChange.setText(FormatUtils.doubleFormatter(getIntent().getDoubleExtra("amount", 0.0), FORMAT, PRECISE));
        editTextExpenseSumChange.setSelection(editTextExpenseSumChange.getText().length());

        dataSource = new DataSource(this);
    }

    private void setSpinner() {
        spinExpenseTypeChange = (Spinner) findViewById(R.id.spinEditAccountType);
        spinExpenseCurrencyChange = (Spinner) findViewById(R.id.spinEditAccountCurrency);

        spinExpenseTypeChange.setAdapter(new SpinnerAccountTypeAdapter(this, R.layout.spin_custom_item,
                getResources().getStringArray(R.array.account_type_array)));

        spinExpenseCurrencyChange.setAdapter(new SpinnerAccountCurrencyAdapter(this, R.layout.spin_custom_item,
                getResources().getStringArray(R.array.account_currency_array)));


        String[] type = getResources().getStringArray(R.array.account_type_array);

        String typeVal = getIntent().getStringExtra("type");

        for (int i = 0; i < type.length; i++) {
            if (type[i].equals(typeVal)) {
                spinExpenseTypeChange.setSelection(i);
            }
        }

        String[] currency = getResources().getStringArray(R.array.account_currency_array);

        String currencyVal = getIntent().getStringExtra("currency");

        for (int i = 0; i < currency.length; i++) {
            if (currency[i].equals(currencyVal)) {
                spinExpenseCurrencyChange.setSelection(i);
            }
        }
    }

    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolBar.inflateMenu(R.menu.toolbar_edit_account_menu);
    }


    private void changeExpense() {

        String st = editTextExpenseNameChange.getText().toString().replaceAll("\\s+", "");

        if (st.isEmpty()) {
            Shake.highlightEditText(editTextExpenseNameChange);
            Toast.makeText(this, getResources().getString(R.string.account_empty_field_name), Toast.LENGTH_LONG).show();
        }

        else {

            if (!editTextExpenseSumChange.getText().toString().matches(".*\\d.*")) {
                Shake.highlightEditText(editTextExpenseSumChange);
                Toast.makeText(this, getResources().getString(R.string.account_empty_field_sum), Toast.LENGTH_LONG).show();
            }

            else {

                String s = editTextExpenseNameChange.getText().toString();

                if (dataSource.checkAccountNameMatches(s) && ! s.equals(OLDNAME)) {
                    Shake.highlightEditText(editTextExpenseNameChange);
                    Toast.makeText(this, getResources().getString(R.string.account_name_exist), Toast.LENGTH_LONG).show();
                }

                else {

                    String name = editTextExpenseNameChange.getText().toString();
                    double amount = Double.parseDouble(editTextExpenseSumChange.getText().toString());
                    String type = spinExpenseTypeChange.getSelectedItem().toString();
                    String currency = spinExpenseCurrencyChange.getSelectedItem().toString();

                    Account account = new Account(name, amount, type, currency);

                    dataSource.editAccount(OLDNAME, account);

                    pushBroadcast();

                    this.finish();
                }
            }
        }
    }

    private void deleteExpenseDialog() {

        new MaterialDialog.Builder(this)
                .title(getString(R.string.dialog_title_delete_account))
                .content(getString(R.string.dialog_text_delete_account))
                .positiveText(getString(R.string.dialog_button_delete_account))
                .negativeText(getString(R.string.dialog_button_delete_account_cancel))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        deleteExpense();
                    }
                })
                .show();

    }

    private void deleteExpense() {
        dataSource.deleteAccount(OLDNAME);
        pushBroadcast();
        this.finish();
    }

    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgMain.PARAM_STATUS_FRAGMENT_MAIN, FrgMain.STATUS_UPDATE_FRAGMENT_MAIN);
        sendBroadcast(intentFragmentMain);
    }

    /*private void closeActivity() {this.finish();}*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;}
            case R.id.change_expense_action_save: {
                changeExpense();
                return true;}
            case R.id.change_expense_action_delete: {
                deleteExpenseDialog();
                return true;}
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_edit_account_menu, menu);
        return true;
    }
}
