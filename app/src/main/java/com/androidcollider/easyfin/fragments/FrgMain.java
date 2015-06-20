package com.androidcollider.easyfin.fragments;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinnerSimpleCustomAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.utils.ChartDataUtils;
import com.androidcollider.easyfin.utils.FormatUtils;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;

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


public class FrgMain extends Fragment {

    public final static String BROADCAST_FRAGMENT_MAIN_ACTION = "com.androidcollider.easyfin.fragmentmain.broadcast";
    public final static String PARAM_STATUS_FRAGMENT_MAIN = "update_fragment_main_current_balance";
    public final static int STATUS_UPDATE_FRAGMENT_MAIN = 100;

    private final int PRECISE = 100;
    private final String FORMAT = "0.00";

    private View view;

    private DataSource dataSource;

    private BroadcastReceiver broadcastReceiver;

    private Spinner spinPeriod, spinBalanceCurrency, spinChartType;

    private HorizontalBarChart chartStat;

    private PieChart pieChart;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_main, container, false);

        makeBroadcastReceiver();

        chartStat = (HorizontalBarChart) view.findViewById(R.id.chartHBarMainStatistic);
        pieChart = (PieChart) view.findViewById(R.id.chartPieMainStatistic);

        setBalanceCurrencySpinner();

        setStatisticSpinner();

        dataSource = new DataSource(getActivity());

        setCurrentBalance(spinBalanceCurrency.getSelectedItemPosition());

        setTransactionsStatistic(spinPeriod.getSelectedItemPosition() + 1,
                spinBalanceCurrency.getSelectedItemPosition());


        setChartTypeSpinner();

        return view;
    }

    private void setCurrentBalanceChart(double[] balance) {

        HorizontalBarChart chart = (HorizontalBarChart) view.findViewById(R.id.chartMainBalance);

        BarData data = ChartDataUtils.getDataSetMainBalanceHorizontalBarChart(balance, getActivity());
        chart.setData(data);
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

        TextView tvMainSumValue = (TextView) view.findViewById(R.id.tvMainSumValue);

        double sum = 0;
        for (double i: balance) {
            sum += i;}

        tvMainSumValue.setText(FormatUtils.doubleFormatter(sum, FORMAT, PRECISE) + " " + getCurrency());
    }

    private double[] getCurrentBalance(int posCurrency) {
        String[] type = getResources().getStringArray(R.array.account_type_array);
        String[] currency = getResources().getStringArray(R.array.account_currency_array);

        double[] balance = new double[4];

        for (int i = 0; i < balance.length; i++) {
            balance[i] = dataSource.getAccountsSumGroupByCurrency(type[i], currency[posCurrency]);}


        return balance;
    }


    private void setTransactionsStatistic(int posPeriod, int posCurrency) {



        double[] statistic = getTransStatistic(posPeriod, posCurrency);


        BarData data = ChartDataUtils.getDataSetMainStatisticHorizontalBarChart(statistic, getActivity());
        chartStat.setData(data);
        //data.setValueFormatter(new ChartValueFormatter());    //this feature will be in properties
        chartStat.setDescription("");
        Legend legend = chartStat.getLegend();
        legend.setEnabled(false);
        YAxis leftAxis = chartStat.getAxisLeft();
        YAxis rightAxis = chartStat.getAxisRight();
        rightAxis.setEnabled(false);
        leftAxis.setSpaceTop(30f);
        //rightAxis.setSpaceTop(25f);
        leftAxis.setLabelCount(3);
        chartStat.animateXY(2000, 2000);
        chartStat.setTouchEnabled(false);
        chartStat.invalidate();


        double statSum = statistic[0] + statistic[1];

        TextView tvMainStatisticSum = (TextView) view.findViewById(R.id.tvMainStatisticSum);
        tvMainStatisticSum.setText(FormatUtils.doubleFormatter(statSum, FORMAT, PRECISE) + " " + getCurrency());
    }


    private void setStatisticPieChart(int posPeriod, int posCurrency) {

        pieChart.setDescription("");
        pieChart.animateXY(2000, 2000);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(45);
        pieChart.setTransparentCircleRadius(48);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        double[] statistic = getTransStatistic(posPeriod, posCurrency);

        PieData data = ChartDataUtils.getDataSetMainStatisticPieChart(statistic, getActivity());

        pieChart.setData(data);

        pieChart.highlightValues(null);

        pieChart.invalidate();
    }


    private double[] getTransStatistic (int posPeriod, int posCurrency) {
        String[] currency = getResources().getStringArray(R.array.account_currency_array);
        return dataSource.getTransactionsStatistic(posPeriod, currency[posCurrency]);
    }

    private String getCurrency() {
        String[] currencyArray = getResources().getStringArray(R.array.account_currency_array_language);
        return currencyArray[spinBalanceCurrency.getSelectedItemPosition()];
    }


    private void setBalanceCurrencySpinner() {
        spinBalanceCurrency = (Spinner) view.findViewById(R.id.spinMainCurrency);

        ArrayAdapter<?> adapterCurrency = ArrayAdapter.createFromResource(getActivity(), R.array.account_currency_array, R.layout.spin_custom_item);
        adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinBalanceCurrency.setAdapter(adapterCurrency);

        spinBalanceCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                setCurrentBalance(spinBalanceCurrency.getSelectedItemPosition());

                setTransactionsStatistic(spinPeriod.getSelectedItemPosition() + 1,
                        spinBalanceCurrency.getSelectedItemPosition());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setStatisticSpinner() {
        spinPeriod = (Spinner) view.findViewById(R.id.spinMainPeriod);

        ArrayAdapter<?> adapterStatPeriod = ArrayAdapter.createFromResource(getActivity(), R.array.main_statistic_period_array, R.layout.spin_custom_item);
        adapterStatPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPeriod.setAdapter(adapterStatPeriod);
        spinPeriod.setSelection(1);

        spinPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setTransactionsStatistic(spinPeriod.getSelectedItemPosition() + 1,
                        spinBalanceCurrency.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void setChartTypeSpinner() {

        spinChartType = (Spinner) view.findViewById(R.id.spinMainChart);
        spinChartType.setAdapter(new SpinnerSimpleCustomAdapter(getActivity(), R.layout.spin_custom_item,
                getResources().getStringArray(R.array.charts_main_array),
                getResources().obtainTypedArray(R.array.charts_main_icons)));

        spinChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 1) {
                    chartStat.setVisibility(View.GONE);
                    pieChart.setVisibility(View.VISIBLE);
                    setStatisticPieChart(spinPeriod.getSelectedItemPosition() + 1,
                            spinBalanceCurrency.getSelectedItemPosition());
                }
                else {
                    pieChart.setVisibility(View.GONE);
                    chartStat.setVisibility(View.VISIBLE);
                    setTransactionsStatistic(spinPeriod.getSelectedItemPosition() + 1,
                            spinBalanceCurrency.getSelectedItemPosition());
                }
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

                    setCurrentBalance(spinBalanceCurrency.getSelectedItemPosition());
                    setTransactionsStatistic(spinPeriod.getSelectedItemPosition() + 1,
                            spinBalanceCurrency.getSelectedItemPosition());
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
