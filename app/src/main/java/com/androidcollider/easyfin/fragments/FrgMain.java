package com.androidcollider.easyfin.fragments;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.utils.ChartDataUtils;
import com.androidcollider.easyfin.utils.FormatUtils;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;

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

import java.util.ArrayList;


public class FrgMain extends Fragment {

    private static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    public final static String BROADCAST_FRAGMENT_MAIN_ACTION = "com.androidcollider.easyfin.fragmentmain.broadcast";

    public final static String PARAM_STATUS_FRAGMENT_MAIN = "update_fragment_main_current_balance";

    public final static int STATUS_UPDATE_FRAGMENT_MAIN = 100;

    private final int PRECISE = 100;
    private final String FORMAT = "0.00";

    int pageNumber;

    private View view;

    private DataSource dataSource;

    private BroadcastReceiver broadcastReceiver;

    private Spinner spinMainPeriod;
    private Spinner spinMainBalanceCurrency;


    public static FrgMain newInstance(int page) {
        FrgMain frgMain = new FrgMain();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        frgMain.setArguments(arguments);
        return frgMain;
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
        view = inflater.inflate(R.layout.frg_main, null);

        setBalanceCurrencySpinner();

        setStatisticSpinner();

        dataSource = new DataSource(getActivity());

        setCurrentBalance(spinMainBalanceCurrency.getSelectedItemPosition());

        setTransactionsStatistic(spinMainPeriod.getSelectedItemPosition() + 1,
                spinMainBalanceCurrency.getSelectedItemPosition());

        return view;
    }

    private void setCurrentBalanceChart(double[] balance) {

        HorizontalBarChart chart = (HorizontalBarChart) view.findViewById(R.id.chartMainBalance);

        ArrayList<String> xAxisValues = ChartDataUtils.getXAxisValues();
        ArrayList<BarDataSet> barDataSet = ChartDataUtils.getDataSetMainBalanceChart(balance, getActivity());

        BarData data = new BarData(xAxisValues, barDataSet);
        chart.setData(data);
        data.setValueTextSize(12f);
        //data.setValueFormatter(new ChartValueFormatter());  //this feature will be in properties
        chart.setDescription("");
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        leftAxis.setSpaceTop(30f);
        //rightAxis.setSpaceTop(25f);
        leftAxis.setLabelCount(3);
        chart.animateXY(2000, 2000);
        chart.setTouchEnabled(false);
        chart.invalidate();
    }

    public void setCurrentBalance(int posCurrency) {
        double[] balance = getCurrentBalance(posCurrency);
        setBalanceTV(balance);
        setCurrentBalanceChart(balance);
    }

    private void setBalanceTV (double[] balance) {
        //TextView tvMainCashValue = (TextView) view.findViewById(R.id.tvMainCashValue);
        //TextView tvMainCardValue = (TextView) view.findViewById(R.id.tvMainCardValue);
        ///TextView tvMainDepositValue = (TextView) view.findViewById(R.id.tvMainDepositValue);
        //TextView tvMainDebtValue = (TextView) view.findViewById(R.id.tvMainDebtValue);
        TextView tvMainSumValue = (TextView) view.findViewById(R.id.tvMainSumValue);

        double sum = 0;
        for (double i: balance) {
            sum += i;}

        //tvMainCashValue.setText(FormatUtils.doubleFormatter(balance[0], FORMAT, PRECISE));
        //tvMainCardValue.setText(FormatUtils.doubleFormatter(balance[1], FORMAT, PRECISE));
        //tvMainDepositValue.setText(FormatUtils.doubleFormatter(balance[2], FORMAT, PRECISE));
        //tvMainDebtValue.setText(FormatUtils.doubleFormatter(balance[3], FORMAT, PRECISE));
        tvMainSumValue.setText(FormatUtils.doubleFormatter(sum, FORMAT, PRECISE) + " " + getCurrency());
    }

