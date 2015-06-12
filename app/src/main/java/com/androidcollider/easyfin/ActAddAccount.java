package com.androidcollider.easyfin;

import com.androidcollider.easyfin.adapters.SpinnerAccountCurrencyAdapter;
import com.androidcollider.easyfin.adapters.SpinnerAccountTypeAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FrgMain;
import com.androidcollider.easyfin.objects.Account;
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


public class ActAddAccount extends AppCompatActivity {

    Spinner spinAddExpenseType, spinAddExpenseCurrency;

    DataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_account);

        setToolbar(R.string.new_account);

        setSpinner();

        dataSource = new DataSource(this);
    }

    private void setSpinner() {
        spinAddExpenseType = (Spinner) findViewById(R.id.spinAddAccountType);
        spinAddExpenseCurrency = (Spinner) findViewById(R.id.spinAddAccountCurrency);

        spinAddExpenseType.setAdapter(new SpinnerAccountTypeAdapter(this, R.layout.spin_custom_item,
                getResources().getStringArray(R.array.account_type_array)));

        spinAddExpenseCurrency.setAdapter(new SpinnerAccountCurrencyAdapter(this, R.layout.spin_custom_item,
                getResources().getStringArray(R.array.account_currency_array)));
    }

    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolBar.inflateMenu(R.menu.toolbar_add_account_menu);
    }


    private void addExpense() {
        EditText editTextExpenseName = (EditText) findViewById(R.id.editTextAccountName);
        EditText editTextExpenseSum = (EditText) findViewById(R.id.editTextAccountSum);

        String st = editTextExpenseName.getText().toString().replaceAll("\\s+", "");

        if (st.isEmpty()) {
            Shake.highlightEditText(editTextExpenseName);
            Toast.makeText(this, getResources().getString(R.string.account_empty_field_name), Toast.LENGTH_LONG).show();
        }

        else {

            if (!editTextExpenseSum.getText().toString().matches(".*\\d.*")) {
                Shake.highlightEditText(editTextExpenseSum);
                Toast.makeText(this, getResources().getString(R.string.account_empty_field_sum), Toast.LENGTH_LONG).show();
            }

            else {

                if (dataSource.checkAccountNameMatches(editTextExpenseName.getText().toString())) {
                    Shake.highlightEditText(editTextExpenseName);
                    Toast.makeText(this, getResources().getString(R.string.account_name_exist), Toast.LENGTH_LONG).show();
                }

                else {

                    String name = editTextExpenseName.getText().toString();
                    double amount = Double.parseDouble(editTextExpenseSum.getText().toString());
                    String type = spinAddExpenseType.getSelectedItem().toString();
                    String currency = spinAddExpenseCurrency.getSelectedItem().toString();

                    Account account = new Account(name, amount, type, currency);

                    dataSource.insertNewAccount(account);

                    pushBroadcast();

                    closeActivity();
                }
            }
        }
    }

    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgMain.PARAM_STATUS_FRAGMENT_MAIN, FrgMain.STATUS_UPDATE_FRAGMENT_MAIN);
        sendBroadcast(intentFragmentMain);

        Intent intentMainSnack = new Intent(MainActivity.BROADCAST_MAIN_SNACK_ACTION);
        intentMainSnack.putExtra(MainActivity.PARAM_STATUS_MAIN_SNACK, MainActivity.STATUS_MAIN_SNACK);
        sendBroadcast(intentMainSnack);
    }

    private void closeActivity() {this.finish();}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                closeActivity();
                return true;}
            case R.id.add_expense_action_save: {
                addExpense();
                return true;}
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_add_account_menu, menu);
        return true;
    }
}
