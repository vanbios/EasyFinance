package com.androidcollider.easyfin.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidcollider.easyfin.R;

import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;


public class FragmentExpense extends Fragment{
    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    private final static int PRECISE = 100;
    private final static String FORMAT = "0.00";

    int pageNumber;

    private View view;

    private DataSource dataSource;

    private BroadcastReceiver broadcastReceiver;

    private LinearLayout linearLayout;


    public static FragmentExpense newInstance(int page) {
        FragmentExpense fragmentExpense = new FragmentExpense();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        fragmentExpense.setArguments(arguments);
        return fragmentExpense;
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
        view = inflater.inflate(R.layout.fragment_expense, null);

        dataSource = new DataSource(getActivity());

        setItemExpense();

        return view;
    }

    private void setItemExpense() {
        //int[] colors = new int[2];
        //colors[0] = getResources().getColor(R.color.silver);
        //colors[1] = getResources().getColor(R.color.gray);


        ArrayList<Account> accountArrayList = dataSource.getAllAccountsInfo();


        linearLayout = (LinearLayout) view.findViewById(R.id.linLayoutFragmentExpense);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        //int i = 0;

        for(Account account : accountArrayList) {
            View item = layoutInflater.inflate(R.layout.item_fragment_expense, linearLayout, false);

            TextView tvItemFragmentExpenseName = (TextView) item.findViewById(R.id.tvItemFragmentExpenseName);
            TextView tvItemFragmentExpenseType = (TextView) item.findViewById(R.id.tvItemFragmentExpenseType);
            TextView tvItemFragmentExpenseAmount = (TextView) item.findViewById(R.id.tvItemFragmentExpenseAmount);

            String name = account.getName();
            String type = account.getType();
            double amount = account.getAmount();
            String currency = account.getCurrency();

            tvItemFragmentExpenseName.setText(name);
            tvItemFragmentExpenseType.setText(type);
            tvItemFragmentExpenseAmount.setText(FormatUtils.doubleFormatter(amount, FORMAT, PRECISE) + " " + currency);

            item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            //item.setBackgroundColor(colors[i % 2]);
            linearLayout.addView(item);

            //i++;
        }
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(FragmentMain.PARAM_STATUS_FRAGMENT_MAIN, 0);

                if (status == FragmentMain.STATUS_UPDATE_FRAGMENT_MAIN) {

                    linearLayout.removeAllViews();

                    setItemExpense();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(FragmentMain.BROADCAST_FRAGMENT_MAIN_ACTION);

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
