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
import android.widget.ListView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.TransactionItemAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Transaction;

import java.util.ArrayList;


public class FragmentTransaction extends Fragment{

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    public final static String BROADCAST_FRAGMENT_TRANSACTION_ACTION = "com.androidcollider.easyfin.fragmenttransaction.broadcast";

    public final static String PARAM_STATUS_FRAGMENT_TRANSACTION = "update_fragment_transaction";

    public final static int STATUS_UPDATE_FRAGMENT_TRANSACTION = 200;

    int pageNumber;

    private View view;

    private DataSource dataSource;

    private BroadcastReceiver broadcastReceiver;


    public static FragmentTransaction newInstance(int page) {
        FragmentTransaction fragmentTransaction = new FragmentTransaction();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        fragmentTransaction.setArguments(arguments);
        return fragmentTransaction;
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
        view = inflater.inflate(R.layout.fragment_transaction, null);

        dataSource = new DataSource(getActivity());

        setItemTransaction();

        return view;
    }

    private void setItemTransaction() {
        ArrayList<Transaction> transactionArrayList = dataSource.getAllTransactionsInfo();

        TransactionItemAdapter transactionItemAdapter = new TransactionItemAdapter(getActivity(), transactionArrayList);

        ListView lvTransaction = (ListView) view.findViewById(R.id.lvTransaction);
        lvTransaction.setAdapter(transactionItemAdapter);
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS_FRAGMENT_TRANSACTION, 0);

                if (status == STATUS_UPDATE_FRAGMENT_TRANSACTION) {

                    setItemTransaction();
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
