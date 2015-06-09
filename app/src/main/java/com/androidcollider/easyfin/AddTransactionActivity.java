package com.androidcollider.easyfin;

import com.androidcollider.easyfin.adapters.MyFragmentPagerAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FragmentAddTransactionDefault;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;


public class AddTransactionActivity extends AppCompatActivity {

    private TextView tvTransactionDate;
    private DatePickerDialog setDatePickerDialog;

    private final String DATEFORMAT = "dd-MM-yyyy";

    private Spinner spinAddTransCategory, spinAddTransExpense;

    DataSource dataSource;

    private ViewPager pagerTrans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtransaction);

        setToolbar(R.string.new_transaction);

        setViewPager();

        //tvTransactionDate = (TextView) findViewById(R.id.tvTransactionDate);

        //setDateTimeField();

        dataSource = new DataSource(this);

        //setSpinner();
    }

    private void setToolbar(int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolBar.inflateMenu(R.menu.toolbar_add_transaction_menu);
    }


    private void setViewPager() {
        pagerTrans = (ViewPager) findViewById(R.id.pagerAddTransaction);
        MyFragmentPagerAdapter adapterPager = new MyFragmentPagerAdapter(getSupportFragmentManager());
        adapterPager.addFragment(new FragmentAddTransactionDefault(),
                getResources().getString(R.string.add_transaction_tab_default));
        adapterPager.addFragment(new FragmentAddTransactionDefault(),
                getResources().getString(R.string.add_transaction_tab_between));

        pagerTrans.setAdapter(adapterPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsAddTransaction);
        tabLayout.setupWithViewPager(pagerTrans);
    }





















    private void closeActivity() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                closeActivity();
                return true;}
            case R.id.add_transaction_action_save: {
                //addTransaction();
                return true;}

        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_add_transaction_menu, menu);
        MenuItem saveTransaction = menu.findItem(R.id.add_transaction_action_save);
        saveTransaction.setEnabled(true);

        List<String> accounts = dataSource.getAllAccountNames();

        if (accounts.size() == 0) {
            saveTransaction.setEnabled(false);}

        return true;
    }















    /*private void setSpinner() {
        spinAddTransCategory = (Spinner) findViewById(R.id.spinAddTransCategory);
        spinAddTransExpense = (Spinner) findViewById(R.id.spinAddTransExpense);

        List<String> accounts = dataSource.getAllAccountNames();

        if (accounts.size() == 0) {
            showDialogNoExpense();
        }


        spinAddTransCategory.setAdapter(new SpinnerCategoriesAdapter(this, R.layout.spinner_item,
                getResources().getStringArray(R.array.cat_transaction_array)));

        List<Account> accountList = dataSource.getAllAccountsInfo();

        spinAddTransExpense.setAdapter(new SpinnerAddTransExpenseAdapter(this, R.layout.spinner_item,
                accounts, accountList));
    }*/


    /*private void setDateTimeField() {
        tvTransactionDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        tvTransactionDate.setText(DateFormat.dateToString(newCalendar.getTime(), DATEFORMAT));

        setDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvTransactionDate.setText(DateFormat.dateToString(newDate.getTime(), DATEFORMAT));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }*/

    /*@Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tvTransactionDate: setDatePickerDialog.show(); break;
        }
    }

    private void addTransaction() {
        EditText editTextTransSum = (EditText) findViewById(R.id.editTextTransSum);
        RadioButton radioButtonCost = (RadioButton) findViewById(R.id.radioButtonCost);

        if (! editTextTransSum.getText().toString().matches(".*\\d.*")) {
            Shake.highlightEditText(editTextTransSum);
            Toast.makeText(this, getResources().getString(R.string.transaction_empty_amount_field), Toast.LENGTH_LONG).show();
        }

        else {

        Long date = DateFormat.stringToDate(tvTransactionDate.getText().toString(), DATEFORMAT).getTime();
        String account_name = spinAddTransExpense.getSelectedItem().toString();
        double amount = Double.parseDouble(editTextTransSum.getText().toString());
        if (radioButtonCost.isChecked()) {
            amount *= -1;}
        String category = spinAddTransCategory.getSelectedItem().toString();

        int id_account = dataSource.getAccountIdByName(account_name);
            String account_currency = dataSource.getAccountCurrencyByName(account_name);

        double accountAmount = dataSource.getAccountAmountForTransaction(id_account);

        if (radioButtonCost.isChecked() && Math.abs(amount) > accountAmount) {
            Toast.makeText(this, getResources().getString(R.string.transaction_not_enough_costs) + " " +
                    Math.abs(amount), Toast.LENGTH_LONG).show();}
        else {
            accountAmount += amount;

            Transaction transaction = new Transaction(date, amount, category, account_name, account_currency, id_account);
            dataSource.insertNewTransaction(transaction);
            dataSource.updateAccountAmountAfterTransaction(id_account, accountAmount);


            pushBroadcast();

            closeActivity();
        }
        }
    }


    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FragmentMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FragmentMain.PARAM_STATUS_FRAGMENT_MAIN, FragmentMain.STATUS_UPDATE_FRAGMENT_MAIN);
        sendBroadcast(intentFragmentMain);

        Intent intentFragmentTransaction = new Intent(FragmentTransaction.BROADCAST_FRAGMENT_TRANSACTION_ACTION);
        intentFragmentTransaction.putExtra(FragmentTransaction.PARAM_STATUS_FRAGMENT_TRANSACTION, FragmentTransaction.STATUS_UPDATE_FRAGMENT_TRANSACTION);
        sendBroadcast(intentFragmentTransaction);
    }*/



    /*private void openAddExpenseActivity() {
        Intent intent = new Intent(this, AddExpenseActivity.class);
        startActivity(intent);
    }*/

    /*private void showDialogNoExpense() {

        new MaterialDialog.Builder(this)
                .title(getString(R.string.no_expense))
                .content(getString(R.string.dialog_text_no_expense))
                .positiveText(getString(R.string.new_expense))
                .negativeText(getString(R.string.return_to_main))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        goToAddNewExpense();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        returnToMain();
                    }
                })
                .show();
    }*/

    /*public void returnToMain() {
        closeActivity();
    }

    public void goToAddNewExpense() {
        closeActivity();
        openAddExpenseActivity();
    }*/










}