    private double[] getCurrentBalance(int posCurrency) {
        String[] types = getResources().getStringArray(R.array.account_type_array);
        String[] currency = getResources().getStringArray(R.array.account_currency_array);

        double[] balance = new double[4];

        for (int i = 0; i < balance.length; i++) {
            balance[i] = dataSource.getAccountsSumGroupByCurrency(types[i], currency[posCurrency]);}   //set int from spinner position


        return balance;
    }


    private void setTransactionsStatistic(int posPeriod, int posCurrency) {

        HorizontalBarChart chart = (HorizontalBarChart) view.findViewById(R.id.chartMainStatistic);

        double[] statistic = getTransStatistic(posPeriod, posCurrency);

        ArrayList<String> xAxisValues = ChartDataUtils.getXAxisValues();
        ArrayList<BarDataSet> barDataSet = ChartDataUtils.getDataSetMainStatisticChart(statistic, getActivity());

        BarData data = new BarData(xAxisValues, barDataSet);
        chart.setData(data);
        //data.setValueFormatter(new ChartValueFormatter());    //this feature will be in properties
        data.setValueTextSize(12f);
        chart.setDescription("");
        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        leftAxis.setSpaceTop(30f);
        //rightAxis.setSpaceTop(25f);
        leftAxis.setLabelCount(3);
        chart.animateXY(2000, 2000);
        chart.setTouchEnabled(false);
        chart.invalidate();


        double statsum = statistic[0] + statistic[1];

        //TextView tvMainIncomeValue = (TextView) view.findViewById(R.id.tvMainIncomeValue);
        //TextView tvMainCostValue = (TextView) view.findViewById(R.id.tvMainCostValue);
        TextView tvMainStatisticSum = (TextView) view.findViewById(R.id.tvMainStatisticSum);

        //tvMainIncomeValue.setText(FormatUtils.doubleFormatter(statistic[1], FORMAT, PRECISE));
        //tvMainCostValue.setText(FormatUtils.doubleFormatter(Math.abs(statistic[0]), FORMAT, PRECISE));
        tvMainStatisticSum.setText(FormatUtils.doubleFormatter(statsum, FORMAT, PRECISE) + " " + getCurrency());
    }

    private double[] getTransStatistic (int posPeriod, int posCurrency) {
        String[] currency = getResources().getStringArray(R.array.account_currency_array);
        return dataSource.getTransactionsStatistic(posPeriod, currency[posCurrency]);   //set int from spinner position
    }

    private String getCurrency() {
        String[] currency_array = getResources().getStringArray(R.array.account_currency_array_language);
        return currency_array[spinMainBalanceCurrency.getSelectedItemPosition()];
    }


    private void setBalanceCurrencySpinner() {
        spinMainBalanceCurrency = (Spinner) view.findViewById(R.id.spinMainCurrency);

        ArrayAdapter<?> adapterCurrency = ArrayAdapter.createFromResource(getActivity(), R.array.account_currency_array, R.layout.spin_custom_item);
        adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMainBalanceCurrency.setAdapter(adapterCurrency);

        spinMainBalanceCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                setCurrentBalance(spinMainBalanceCurrency.getSelectedItemPosition());

                setTransactionsStatistic(spinMainPeriod.getSelectedItemPosition() + 1,
                                spinMainBalanceCurrency.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setStatisticSpinner() {
        spinMainPeriod = (Spinner) view.findViewById(R.id.spinMainPeriod);

        ArrayAdapter<?> adapterStatPeriod = ArrayAdapter.createFromResource(getActivity(), R.array.main_statistic_period_array, R.layout.spin_custom_item);
        adapterStatPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMainPeriod.setAdapter(adapterStatPeriod);
        spinMainPeriod.setSelection(1);

        spinMainPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setTransactionsStatistic(spinMainPeriod.getSelectedItemPosition() + 1,
                                spinMainBalanceCurrency.getSelectedItemPosition());
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

                    setCurrentBalance(spinMainBalanceCurrency.getSelectedItemPosition());
                    setTransactionsStatistic(spinMainPeriod.getSelectedItemPosition()+1,
                                  spinMainBalanceCurrency.getSelectedItemPosition());
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
