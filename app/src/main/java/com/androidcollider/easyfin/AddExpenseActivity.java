package com.androidcollider.easyfin;

import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FragmentMain;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.Shake;
import com.gc.materialdesign.views.ButtonRectangle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class AddExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner spinAddExpenseType, spinAddExpenseCurrency;

    ButtonRectangle btnExpenseAdd;

    DataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addexpense);

        setToolbar(R.string.new_expense);

        setSpinner();

        btnExpenseAdd = (ButtonRectangle) findViewById(R.id.btnExpenseAdd);
        btnExpenseAdd.setOnClickListener(this);

        dataSource = new DataSource(this);
    }

    private void setSpinner() {
        spinAddExpenseType = (Spinner) findViewById(R.id.spinAddExpenseType);
        spinAddExpenseCurrency = (Spinner) findViewById(R.id.spinAddExpenseCurrency);

        ArrayAdapter<?> adapterExpenseType = ArrayAdapter.createFromResource(this, R.array.expense_type_array, R.layout.spinner_item);
        adapterExpenseType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAddExpenseType.setAdapter(adapterExpenseType);

        ArrayAdapter<?> adapterExpenseCurrency = ArrayAdapter.createFromResource(this, R.array.expense_currency_array, R.layout.spinner_item);
        adapterExpenseCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAddExpenseCurrency.setAdapter(adapterExpenseCurrency);
    }

    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnExpenseAdd: {addExpense(); break;}
        }
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
    }

    private void closeActivity() {this.finish();}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivity();
        }
        return true;
    }
}
