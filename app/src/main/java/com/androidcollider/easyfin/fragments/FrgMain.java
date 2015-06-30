package com.androidcollider.easyfin.fragments;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.utils.ChartDataUtils;
import com.androidcollider.easyfin.utils.ChartValueFormatter;
import com.androidcollider.easyfin.utils.ExchangeUtils;
import com.androidcollider.easyfin.utils.FormatUtils;
import com.androidcollider.easyfin.utils.SharedPref;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class FrgMain extends Fragment implements View.OnClickListener{

    public final static String BROADCAST_FRG_MAIN_ACTION = "com.androidcollider.easyfin.frgmain.broadcast";
    public final static String PARAM_STATUS_FRG_MAIN = "update_frg_main";
    public final static int STATUS_UPDATE_FRG_MAIN = 1;
    public final static int STATUS_UPDATE_FRG_MAIN_BALANCE = 2;

    private String[] currencyArray;
    private String[] currencyLangArray;

    private double[] statistic = new double[2];

    private final int PRECISE = 100;
    private final String FORMAT = "0.00";

    private View view;

    private DataSource dataSource;

    private BroadcastReceiver broadcastReceiver;

    private Spinner spinPeriod, spinBalanceCurrency, spinChartType;

    private TextView tvStatisticSum, tvBalanceSum;

    private HorizontalBarChart chartStatistic, chartBalance;
    private PieChart chartStatisticPie;

    private HashMap<String, double[]> balanceMap = null;

    private MaterialDialog balanceSettingsDialog;

    private CheckBox chkBoxConvert, chkBoxShowCents;

    private SharedPref sharedPref;

    private boolean convert, showCents;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frg_main, container, false);

        tvStatisticSum = (TextView) view.findViewById(R.id.tvMainStatisticSum);
        tvBalanceSum = (TextView) view.findViewById(R.id.tvMainSumValue);

        chartBalance = (HorizontalBarChart) view.findViewById(R.id.chartMainBalance);
        chartStatistic = (HorizontalBarChart) view.findViewById(R.id.chartHBarMainStatistic);
        chartStatisticPie = (PieChart) view.findViewById(R.id.chartPieMainStatistic);

        ImageView ivBalanceSettings = (ImageView) view.findViewById(R.id.ivMainBalanceSettings);
        ivBalanceSettings.setOnClickListener(this);


        buildBalanceSettingsDialog();


        View balanceSettings = balanceSettingsDialog.getCustomView();

        if (balanceSettings != null) {
            chkBoxConvert = (CheckBox) balanceSettings.findViewById(R.id.checkBoxMainBalanceSettingsConvert);
            chkBoxShowCents = (CheckBox) balanceSettings.findViewById(R.id.checkBoxMainBalanceSettingsShowCents);}


        sharedPref = new SharedPref(getActivity());

        convert = sharedPref.getMainBalanceSettingsConvertCheck();
        showCents = sharedPref.getMainBalanceSettingsShowCentsCheck();

        chkBoxConvert.setChecked(convert);
        chkBoxShowCents.setChecked(showCents);

        chkBoxConvert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPref.setMainBalanceSettingsConvertCheck(b);
                convert = b;
                setBalance(spinBalanceCurrency.getSelectedItemPosition());
            }
        });

        chkBoxShowCents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPref.setMainBalanceSettingsShowCentsCheck(b);
                showCents = b;
                setBalance(spinBalanceCurrency.getSelectedItemPosition());
                checkStatChartTypeForUpdate();
            }
        });


        currencyArray = getResources().getStringArray(R.array.account_currency_array);
        currencyLangArray = getResources().getStringArray(R.array.account_currency_array_language);

        dataSource = new DataSource(getActivity());

        balanceMap = dataSource.getAccountsSumGroupByTypeAndCurrency();

        setBalanceCurrencySpinner();

        setStatisticSpinner();

        getTransactionStatistic(spinPeriod.getSelectedItemPosition() + 1,
                spinBalanceCurrency.getSelectedItemPosition());

        setBalance(spinBalanceCurrency.getSelectedItemPosition());

        setStatisticBarChart();

        setStatisticSumTV();

        setChartTypeSpinner();

        makeBroadcastReceiver();

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ivMainBalanceSettings: {
                balanceSettingsDialog.show();
                break;}
        }
    }

    private void buildBalanceSettingsDialog() {

        balanceSettingsDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.settings)
                .customView(R.layout.item_main_balance_menu, true)
                .positiveText(R.string.done)
                .build();
    }

    private void setBalanceCurrencySpinner() {
        spinBalanceCurrency = (Spinner) view.findViewById(R.id.spinMainCurrency);

        spinBalanceCurrency.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text_main,
                R.id.tvSpinHeadIconTextMain,
                R.id.ivSpinHeadIconTextMain,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                getResources().getStringArray(R.array.account_currency_array),
                getResources().obtainTypedArray(R.array.flag_icons)));


        spinBalanceCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                setBalance(i);

                getTransactionStatistic(spinPeriod.getSelectedItemPosition() + 1, i);

                setStatisticSumTV();
                checkStatChartTypeForUpdate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setStatisticSpinner() {
        spinPeriod = (Spinner) view.findViewById(R.id.spinMainPeriod);

        ArrayAdapter<?> adapterStatPeriod = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.main_statistic_period_array,
                R.layout.spin_head_text_medium);

        adapterStatPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPeriod.setAdapter(adapterStatPeriod);
        spinPeriod.setSelection(1);

        spinPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getTransactionStatistic(i + 1,
                        spinBalanceCurrency.getSelectedItemPosition());

                setStatisticSumTV();
                checkStatChartTypeForUpdate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setChartTypeSpinner() {

        spinChartType = (Spinner) view.findViewById(R.id.spinMainChart);
        spinChartType.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text_main_chart,
                R.id.tvSpinHeadIconTextMainChart,
                R.id.ivSpinHeadIconTextMainChart,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                getResources().getStringArray(R.array.charts_main_array),
                getResources().obtainTypedArray(R.array.charts_main_icons)));

        spinChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 1) {
                    chartStatistic.setVisibility(View.GONE);
                    chartStatisticPie.setVisibility(View.VISIBLE);
                    setStatisticPieChart();
                } else {
                    chartStatisticPie.setVisibility(View.GONE);
                    chartStatistic.setVisibility(View.VISIBLE);
                    setStatisticBarChart();
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
                int status = intent.getIntExtra(PARAM_STATUS_FRG_MAIN, 0);

                balanceMap.clear();
                balanceMap.putAll(dataSource.getAccountsSumGroupByTypeAndCurrency());

                setBalance(spinBalanceCurrency.getSelectedItemPosition());


                switch (status) {
                    case STATUS_UPDATE_FRG_MAIN: {

                        getTransactionStatistic(spinPeriod.getSelectedItemPosition() + 1,
                                spinBalanceCurrency.getSelectedItemPosition());

                        setStatisticSumTV();
                        checkStatChartTypeForUpdate();

                        break;
                    }
                    /*case STATUS_UPDATE_FRG_MAIN_BALANCE: {
                        setBalance(spinBalanceCurrency.getSelectedItemPosition());
                    }*/
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_FRG_MAIN_ACTION);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setBalanceBarChart(double[] balance) {

        BarData data = ChartDataUtils.getDataSetMainBalanceHorizontalBarChart(balance, getActivity());
        chartBalance.setData(data);

        if (showCents) {
            data.setValueFormatter(new ChartValueFormatter());
        }
        chartBalance.setDescription("");
        Legend legend = chartBalance.getLegend();
        legend.setEnabled(false);
        YAxis leftAxis = chartBalance.getAxisLeft();
        YAxis rightAxis = chartBalance.getAxisRight();
        rightAxis.setEnabled(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setLabelCount(3);
        chartBalance.animateXY(2000, 2000);
        chartBalance.setTouchEnabled(false);
        chartBalance.invalidate();
    }

    private void setStatisticBarChart() {

        BarData data = ChartDataUtils.getDataSetMainStatisticHorizontalBarChart(statistic, getActivity());
        chartStatistic.setData(data);

        if (showCents) {
            data.setValueFormatter(new ChartValueFormatter());
        }
        chartStatistic.setDescription("");
        Legend legend = chartStatistic.getLegend();
        legend.setEnabled(false);
        YAxis leftAxis = chartStatistic.getAxisLeft();
        YAxis rightAxis = chartStatistic.getAxisRight();
        rightAxis.setEnabled(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setLabelCount(3);
        chartStatistic.animateXY(2000, 2000);
        chartStatistic.setTouchEnabled(false);
        chartStatistic.invalidate();
    }

    private void setStatisticPieChart() {

        chartStatisticPie.setDescription("");
        chartStatisticPie.animateXY(2000, 2000);

        chartStatisticPie.setDrawHoleEnabled(true);
        chartStatisticPie.setHoleColorTransparent(true);
        chartStatisticPie.setHoleRadius(45);
        chartStatisticPie.setTransparentCircleRadius(48);

        chartStatisticPie.setRotationAngle(0);
        chartStatisticPie.setRotationEnabled(true);

        Legend legend = chartStatisticPie.getLegend();
        legend.setEnabled(false);

        PieData data = ChartDataUtils.getDataSetMainStatisticPieChart(statistic, getActivity());

        chartStatisticPie.setData(data);

        chartStatisticPie.highlightValues(null);

        chartStatisticPie.invalidate();
    }

    private void setStatisticSumTV() {
        double statSum = statistic[0] + statistic[1];
        tvStatisticSum.setText(FormatUtils.doubleFormatter(statSum, FORMAT, PRECISE) + " " + getCurrencyLang());
    }

    private void setBalanceTV (double[] balance) {

        double sum = 0;
        for (double i: balance) {
            sum += i;
        }

        tvBalanceSum.setText(FormatUtils.doubleFormatter(sum, FORMAT, PRECISE) + " " + getCurrencyLang());
    }

    private void setBalance(int posCurrency) {
        double[] balance = getCurrentBalance(posCurrency);
        setBalanceTV(balance);
        setBalanceBarChart(balance);
    }

    private double[] getCurrentBalance(int posCurrency) {

        if (convert) {
            return convertBalanceToOneCurrency(posCurrency);
        }

        else {

            Iterator it = balanceMap.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                if (currencyArray[posCurrency].equals(pair.getKey())) {
                    return (double[]) pair.getValue();
                }
            }
        }

        return new double[]{0, 0, 0, 0};
    }


    private double[] convertBalanceToOneCurrency(int posCurrency) {

        double[] uahArr = new double[4];
        double[] usdArr = new double[4];
        double[] eurArr = new double[4];
        double[] rubArr = new double[4];

        final String uahCurName = currencyArray[0];
        final String usdCurName = currencyArray[1];
        final String eurCurName = currencyArray[2];
        final String rubCurName = currencyArray[3];

        Iterator it = balanceMap.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();
            String key = (String) pair.getKey();
            double[] value = (double[]) pair.getValue();

            if (uahCurName.equals(key)) {
                uahArr = value.clone();
            }
            else if (usdCurName.equals(key)) {
                usdArr = value.clone();
            }
            else if (eurCurName.equals(key)) {
                eurArr = value.clone();
            }
            else if (rubCurName.equals(key)) {
                rubArr = value.clone();
            }
        }


        String convertTo = currencyArray[posCurrency];

        double uahExchange = ExchangeUtils.getExchangeRate(uahCurName, convertTo);
        double usdExchange = ExchangeUtils.getExchangeRate(usdCurName, convertTo);
        double eurExchange = ExchangeUtils.getExchangeRate(eurCurName, convertTo);
        double rubExchange = ExchangeUtils.getExchangeRate(rubCurName, convertTo);


        uahArr = convertArray(uahArr, uahExchange);
        usdArr = convertArray(usdArr, usdExchange);
        eurArr = convertArray(eurArr, eurExchange);
        rubArr = convertArray(rubArr, rubExchange);


        double[] result = new double[4];

        for (int i = 0; i < result.length; i++) {
            result[i] = uahArr[i] + usdArr[i] + eurArr[i] + rubArr[i];
        }

        return result;
    }

    private double[] convertArray(double[] arr, double exc) {

        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i] / exc;
        }

        return arr;
    }

    private void getTransactionStatistic(int posPeriod, int posCurrency) {
        double[] st = dataSource.getTransactionsStatistic(posPeriod, currencyArray[posCurrency]);
        System.arraycopy(st, 0, statistic, 0, statistic.length);
    }

    private String getCurrencyLang() {
        return currencyLangArray[spinBalanceCurrency.getSelectedItemPosition()];
    }

    private void checkStatChartTypeForUpdate() {

        switch (spinChartType.getSelectedItemPosition()) {

            case 0: {
                setStatisticBarChart(); break;}
            case 1: {setStatisticPieChart(); break;}
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
