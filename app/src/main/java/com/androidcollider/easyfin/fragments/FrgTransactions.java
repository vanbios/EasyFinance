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

import com.androidcollider.easyfin.R;

import com.androidcollider.easyfin.adapters.TransactionRecyclerAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Transaction;

import java.util.ArrayList;


public class FrgTransactions extends Fragment{

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    public final static String BROADCAST_FRAGMENT_TRANSACTION_ACTION = "com.androidcollider.easyfin.fragmenttransaction.broadcast";
    public final static String PARAM_STATUS_FRAGMENT_TRANSACTION = "update_fragment_transaction";
    public final static int STATUS_UPDATE_FRAGMENT_TRANSACTION = 200;

    int pageNumber;

    private View view;

    private BroadcastReceiver broadcastReceiver;

    private ArrayList<Transaction> transactionList = null;
    private TransactionRecyclerAdapter recyclerAdapter;

    private DataSource dataSource;



    public static FrgTransactions newInstance(int page) {
        FrgTransactions frgTransactions = new FrgTransactions();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        frgTransactions.setArguments(arguments);
        return frgTransactions;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        makeBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_transactions, container, false);

        setItemTransaction();

        return view;
    }

    private void setItemTransaction() {

        dataSource = new DataSource(getActivity());

        transactionList = dataSource.getAllTransactionsInfo();

        RecyclerView recyclerTransaction = (RecyclerView) view.findViewById(R.id.recyclerTransaction);

        recyclerTransaction.setLayoutManager(new LinearLayoutManager(recyclerTransaction.getContext()));
        recyclerAdapter = new TransactionRecyclerAdapter(getActivity(), transactionList);
        recyclerTransaction.setAdapter(recyclerAdapter);
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS_FRAGMENT_TRANSACTION, 0);

                if (status == STATUS_UPDATE_FRAGMENT_TRANSACTION) {

                    transactionList.clear();
                    transactionList.addAll(dataSource.getAllTransactionsInfo());
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_FRAGMENT_TRANSACTION_ACTION);

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
