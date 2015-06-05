package com.androidcollider.easyfin;

import com.androidcollider.easyfin.adapters.SpinnerCurrencyAdapter;
import com.androidcollider.easyfin.adapters.SpinnerExpenceTypeAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FragmentMain;
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


public class AddExpenseActivity extends AppCompatActivity {

    Spinner spinAddExpenseType, spinAddExpenseCurrency;

    DataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addexpense);

        setToolbar(R.string.new_expense);

        setSpinner();

        dataSource = new DataSource(this);
    }

    private void setSpinner() {
        spinAddExpenseType = (Spinner) findViewById(R.id.spinAddExpenseType);
        spinAddExpenseCurrency = (Spinner) findViewById(R.id.spinAddExpenseCurrency);

        spinAddExpenseType.setAdapter(new SpinnerExpenceTypeAdapter(this, R.layout.spinner_item,
                getResources().getStringArray(R.array.expense_type_array)));

        spinAddExpenseCurrency.setAdapter(new SpinnerCurrencyAdapter(this, R.layout.spinner_item,
                getResources().getStringArray(R.array.expense_currency_array)));
    }

    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolBar.inflateMenu(R.menu.toolbar_add_expense_menu);
    }


    private void addExpense() {
        EditText editTextExpenseName = (EditText) findViewById(R.id.editTextExpenseName);
        EditText editTextExpenseSum = (EditText) findViewById(R.id.editTextExpenseSum);

        String st = editTextExpenseName.getText().toString().replaceAll("\\s+", "");

        if (st.isEmpty()) {
            Shake.highlightEditText(editTextExpenseName);
            Toast.makeText(this, getResources().getString(R.string.expense_empty_field_name), Toast.LENGTH_LONG).show();
        }

        else {

            if (!editTextExpenseSum.getText().toString().matches(".*\\d.*")) {
                Shake.highlightEditText(editTextExpenseSum);
                Toast.makeText(this, getResources().getString(R.string.expense_empty_field_sum), Toast.LENGTH_LONG).show();
            }

            else {

                if (dataSource.checkAccountNameMatches(editTextExpenseName.getText().toString())) {
                    Shake.highlightEditText(editTextExpenseName);
                    Toast.makeText(this, getResources().getString(R.string.expense_name_exist), Toast.LENGTH_LONG).show();
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
        Intent intentFragmentMain = new Intent(FragmentMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FragmentMain.PARAM_STATUS_FRAGMENT_MAIN, FragmentMain.STATUS_UPDATE_FRAGMENT_MAIN);
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
        getMenuInflater().inflate(R.menu.toolbar_add_expense_menu, menu);
        return true;
    }
}
