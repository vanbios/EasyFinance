package com.androidcollider.easyfin;

import com.androidcollider.easyfin.database.DataSource;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddExpense extends AppCompatActivity implements View.OnClickListener {

    Spinner spinAddExpenseType, spinAddExpenseCurrency;
    Button btnExpenseAdd;

    DataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addexpense);

        setToolbar(R.string.new_expense);

        setSpinner();

        btnExpenseAdd = (Button) findViewById(R.id.btnExpenseAdd);
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
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);}

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnExpenseAdd: {addExpense(); break;}
        }
    }

    private void addExpense() {
        EditText editTextExpenseName = (EditText) findViewById(R.id.editTextExpenseName);
        EditText editTextExpenseSum = (EditText) findViewById(R.id.editTextExpenseSum);
        ContentValues cv = new ContentValues();

        String name = editTextExpenseName.getText().toString();
        int amount = Integer.parseInt(editTextExpenseSum.getText().toString());
        String type = spinAddExpenseType.getSelectedItem().toString();
        String currency = spinAddExpenseCurrency.getSelectedItem().toString();

        cv.put("name", name);
        cv.put("amount", amount);
        cv.put("type", type);
        cv.put("currency", currency);

        dataSource.insertLocal("Account", cv);



        Toast.makeText(this, name + " " + amount + " " + currency + " " + type, Toast.LENGTH_LONG).show();

        this.finish();
    }
}
