package com.androidcollider.easyfin;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.adapters.RecyclerDebtAdapter;
import com.androidcollider.easyfin.fragments.FrgAccounts;
import com.androidcollider.easyfin.fragments.FrgHome;
import com.androidcollider.easyfin.objects.Debt;
import com.androidcollider.easyfin.objects.InfoFromDB;

import java.util.ArrayList;

public class ActDebt extends AppCompatActivity {

    public final static String BROADCAST_DEBT_ACTION = "com.androidcollider.easyfin.debt.broadcast";
    public final static String PARAM_STATUS_DEBT = "update_debt";
    public final static int STATUS_UPDATE_DEBT = 6;

    private RecyclerView recyclerView;
    private TextView tvEmpty;

    private ArrayList<Debt> debtList = null;

    private RecyclerDebtAdapter recyclerAdapter;

    private BroadcastReceiver broadcastReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_debt);

        setToolbar(R.string.debts);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerDebt);

        tvEmpty = (TextView) findViewById(R.id.tvEmptyDebt);

        setItemDebt();

        registerForContextMenu(recyclerView);

        makeBroadcastReceiver();
    }

    private void setItemDebt() {

        debtList = InfoFromDB.getInstance().getDataSource().getAllDebtInfo();

        setVisibility();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerDebtAdapter(this, debtList);
        recyclerView.setAdapter(recyclerAdapter);

    }

    private void setToolbar (int id) {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(id);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setVisibility() {
        if (debtList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }

        else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;}
        }
        return false;
    }

    public void goToAddDebtAct(View view) {
        Intent intent = new Intent(this, ActAddDebt.class);
        intent.putExtra("mode", 0);
        startActivity(intent);
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS_DEBT, 0);

                if (status == STATUS_UPDATE_DEBT) {

                    debtList.clear();
                    debtList.addAll(InfoFromDB.getInstance().getDataSource().getAllDebtInfo());

                    setVisibility();

                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_DEBT_ACTION);

        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int pos;

        try {
            pos = (int) recyclerAdapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {

            case R.id.ctx_menu_pay_all_debt: {
                goToActPayDebt(pos, 1);
                break;}

            case R.id.ctx_menu_pay_part_debt: {
                goToActPayDebt(pos, 2);
                break;}

            case R.id.ctx_menu_take_more_debt: {
                goToActPayDebt(pos, 3);
                break;}

            case R.id.ctx_menu_edit_debt: {
                editDebtAct(pos, 1);
                break;
            }

            case R.id.ctx_menu_delete_debt: {
                showDialogDeleteDebt(pos);
                break;}
        }

        return super.onContextItemSelected(item);
    }

    private void showDialogDeleteDebt(final int pos) {

        new MaterialDialog.Builder(this)
                .title(getString(R.string.delete_debt))
                .content(getString(R.string.debt_delete_warning))
                .positiveText(getString(R.string.delete))
                .negativeText(getString(R.string.cancel))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        deleteDebt(pos);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                })
                .cancelable(false)
                .show();
    }

    private void deleteDebt(int pos) {

        Debt debt = debtList.get(pos);
        int idAccount = debt.getIdAccount();
        int idDebt = debt.getId();
        double amount = debt.getAmountCurrent();
        int type = debt.getType();


        InfoFromDB.getInstance().getDataSource().deleteDebt(idAccount, idDebt, amount, type);

        debtList.remove(pos);

        setVisibility();

        recyclerAdapter.notifyDataSetChanged();

        InfoFromDB.getInstance().updateAccountList();

        pushBroadcast();
    }

    private void pushBroadcast() {
        Intent intentFrgMain = new Intent(FrgHome.BROADCAST_FRG_MAIN_ACTION);
        intentFrgMain.putExtra(FrgHome.PARAM_STATUS_FRG_MAIN, FrgHome.STATUS_UPDATE_FRG_MAIN_BALANCE);
        sendBroadcast(intentFrgMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        sendBroadcast(intentFrgAccounts);
    }

    private void goToActPayDebt(int pos, int mode){
        Intent intent = new Intent(this, ActPayDebt.class);

        Debt debt = debtList.get(pos);

        intent.putExtra("debt", debt);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }

    public void editDebtAct(int pos, int mode) {
        Intent intent = new Intent(this, ActAddDebt.class);

        Debt debt = debtList.get(pos);

        intent.putExtra("debt", debt);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }

}
