package com.androidcollider.easyfin.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidcollider.easyfin.R;

import com.androidcollider.easyfin.adapters.RecyclerTransactionAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Transaction;

import java.util.ArrayList;


public class FrgTransactions extends Fragment{

    public final static String BROADCAST_FRG_TRANSACTION_ACTION = "com.androidcollider.easyfin.frgtransaction.broadcast";
    public final static String PARAM_STATUS_FRG_TRANSACTION = "update_frg_transaction";
    public final static int STATUS_UPDATE_FRG_TRANSACTION = 3;

    private RecyclerView recyclerView;
    private TextView tvEmpty;

    private BroadcastReceiver broadcastReceiver;

    private ArrayList<Transaction> transactionList = null;
    private RecyclerTransactionAdapter recyclerAdapter;

    private DataSource dataSource;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_transactions, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerTransaction);

        tvEmpty = (TextView) view.findViewById(R.id.tvEmptyTransactions);

        setItemTransaction();

        makeBroadcastReceiver();

        return view;
    }

    private void setItemTransaction() {

        dataSource = new DataSource(getActivity());

        transactionList = dataSource.getAllTransactionsInfo();

        setVisibility();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerTransactionAdapter(getActivity(), transactionList);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS_FRG_TRANSACTION, 0);

                if (status == STATUS_UPDATE_FRG_TRANSACTION) {

                    transactionList.clear();
                    transactionList.addAll(dataSource.getAllTransactionsInfo());

                    setVisibility();

                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_FRG_TRANSACTION_ACTION);

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void setVisibility() {
        if (transactionList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }

        else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
