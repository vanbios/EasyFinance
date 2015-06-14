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


public class ActAccount extends AppCompatActivity {

    Spinner spinType, spinCurrency;
    EditText editName, editSum;
    private String old_name;
    int id_account;

    Intent intent;
    DataSource dataSource;

    int mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_account);

        dataSource = new DataSource(this);

        initializeFields();

        setMode();
    }

    private void initializeFields() {
        spinType = (Spinner) findViewById(R.id.spinAddAccountType);
        spinCurrency = (Spinner) findViewById(R.id.spinAddAccountCurrency);

        editName = (EditText) findViewById(R.id.editTextAccountName);
        editSum = (EditText) findViewById(R.id.editTextAccountSum);

    }

    private void setMode () {
        intent = getIntent();
        mode = intent.getIntExtra("mode", 0);

        switch (mode) {
            case 0: {setToolbar(R.string.new_account); break;}
            case 1: {setToolbar(R.string.edit_account);
                     setEdits();
                     break;}
        }

        setSpinner();
    }

    private void setSpinner() {
        spinType.setAdapter(new SpinnerAccountTypeAdapter(this, R.layout.spin_custom_item,
                getResources().getStringArray(R.array.account_type_array)));

        spinCurrency.setAdapter(new SpinnerAccountCurrencyAdapter(this, R.layout.spin_custom_item,
                getResources().getStringArray(R.array.account_currency_array)));

        if (mode == 1) {
            String[] type = getResources().getStringArray(R.array.account_type_array);

            String typeVal = getIntent().getStringExtra("type");

            for (int i = 0; i < type.length; i++) {
                if (type[i].equals(typeVal)) {
                    spinType.setSelection(i);
                }
            }

            String[] currency = getResources().getStringArray(R.array.account_currency_array);

            String currencyVal = getIntent().getStringExtra("currency");

            for (int i = 0; i < currency.length; i++) {
                if (currency[i].equals(currencyVal)) {
                    spinCurrency.setSelection(i);
                }
            }
        }
    }

    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolBar.inflateMenu(R.menu.toolbar_account_menu);
    }

    private void setEdits() {
        old_name = intent.getStringExtra("name");
        editName.setText(old_name);
        editName.setSelection(editName.getText().length());

        final int PRECISE = 100;
        final String FORMAT = "0.00";

        editSum.setText(FormatUtils.doubleFormatter(getIntent().getDoubleExtra("amount", 0.0), FORMAT, PRECISE));
        editSum.setSelection(editSum.getText().length());

        id_account = intent.getIntExtra("id_account", 0);
    }


    private void addAccount() {

        String st = editName.getText().toString().replaceAll("\\s+", "");

        if (st.isEmpty()) {
            Shake.highlightEditText(editName);
            Toast.makeText(this, getResources().getString(R.string.account_empty_field_name), Toast.LENGTH_LONG).show();
        }

        else {

            if (!editSum.getText().toString().matches(".*\\d.*")) {
                Shake.highlightEditText(editSum);
                Toast.makeText(this, getResources().getString(R.string.account_empty_field_sum), Toast.LENGTH_LONG).show();
            }

            else {

                DataSource dataSource = new DataSource(this);

                if (dataSource.checkAccountNameMatches(editName.getText().toString())) {
                    Shake.highlightEditText(editName);
                    Toast.makeText(this, getResources().getString(R.string.account_name_exist), Toast.LENGTH_LONG).show();
                }

                else {

                    String name = editName.getText().toString();
                    double amount = Double.parseDouble(editSum.getText().toString());
                    String type = spinType.getSelectedItem().toString();
                    String currency = spinCurrency.getSelectedItem().toString();

                    Account account = new Account(name, amount, type, currency);

                    dataSource.insertNewAccount(account);

                    pushBroadcast();

                    this.finish();
                }
            }
        }
    }


    private void editAccount() {

        String st = editName.getText().toString().replaceAll("\\s+", "");

        if (st.isEmpty()) {
            Shake.highlightEditText(editName);
            Toast.makeText(this, getResources().getString(R.string.account_empty_field_name), Toast.LENGTH_LONG).show();
        }

        else {

            if (!editSum.getText().toString().matches(".*\\d.*")) {
                Shake.highlightEditText(editSum);
                Toast.makeText(this, getResources().getString(R.string.account_empty_field_sum), Toast.LENGTH_LONG).show();
            }

            else {

                String s = editName.getText().toString();

                if (dataSource.checkAccountNameMatches(s) && ! s.equals(old_name)) {
                    Shake.highlightEditText(editName);
                    Toast.makeText(this, getResources().getString(R.string.account_name_exist), Toast.LENGTH_LONG).show();
                }

                else {

                    String name = editName.getText().toString();
                    double amount = Double.parseDouble(editSum.getText().toString());
                    String type = spinType.getSelectedItem().toString();
                    String currency = spinCurrency.getSelectedItem().toString();

                    Account account = new Account(id_account, name, amount, type, currency);

                    dataSource.editAccount(account);

                    pushBroadcast();

                    this.finish();
                }
            }
        }
    }

    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgMain.PARAM_STATUS_FRAGMENT_MAIN, FrgMain.STATUS_UPDATE_FRAGMENT_MAIN);
        sendBroadcast(intentFragmentMain);

        if (mode == 0) {
            Intent intentMainSnack = new Intent(MainActivity.BROADCAST_MAIN_SNACK_ACTION);
            intentMainSnack.putExtra(MainActivity.PARAM_STATUS_MAIN_SNACK, MainActivity.STATUS_MAIN_SNACK);
            sendBroadcast(intentMainSnack);
        }
    }

    private void deleteAccountDialog() {

        new MaterialDialog.Builder(this)
                .title(getString(R.string.dialog_title_delete_account))
                .content(getString(R.string.dialog_text_delete_account))
                .positiveText(getString(R.string.dialog_button_delete_account))
                .negativeText(getString(R.string.dialog_button_delete_account_cancel))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        deleteAccount();
                    }
                })
                .show();

    }

    private void deleteAccount() {
        if (dataSource.checkAccountTransactionExist(id_account)) {
            dataSource.makeAccountInvisible(id_account);
        }
        else {
            dataSource.deleteAccount(id_account);}

        pushBroadcast();
        this.finish();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;}

            case R.id.account_action_save: {

                switch (mode) {
                    case 0: {addAccount(); break;}
                    case 1: {editAccount(); break;}
                }

                return true;}

            case R.id.account_action_delete: {
                deleteAccountDialog();
                return true;}
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_account_menu, menu);
        MenuItem delete_account = menu.findItem(R.id.account_action_delete);

        switch (mode) {
            case 0: {delete_account.setVisible(false); break;}
            case 1: {delete_account.setVisible(true); break;}
        }

        return true;
    }
}
