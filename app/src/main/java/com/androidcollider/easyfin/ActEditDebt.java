package com.androidcollider.easyfin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.Debt;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;

public class ActEditDebt extends AppCompatActivity {

    private TextView tvDebtName;
    private EditText etSum;
    private Spinner spinAccount;

    private Intent intent;

    private ArrayList<Account> accountList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_edit_debt);

        accountList = InfoFromDB.getInstance().getAccountList();

        initializeView();

        intent = getIntent();

        setView();

        setToolbar(R.string.pay_all_debt);
    }


    private void initializeView() {
        tvDebtName = (TextView) findViewById(R.id.tvEditDebtName);
        etSum = (EditText) findViewById(R.id.editTextEditDebtSum);
        spinAccount = (Spinner) findViewById(R.id.spinEditDebtAccount);
    }

    private void setView() {

        Debt debt = (Debt) intent.getSerializableExtra("debt");

        tvDebtName.setText(debt.getName());

        final int PRECISE = 100;
        final String FORMAT = "0.00";

        etSum.setText(FormatUtils.doubleFormatter(debt.getAmount(), FORMAT, PRECISE));
        etSum.setSelection(etSum.getText().length());
        etSum.setEnabled(false);

        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                this,
                R.layout.spin_head_icon_text,
                accountList));

        int idAccount = debt.getIdAccount();

        int pos = 0;

        for (int i = 0; i < accountList.size(); i++) {

            if (idAccount == accountList.get(i).getId()) {
                pos = i;
            }
        }

        spinAccount.setSelection(pos);

    }


    private void setToolbar (int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ToolBar.inflateMenu(R.menu.toolbar_debt_menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;}
            case R.id.debt_action_save: {

                return true;}

        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_debt_menu, menu);
        MenuItem saveDebtItem = menu.findItem(R.id.debt_action_save);
        saveDebtItem.setEnabled(true);

        /*if (accountList.isEmpty()) {
            saveDebtItem.setVisible(false);}*/

        return true;
    }
}
