package com.androidcollider.easyfin.fragments;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.database.DataSource;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FragmentMain extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    public final static String BROADCAST_ACTION = "com.androidcollider.easyfin.fragmentmain.broadcast";

    public final static String PARAM_STATUS_FRAGMENT_MAIN = "update_fragment_main_current_balance";

    public final static int STATUS_UPDATE_FRAGMENT_MAIN = 100;

    int pageNumber;

    View view;

    DataSource dataSource;

    BroadcastReceiver broadcastReceiver;


    public static FragmentMain newInstance(int page) {
        FragmentMain fragmentMain = new FragmentMain();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        fragmentMain.setArguments(arguments);
        return fragmentMain;
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
        view = inflater.inflate(R.layout.fragment_main, null);

        dataSource = new DataSource(getActivity());

        setCurrentBalance();


        return view;
    }

    public void setCurrentBalance() {
        int[] balance = getCurrentBalance();
        setBalanceTV(balance);
    }

    private void setBalanceTV (int[] balance) {
        TextView tvMainCashValue = (TextView) view.findViewById(R.id.tvMainCashValue);
        TextView tvMainCardValue = (TextView) view.findViewById(R.id.tvMainCardValue);
        TextView tvMainDepositValue = (TextView) view.findViewById(R.id.tvMainDepositValue);
        TextView tvMainDebtValue = (TextView) view.findViewById(R.id.tvMainDebtValue);
        TextView tvMainSumValue = (TextView) view.findViewById(R.id.tvMainSumValue);

        int sum = 0;
        for (int i: balance) {
            sum += i;}

        tvMainCashValue.setText(Integer.toString(balance[0]));
        tvMainCardValue.setText(Integer.toString(balance[1]));
        tvMainDepositValue.setText(Integer.toString(balance[2]));
        tvMainDebtValue.setText(Integer.toString(balance[3]));
        tvMainSumValue.setText(Integer.toString(sum));
    }

    private int[] getCurrentBalance() {
        String[] types = getResources().getStringArray(R.array.expense_type_array);

        int[] balance = new int[4];

        for (int i = 0; i < balance.length; i++) {
            balance[i] = dataSource.getExpenseSum(types[i]);}


        return balance;
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS_FRAGMENT_MAIN, 0);

                if (status == STATUS_UPDATE_FRAGMENT_MAIN) {

                    setCurrentBalance();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
