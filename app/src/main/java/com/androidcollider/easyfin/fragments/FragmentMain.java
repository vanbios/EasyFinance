package com.androidcollider.easyfin.fragments;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.utils.FormatUtils;

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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;



public class FragmentMain extends Fragment {

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    public final static String BROADCAST_FRAGMENT_MAIN_ACTION = "com.androidcollider.easyfin.fragmentmain.broadcast";

    public final static String PARAM_STATUS_FRAGMENT_MAIN = "update_fragment_main_current_balance";

    public final static int STATUS_UPDATE_FRAGMENT_MAIN = 100;

    private final static int PRECISE = 100;
    private final static String FORMAT = "0.00";

    int pageNumber;

    private View view;

    private DataSource dataSource;

    private BroadcastReceiver broadcastReceiver;

    private Spinner spinMainPeriod;


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

        setStatisticSpinner();

        dataSource = new DataSource(getActivity());

        setCurrentBalance();

        setTransactionsStatistic(spinMainPeriod.getSelectedItemPosition() + 1);


        return view;
    }

    public void setCurrentBalance() {
        double[] balance = getCurrentBalance();
        setBalanceTV(balance);
    }

    private void setBalanceTV (double[] balance) {
        TextView tvMainCashValue = (TextView) view.findViewById(R.id.tvMainCashValue);
        TextView tvMainCardValue = (TextView) view.findViewById(R.id.tvMainCardValue);
        TextView tvMainDepositValue = (TextView) view.findViewById(R.id.tvMainDepositValue);
        TextView tvMainDebtValue = (TextView) view.findViewById(R.id.tvMainDebtValue);
        TextView tvMainSumValue = (TextView) view.findViewById(R.id.tvMainSumValue);

        double sum = 0;
        for (double i: balance) {
            sum += i;}

        tvMainCashValue.setText(FormatUtils.doubleFormatter(balance[0], FORMAT, PRECISE));
        tvMainCardValue.setText(FormatUtils.doubleFormatter(balance[1], FORMAT, PRECISE));
        tvMainDepositValue.setText(FormatUtils.doubleFormatter(balance[2], FORMAT, PRECISE));
        tvMainDebtValue.setText(FormatUtils.doubleFormatter(balance[3], FORMAT, PRECISE));
        tvMainSumValue.setText(FormatUtils.doubleFormatter(sum, FORMAT, PRECISE));
    }

    private double[] getCurrentBalance() {
        String[] types = getResources().getStringArray(R.array.expense_type_array);

        double[] balance = new double[4];

        for (int i = 0; i < balance.length; i++) {
            balance[i] = dataSource.getExpenseSum(types[i]);}


        return balance;
    }

    private void setTransactionsStatistic(int position) {

        double[] statistic = dataSource.getTransactionsStatistic(position);

        double statsum = statistic[0] + statistic[1];

        TextView tvMainIncomeValue = (TextView) view.findViewById(R.id.tvMainIncomeValue);
        TextView tvMainCostValue = (TextView) view.findViewById(R.id.tvMainCostValue);
        TextView tvMainStatisticSum = (TextView) view.findViewById(R.id.tvMainStatisticSum);

        tvMainIncomeValue.setText(FormatUtils.doubleFormatter(statistic[1], FORMAT, PRECISE));
        tvMainCostValue.setText(FormatUtils.doubleFormatter(Math.abs(statistic[0]), FORMAT, PRECISE));
        tvMainStatisticSum.setText(FormatUtils.doubleFormatter(statsum, FORMAT, PRECISE));

    }

    private void setStatisticSpinner() {
        spinMainPeriod = (Spinner) view.findViewById(R.id.spinMainPeriod);

        ArrayAdapter<?> adapterStatPeriod = ArrayAdapter.createFromResource(getActivity(), R.array.main_statistic_period_array, R.layout.spinner_item);
        adapterStatPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMainPeriod.setAdapter(adapterStatPeriod);

        spinMainPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setTransactionsStatistic(spinMainPeriod.getSelectedItemPosition() + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS_FRAGMENT_MAIN, 0);

                if (status == STATUS_UPDATE_FRAGMENT_MAIN) {

                    setCurrentBalance();

                    setTransactionsStatistic(spinMainPeriod.getSelectedItemPosition()+1);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_FRAGMENT_MAIN_ACTION);

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
