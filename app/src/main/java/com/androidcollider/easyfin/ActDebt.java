package com.androidcollider.easyfin;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidcollider.easyfin.adapters.RecyclerDebtAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Debt;

import java.util.ArrayList;

public class ActDebt extends AppCompatActivity {

    public final static String BROADCAST_DEBT_ACTION = "com.androidcollider.easyfin.debt.broadcast";
    public final static String PARAM_STATUS_DEBT = "update_debt";
    public final static int STATUS_UPDATE_DEBT = 6;

    private RecyclerView recyclerView;
    private TextView tvEmpty;

    private ArrayList<Debt> debtList = null;

    private RecyclerDebtAdapter recyclerAdapter;

    private DataSource dataSource;

    private BroadcastReceiver broadcastReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_debt);

        setToolbar(R.string.main_debt);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerDebt);

        tvEmpty = (TextView) findViewById(R.id.tvEmptyDebt);

        dataSource = new DataSource(this);

        setItemDebt();

        makeBroadcastReceiver();
    }


    private void setItemDebt() {

        debtList = dataSource.getAllDebtInfo();

        setVisibility();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerDebtAdapter(this, debtList);
        recyclerView.setAdapter(recyclerAdapter);

    }





    private void setToolbar (int id) {
        Toolbar ToolBar = (Toolbar) findViewById(R.id.toolbarMain);
        assert getSupportActionBar() != null;
        setSupportActionBar(ToolBar);
        getSupportActionBar().setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        startActivity(intent);
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS_DEBT, 0);

                if (status == STATUS_UPDATE_DEBT) {

                    debtList.clear();
                    debtList.addAll(dataSource.getAllDebtInfo());

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

}
