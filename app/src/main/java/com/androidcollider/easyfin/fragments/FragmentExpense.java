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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.androidcollider.easyfin.AddExpenseActivity;
import com.androidcollider.easyfin.ChangeExpenseActivity;
import com.androidcollider.easyfin.R;

import com.androidcollider.easyfin.adapters.ExpenseItemAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;

import java.util.ArrayList;


public class FragmentExpense extends Fragment{
    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;

    private View view;

    private DataSource dataSource;

    private BroadcastReceiver broadcastReceiver;



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

        ArrayList<Account> accountArrayList = dataSource.getAllAccountsInfo();

        final ExpenseItemAdapter expenseItemAdapter = new ExpenseItemAdapter(getActivity(), accountArrayList);

        ListView lvExpense = (ListView) view.findViewById(R.id.lvExpense);
        lvExpense.setAdapter(expenseItemAdapter);

        lvExpense.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ChangeExpenseActivity.class);
                Account account = expenseItemAdapter.getAccount(i);

                intent.putExtra("name", account.getName());
                intent.putExtra("type", account.getType());
                intent.putExtra("amount", account.getAmount());
                intent.putExtra("currency", account.getCurrency());
                startActivity(intent);
                return true;
            }
        });
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(FragmentMain.PARAM_STATUS_FRAGMENT_MAIN, 0);

                if (status == FragmentMain.STATUS_UPDATE_FRAGMENT_MAIN) {

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
