package com.androidcollider.easyfin;

import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FragmentMain;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.FormatUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class ChangeExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner spinAddExpenseType, spinAddExpenseCurrency;
    Button btnExpenseAdd;

    EditText editTextExpenseName, editTextExpenseSum;

    DataSource dataSource;

    private String OLDNAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_expense);

        setToolbar(R.string.change_expense);

        OLDNAME = getIntent().getStringExtra("name");

        setSpinner();

        editTextExpenseName = (EditText) findViewById(R.id.editTextExpenseNameChange);
        editTextExpenseSum = (EditText) findViewById(R.id.editTextExpenseSumChange);

        editTextExpenseName.setText(getIntent().getStringExtra("name"));
        editTextExpenseName.setSelection(editTextExpenseName.getText().length());

        //editTextExpenseSum.setText(FormatUtils.doubleFormatter(getIntent().getDoubleExtra("amount", 0.0), FORMAT, PRECISE));
        editTextExpenseSum.setText(Double.toString(getIntent().getDoubleExtra("amount", 0.0)));
        editTextExpenseSum.setSelection(editTextExpenseSum.getText().length());

        btnExpenseAdd = (Button) findViewById(R.id.btnExpenseChange);
        btnExpenseAdd.setOnClickListener(this);

        dataSource = new DataSource(this);
    }

    private void setSpinner() {
        spinAddExpenseType = (Spinner) findViewById(R.id.spinAddExpenseTypeChange);
        spinAddExpenseCurrency = (Spinner) findViewById(R.id.spinAddExpenseCurrencyChange);

        ArrayAdapter<?> adapterExpenseType = ArrayAdapter.createFromResource(this, R.array.expense_type_array, R.layout.spinner_item);
        adapterExpenseType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAddExpenseType.setAdapter(adapterExpenseType);

        ArrayAdapter<?> adapterExpenseCurrency = ArrayAdapter.createFromResource(this, R.array.expense_currency_array, R.layout.spinner_item);
        adapterExpenseCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAddExpenseCurrency.setAdapter(adapterExpenseCurrency);

        String[] type = getResources().getStringArray(R.array.expense_type_array);

        String typeVal = getIntent().getStringExtra("type");

        for (int i = 0; i < type.length; i++) {
            if (type[i].equals(typeVal)) {
                spinAddExpenseType.setSelection(i);
            }
        }

        String[] currency = getResources().getStringArray(R.array.expense_currency_array);

        String currencyVal = getIntent().getStringExtra("currency");

        for (int i = 0; i < currency.length; i++) {
            if (currency[i].equals(currencyVal)) {
                spinAddExpenseCurrency.setSelection(i);
            }
        }
    }

    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);}

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnExpenseChange: {addExpense(); break;}
        }
    }

    private void addExpense() {

        String name = editTextExpenseName.getText().toString();
        double amount = Double.parseDouble(editTextExpenseSum.getText().toString());
        String type = spinAddExpenseType.getSelectedItem().toString();
        String currency = spinAddExpenseCurrency.getSelectedItem().toString();

        Account account = new Account(name, amount, type, currency);

        dataSource.changeAccount(OLDNAME, account);

        pushBroadcast();

        this.finish();
    }

    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FragmentMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FragmentMain.PARAM_STATUS_FRAGMENT_MAIN, FragmentMain.STATUS_UPDATE_FRAGMENT_MAIN);
        sendBroadcast(intentFragmentMain);
    }
}
