package com.androidcollider.easyfin;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.fragments.FrgAccounts;
import com.androidcollider.easyfin.fragments.FrgMain;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.EditTextAmountWatcher;
import com.androidcollider.easyfin.utils.FormatUtils;
import com.androidcollider.easyfin.utils.Shake;
import com.androidcollider.easyfin.utils.SharedPref;

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

    private Spinner spinType, spinCurrency;
    private EditText etName, etSum;
    private String oldName;
    private int idAccount;

    private int mode;

    private Account accFrIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_account);

        initializeFields();

        setMode();
    }

    private void initializeFields() {
        spinType = (Spinner) findViewById(R.id.spinAddAccountType);
        spinCurrency = (Spinner) findViewById(R.id.spinAddAccountCurrency);

        etName = (EditText) findViewById(R.id.editTextAccountName);
        etSum = (EditText) findViewById(R.id.editTextAccountSum);
        etSum.addTextChangedListener(new EditTextAmountWatcher(etSum));
    }

    private void setMode () {
        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);

        switch (mode) {
            case 0: {setToolbar(R.string.new_account); break;}
            case 1: {setToolbar(R.string.edit_account);
                     accFrIntent = (Account) intent.getSerializableExtra("account");
                     setEdits();
                     break;}
        }

        setSpinner();
    }

    private void setSpinner() {

        spinType.setAdapter(new SpinIconTextHeadAdapter(
                this,
                R.layout.spin_head_icon_text,
                R.id.tvSpinHeadIconText,
                R.id.ivSpinHeadIconText,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                getResources().getStringArray(R.array.account_type_array),
                getResources().obtainTypedArray(R.array.account_type_icons)));


        spinCurrency.setAdapter(new SpinIconTextHeadAdapter(
                this,
                R.layout.spin_head_icon_text,
                R.id.tvSpinHeadIconText,
                R.id.ivSpinHeadIconText,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                getResources().getStringArray(R.array.account_currency_array),
                getResources().obtainTypedArray(R.array.flag_icons)));

        if (mode == 1) {
            String[] typeArray = getResources().getStringArray(R.array.account_type_array);

            String typeVal = accFrIntent.getType();

            for (int i = 0; i < typeArray.length; i++) {
                if (typeArray[i].equals(typeVal)) {
                    spinType.setSelection(i);
                }
            }

            String[] currencyArray = getResources().getStringArray(R.array.account_currency_array);

            String currencyVal = accFrIntent.getCurrency();

            for (int i = 0; i < currencyArray.length; i++) {
                if (currencyArray[i].equals(currencyVal)) {
                    spinCurrency.setSelection(i);
                }
            }

            spinCurrency.setEnabled(false);
        }
    }

    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolBar.inflateMenu(R.menu.toolbar_account_menu);
    }

    private void setEdits() {

        oldName = accFrIntent.getName();
        etName.setText(oldName);
        etName.setSelection(etName.getText().length());

        final int PRECISE = 100;
        final String FORMAT = "0.00";

        etSum.setText(FormatUtils.doubleToStringFormatter(accFrIntent.getAmount(), FORMAT, PRECISE));
        etSum.setSelection(etSum.getText().length());

        idAccount = accFrIntent.getId();
    }

    private void addAccount() {

        if(checkForFillNameSumFields()) {

            String name = etName.getText().toString();

                if (InfoFromDB.getInstance().checkForAccountNameMatches(name)) {
                    Shake.highlightEditText(etName);
                    Toast.makeText(this, getResources().getString(R.string.account_name_exist), Toast.LENGTH_SHORT).show();
                }

                else {

                    double amount = Double.parseDouble(FormatUtils.prepareStringToParse(etSum.getText().toString()));
                    String type = spinType.getSelectedItem().toString();
                    String currency = spinCurrency.getSelectedItem().toString();

                    Account account = new Account(name, amount, type, currency);

                    MainActivity.dataSource.insertNewAccount(account);

                    lastActions();
                }
            }
        }

    private void editAccount() {

        if (checkForFillNameSumFields()) {

                String name = etName.getText().toString();

                if (InfoFromDB.getInstance().checkForAccountNameMatches(name) && ! name.equals(oldName)) {
                    Shake.highlightEditText(etName);
                    Toast.makeText(this, getResources().getString(R.string.account_name_exist), Toast.LENGTH_SHORT).show();
                }

                else {

                    String sum = FormatUtils.prepareStringToParse(etSum.getText().toString());

                    double amount = Double.parseDouble(sum);
                    String type = spinType.getSelectedItem().toString();
                    String currency = spinCurrency.getSelectedItem().toString();

                    Account account = new Account(idAccount, name, amount, type, currency);

                    MainActivity.dataSource.editAccount(account);

                    lastActions();
                }
            }
        }

    private void lastActions() {
        InfoFromDB.getInstance().updateAccountList();
        pushBroadcast();
        this.finish();
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

    private void pushBroadcast() {
        Intent intentFrgMain = new Intent(FrgMain.BROADCAST_FRG_MAIN_ACTION);
        intentFrgMain.putExtra(FrgMain.PARAM_STATUS_FRG_MAIN, FrgMain.STATUS_UPDATE_FRG_MAIN_BALANCE);
        sendBroadcast(intentFrgMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        sendBroadcast(intentFrgAccounts);


        if (mode == 0) {
            SharedPref sp = new SharedPref(this);
            if(!sp.isSnackBarAccountDisable()) {

                Intent intentMainSnack = new Intent(MainActivity.BROADCAST_MAIN_SNACK_ACTION);
                intentMainSnack.putExtra(MainActivity.PARAM_STATUS_MAIN_SNACK, MainActivity.STATUS_MAIN_SNACK);
                sendBroadcast(intentMainSnack);
            }
        }
    }

    private void showDeleteAccountDialog() {

        new MaterialDialog.Builder(this)
                .title(getString(R.string.dialog_title_delete_account))
                .content(getString(R.string.dialog_text_delete_account))
                .positiveText(getString(R.string.delete))
                .negativeText(getString(R.string.cancel))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        deleteAccount();
                    }
                })
                .show();
    }

    private void deleteAccount() {
        if (MainActivity.dataSource.checkAccountForTransactionOrDebtExist(idAccount)) {
            MainActivity.dataSource.makeAccountInvisible(idAccount);
        }
        else {
            MainActivity.dataSource.deleteAccount(idAccount);}

        lastActions();
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
                showDeleteAccountDialog();
                return true;}
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_account_menu, menu);
        MenuItem deleteAccountItem = menu.findItem(R.id.account_action_delete);

        switch (mode) {
            case 0: {deleteAccountItem.setVisible(false); break;}
            case 1: {deleteAccountItem.setVisible(true); break;}
        }

        return true;
    }

}
