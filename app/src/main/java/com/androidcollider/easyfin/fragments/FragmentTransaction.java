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
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;


public class FragmentTransaction extends Fragment{

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    private final static int PRECISE = 100;
    private final static String FORMAT = "0.00";

    public final static String BROADCAST_FRAGMENT_TRANSACTION_ACTION = "com.androidcollider.easyfin.fragmenttransaction.broadcast";

    public final static String PARAM_STATUS_FRAGMENT_TRANSACTION = "update_fragment_transaction";

    public final static int STATUS_UPDATE_FRAGMENT_TRANSACTION = 200;

    int pageNumber;

    private View view;

    private DataSource dataSource;

    private BroadcastReceiver broadcastReceiver;

    private LinearLayout linearLayout;


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

        linearLayout = (LinearLayout) view.findViewById(R.id.linLayoutFragmentTransaction);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        int i = 0;

        for (Transaction transaction : transactionArrayList) {

            View item = layoutInflater.inflate(R.layout.item_fragment_transaction, linearLayout, false);

            TextView tvItemFragmentTransactionAmount = (TextView) item.findViewById(R.id.tvItemFragmentTransactionAmount);
            TextView tvItemFragmentTransactionAccountName = (TextView) item.findViewById(R.id.tvItemFragmentTransactionAccountName);
            TextView tvItemFragmentTransactionCategory = (TextView) item.findViewById(R.id.tvItemFragmentTransactionCategory);
            TextView tvItemFragmentTransactionDate = (TextView) item.findViewById(R.id.tvItemFragmentTransactionDate);

            double amount = transaction.getAmount();
            String date = transaction.getDate();
            String category = transaction.getCategory();
            String account_name = transaction.getAccount_name();
            String account_currency = transaction.getAccount_currency();

            tvItemFragmentTransactionAccountName.setText(account_name);
            tvItemFragmentTransactionDate.setText(date);
            tvItemFragmentTransactionCategory.setText(category);
            tvItemFragmentTransactionAmount.setText(FormatUtils.doubleFormatter(amount, FORMAT, PRECISE) + " " + account_currency);

            if (FormatUtils.doubleFormatter(amount, FORMAT, PRECISE).contains("-")) {
                tvItemFragmentTransactionAmount.setTextColor(getResources().getColor(R.color.red));
            }
            else {
                tvItemFragmentTransactionAmount.setTextColor(getResources().getColor(R.color.green));
            }

            item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            linearLayout.addView(item);

            i++;

            if (i>=50) {
                break;}
        }
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS_FRAGMENT_TRANSACTION, 0);

                if (status == STATUS_UPDATE_FRAGMENT_TRANSACTION) {

                    linearLayout.removeAllViews();

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
