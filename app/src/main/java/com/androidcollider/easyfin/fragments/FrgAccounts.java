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

import com.androidcollider.easyfin.adapters.AccountRecyclerAdapter;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.InfoFromDB;

import java.util.ArrayList;


public class FrgAccounts extends Fragment{

    private RecyclerView recyclerView;

    private TextView tvEmpty;

    private AccountRecyclerAdapter recyclerAdapter;
    private ArrayList<Account> accountList = null;

    private BroadcastReceiver broadcastReceiver;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_accounts, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerAccount);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmptyAccounts);

        setItemAccount();

        makeBroadcastReceiver();

        return view;
    }

    private void setItemAccount() {

        accountList = InfoFromDB.getInstance().getAccountList();

        setVisibility();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new AccountRecyclerAdapter(getActivity(), accountList);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(FrgMain.PARAM_STATUS_FRAGMENT_MAIN, 0);

                if (status == FrgMain.STATUS_UPDATE_FRAGMENT_MAIN) {

                    accountList.clear();
                    accountList.addAll(InfoFromDB.getInstance().getAccountList());

                    setVisibility();

                    recyclerAdapter.notifyDataSetChanged();
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

    private void setVisibility() {
        if (accountList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }

        else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
