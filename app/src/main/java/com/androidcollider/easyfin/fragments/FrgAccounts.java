package com.androidcollider.easyfin.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidcollider.easyfin.R;

import com.androidcollider.easyfin.adapters.AccountRecyclerAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;

import java.util.ArrayList;


public class FrgAccounts extends Fragment{
    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;

    private View view;

    private DataSource dataSource;

    private BroadcastReceiver broadcastReceiver;


    public static FrgAccounts newInstance(int page) {
        FrgAccounts frgAccounts = new FrgAccounts();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        frgAccounts.setArguments(arguments);
        return frgAccounts;
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
        view = inflater.inflate(R.layout.frg_accounts, null);

        dataSource = new DataSource(getActivity());

        setItemExpense();

        return view;
    }

    private void setItemExpense() {

        ArrayList<Account> accountArrayList = dataSource.getAllAccountsInfo();

        RecyclerView recyclerExpense = (RecyclerView) view.findViewById(R.id.recyclerExpense);

        recyclerExpense.setLayoutManager(new LinearLayoutManager(recyclerExpense.getContext()));
        recyclerExpense.setAdapter(new AccountRecyclerAdapter(getActivity(), accountArrayList));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerExpense.setItemAnimator(itemAnimator);
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(FrgMain.PARAM_STATUS_FRAGMENT_MAIN, 0);

                if (status == FrgMain.STATUS_UPDATE_FRAGMENT_MAIN) {

                    setItemExpense();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(FrgMain.BROADCAST_FRAGMENT_MAIN_ACTION);

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
