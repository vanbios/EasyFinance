package com.androidcollider.easyfin;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FragmentMain;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.FormatUtils;
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


public class ChangeExpenseActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner spinExpenseTypeChange, spinExpenseCurrencyChange;
    ButtonRectangle btnExpenseChange, btnExpenseDelete;

    EditText editTextExpenseNameChange, editTextExpenseSumChange;

    DataSource dataSource;

    private String OLDNAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_expense);

        setToolbar(R.string.change_expense);

        OLDNAME = getIntent().getStringExtra("name");

        setSpinner();

        editTextExpenseNameChange = (EditText) findViewById(R.id.editTextExpenseNameChange);
        editTextExpenseSumChange = (EditText) findViewById(R.id.editTextExpenseSumChange);

        editTextExpenseNameChange.setText(getIntent().getStringExtra("name"));
        editTextExpenseNameChange.setSelection(editTextExpenseNameChange.getText().length());

        final int PRECISE = 100;
        final String FORMAT = "0.00";

        editTextExpenseSumChange.setText(FormatUtils.doubleFormatter(getIntent().getDoubleExtra("amount", 0.0), FORMAT, PRECISE));
        editTextExpenseSumChange.setSelection(editTextExpenseSumChange.getText().length());

        btnExpenseChange = (ButtonRectangle) findViewById(R.id.btnExpenseChange);
        btnExpenseChange.setOnClickListener(this);

        btnExpenseDelete = (ButtonRectangle) findViewById(R.id.btnExpenseDelete);
        btnExpenseDelete.setOnClickListener(this);

        dataSource = new DataSource(this);
    }

    private void setSpinner() {
        spinExpenseTypeChange = (Spinner) findViewById(R.id.spinExpenseTypeChange);
        spinExpenseCurrencyChange = (Spinner) findViewById(R.id.spinExpenseCurrencyChange);

        ArrayAdapter<?> adapterExpenseType = ArrayAdapter.createFromResource(this, R.array.expense_type_array, R.layout.spinner_item);
        adapterExpenseType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinExpenseTypeChange.setAdapter(adapterExpenseType);

        ArrayAdapter<?> adapterExpenseCurrency = ArrayAdapter.createFromResource(this, R.array.expense_currency_array, R.layout.spinner_item);
        adapterExpenseCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinExpenseCurrencyChange.setAdapter(adapterExpenseCurrency);

        String[] type = getResources().getStringArray(R.array.expense_type_array);

        String typeVal = getIntent().getStringExtra("type");

        for (int i = 0; i < type.length; i++) {
            if (type[i].equals(typeVal)) {
                spinExpenseTypeChange.setSelection(i);
            }
        }

        String[] currency = getResources().getStringArray(R.array.expense_currency_array);

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnExpenseChange: {changeExpense(); break;}
            case R.id.btnExpenseDelete: {deleteExpenseDialog(); break;}
        }
    }

    private void changeExpense() {

        String st = editTextExpenseNameChange.getText().toString().replaceAll("\\s+", "");

        if (st.isEmpty()) {
            Shake.highlightEditText(editTextExpenseNameChange);
            Toast.makeText(this, getResources().getString(R.string.expense_empty_field_name), Toast.LENGTH_LONG).show();
        }

        else {

            if (!editTextExpenseSumChange.getText().toString().matches(".*\\d.*")) {
                Shake.highlightEditText(editTextExpenseSumChange);
                Toast.makeText(this, getResources().getString(R.string.expense_empty_field_sum), Toast.LENGTH_LONG).show();
            }

            else {

                String s = editTextExpenseNameChange.getText().toString();

                if (dataSource.checkAccountNameMatches(s) && ! s.equals(OLDNAME)) {
                    Shake.highlightEditText(editTextExpenseNameChange);
                    Toast.makeText(this, getResources().getString(R.string.expense_name_exist), Toast.LENGTH_LONG).show();
                }

                else {

                    String name = editTextExpenseNameChange.getText().toString();
                    double amount = Double.parseDouble(editTextExpenseSumChange.getText().toString());
                    String type = spinExpenseTypeChange.getSelectedItem().toString();
                    String currency = spinExpenseCurrencyChange.getSelectedItem().toString();

                    Account account = new Account(name, amount, type, currency);

                    dataSource.changeAccount(OLDNAME, account);

                    pushBroadcast();

                    closeActivity();
                }
            }
        }
    }

    private void deleteExpenseDialog() {

        new MaterialDialog.Builder(this)
                .title(getString(R.string.dialog_title_delete_expense))
                .content(getString(R.string.dialog_text_delete_expense))
                .positiveText(getString(R.string.dialog_button_delete_expense))
                .negativeText(getString(R.string.dialog_button_delete_expense_cancel))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        deleteExpense();
                        closeActivity();
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
