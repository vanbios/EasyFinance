package com.androidcollider.easyfin.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.events.UpdateFrgHome;
import com.androidcollider.easyfin.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.events.UpdateFrgHomeNewRates;
import com.androidcollider.easyfin.managers.rates.exchange.ExchangeManager;
import com.androidcollider.easyfin.managers.rates.rates_info.RatesInfoManager;
import com.androidcollider.easyfin.repository.Repository;
import com.androidcollider.easyfin.utils.ChartDataUtils;
import com.androidcollider.easyfin.utils.ChartLargeValueFormatter;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.SharedPref;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;


public class FrgHome extends CommonFragmentWithEvents {

    private String[] currencyArray, currencyLangArray;
    private double[] statistic = new double[2];
    private HashMap<String, double[]> balanceMap, statisticMap = null;

    private final int PRECISE = 100;
    private final String FORMAT = "###,##0.00";

    private View view;
    private Spinner spinPeriod, spinBalanceCurrency, spinChartType;
    private TextView tvStatisticSum, tvBalanceSum, tvNoData;
    private HorizontalBarChart chartStatistic, chartBalance;
    private PieChart chartStatisticPie;
    private MaterialDialog balanceSettingsDialog;
    private CheckBox chkBoxConvert, chkBoxShowOnlyIntegers;

    private SharedPref sharedPref;
    private boolean convert, showOnlyIntegers,
            spinPeriodNotInitSelectedItemCall,
            spinBalanceCurrencyNotInitSelectedItemCall,
            spinChartTypeNotInitSelectedItemCall;

    @Inject
    ExchangeManager exchangeManager;

    @Inject
    RatesInfoManager ratesInfoManager;

    @Inject
    Repository repository;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_home, container, false);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        initializeViewsAndRes();

        setBalanceCurrencySpinner();
        setStatisticPeriodSpinner();

        balanceMap = new HashMap<>();
        statisticMap = new HashMap<>();

        Observable.combineLatest(
                repository.getAccountsAmountSumGroupByTypeAndCurrency(),
                repository.getTransactionsStatistic(spinPeriod.getSelectedItemPosition() + 1),
                Pair::new)
                .subscribe(new Subscriber<Pair<Map<String, double[]>, Map<String, double[]>>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Pair<Map<String, double[]>, Map<String, double[]>> pair) {
                        balanceMap.clear();
                        balanceMap.putAll(pair.first);
                        statisticMap.clear();
                        statisticMap.putAll(pair.second);

                        setTransactionStatisticArray(spinBalanceCurrency.getSelectedItemPosition());
                        setBalance(spinBalanceCurrency.getSelectedItemPosition());
                        setStatisticBarChart();
                        setStatisticSumTV();
                        setChartTypeSpinner();
                    }
                });

        //balanceMap = InMemoryRepository.getInstance().getDataSource().getAccountsSumGroupByTypeAndCurrency();
        //statisticMap = InMemoryRepository.getInstance().getDataSource().getTransactionsStatistic(spinPeriod.getSelectedItemPosition() + 1);

        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    private void initializeViewsAndRes() {
        tvStatisticSum = (TextView) view.findViewById(R.id.tvMainStatisticSum);
        tvBalanceSum = (TextView) view.findViewById(R.id.tvMainSumValue);

        chartBalance = (HorizontalBarChart) view.findViewById(R.id.chartMainBalance);
        chartStatistic = (HorizontalBarChart) view.findViewById(R.id.chartHBarMainStatistic);
        chartStatisticPie = (PieChart) view.findViewById(R.id.chartPieMainStatistic);

        ImageView ivBalanceSettings = (ImageView) view.findViewById(R.id.ivMainBalanceSettings);
        ivBalanceSettings.setOnClickListener(view1 -> balanceSettingsDialog.show());

        buildBalanceSettingsDialog();

        View balanceSettings = balanceSettingsDialog.getCustomView();

        if (balanceSettings != null) {
            chkBoxConvert = (CheckBox) balanceSettings.findViewById(R.id.checkBoxMainBalanceSettingsConvert);
            chkBoxShowOnlyIntegers = (CheckBox) balanceSettings.findViewById(R.id.checkBoxMainBalanceSettingsShowCents);
        }

        sharedPref = new SharedPref(getActivity());

        convert = sharedPref.getMainBalanceSettingsConvertCheck();
        showOnlyIntegers = sharedPref.getMainBalanceSettingsShowOnlyIntegersCheck();

        chkBoxConvert.setChecked(convert);
        chkBoxShowOnlyIntegers.setChecked(showOnlyIntegers);

        chkBoxConvert.setOnCheckedChangeListener((compoundButton, b) -> {
            sharedPref.setMainBalanceSettingsConvertCheck(b);
            convert = b;
            setBalance(spinBalanceCurrency.getSelectedItemPosition());

            setTransactionStatisticArray(spinBalanceCurrency.getSelectedItemPosition());
            setStatisticSumTV();
            checkStatChartTypeForUpdate();
        });

        chkBoxShowOnlyIntegers.setOnCheckedChangeListener((compoundButton, b) -> {
            sharedPref.setMainBalanceSettingsShowOnlyIntegersCheck(b);
            showOnlyIntegers = b;
            setBalance(spinBalanceCurrency.getSelectedItemPosition());
            checkStatChartTypeForUpdate();
        });


        currencyArray = getResources().getStringArray(R.array.account_currency_array);
        currencyLangArray = getResources().getStringArray(R.array.account_currency_array_language);

        TextView tvBalance = (TextView) view.findViewById(R.id.tvMainCurrentBalance);
        tvNoData = (TextView) view.findViewById(R.id.tvMainNoData);

        ratesInfoManager.setupMultiTapListener(tvBalance, getActivity());
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
                if (spinBalanceCurrencyNotInitSelectedItemCall) {
                    setBalance(i);
                    setTransactionStatisticArray(i);
                    setStatisticSumTV();
                    checkStatChartTypeForUpdate();
                    sharedPref.setHomeBalanceCurrencyPos(i);
                } else {
                    spinBalanceCurrencyNotInitSelectedItemCall = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinBalanceCurrency.setSelection(sharedPref.getHomeBalanceCurrencyPos());
    }

    private void setStatisticPeriodSpinner() {
        spinPeriod = (Spinner) view.findViewById(R.id.spinMainPeriod);

        ArrayAdapter<?> adapterStatPeriod = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.main_statistic_period_array,
                R.layout.spin_head_text_medium);

        adapterStatPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPeriod.setAdapter(adapterStatPeriod);

        spinPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinPeriodNotInitSelectedItemCall) {
                    repository.getTransactionsStatistic(i + 1)
                            .subscribe(new Subscriber<Map<String, double[]>>() {

                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Map<String, double[]> map) {
                                    statisticMap.clear();
                                    statisticMap.putAll(map);
                                    //statisticMap = InMemoryRepository.getInstance().getDataSource().getTransactionsStatistic(i + 1);
                                    setTransactionStatisticArray(spinBalanceCurrency.getSelectedItemPosition());

                                    setStatisticSumTV();
                                    checkStatChartTypeForUpdate();
                                    sharedPref.setHomePeriodPos(i);
                                }
                            });
                } else {
                    spinPeriodNotInitSelectedItemCall = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinPeriod.setSelection(sharedPref.getHomePeriodPos());
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
                getResources().getStringArray(R.array.chart_type_array),
                getResources().obtainTypedArray(R.array.charts_main_icons)));

        spinChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinChartTypeNotInitSelectedItemCall) {
                    if (i == 1) {
                        chartStatistic.setVisibility(View.GONE);
                        if (statistic[0] == 0 && statistic[1] == 0)
                            tvNoData.setVisibility(View.VISIBLE);
                        else {
                            chartStatisticPie.setVisibility(View.VISIBLE);
                            setStatisticPieChart();
                        }
                    } else {
                        tvNoData.setVisibility(View.GONE);
                        chartStatisticPie.setVisibility(View.GONE);
                        chartStatistic.setVisibility(View.VISIBLE);
                        setStatisticBarChart();
                    }
                    sharedPref.setHomeChartTypePos(i);
                } else {
                    spinChartTypeNotInitSelectedItemCall = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinChartType.setSelection(sharedPref.getHomeChartTypePos());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgHome event) {
        repository.getAccountsAmountSumGroupByTypeAndCurrency()
                .subscribe(new Subscriber<Map<String, double[]>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, double[]> map) {
                        balanceMap.clear();
                        balanceMap.putAll(map);
                        //balanceMap.putAll(InMemoryRepository.getInstance().getDataSource().getAccountsSumGroupByTypeAndCurrency());
                        setBalance(spinBalanceCurrency.getSelectedItemPosition());
                    }
                });

        repository.getTransactionsStatistic(spinPeriod.getSelectedItemPosition() + 1)
                .subscribe(new Subscriber<Map<String, double[]>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, double[]> map) {
                        statisticMap.clear();
                        statisticMap.putAll(map);
                        //statisticMap.putAll(InMemoryRepository.getInstance().getDataSource().getTransactionsStatistic(spinPeriod.getSelectedItemPosition() + 1));
                        setTransactionStatisticArray(spinBalanceCurrency.getSelectedItemPosition());
                        setStatisticSumTV();
                        checkStatChartTypeForUpdate();
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgHomeBalance event) {
        repository.getAccountsAmountSumGroupByTypeAndCurrency()
                .subscribe(new Subscriber<Map<String, double[]>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, double[]> map) {
                        balanceMap.clear();
                        balanceMap.putAll(map);
                        //balanceMap.putAll(InMemoryRepository.getInstance().getDataSource().getAccountsSumGroupByTypeAndCurrency());
                        setBalance(spinBalanceCurrency.getSelectedItemPosition());
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgHomeNewRates event) {
        if (convert) {
            setBalance(spinBalanceCurrency.getSelectedItemPosition());
            setTransactionStatisticArray(spinBalanceCurrency.getSelectedItemPosition());
            setStatisticSumTV();
            checkStatChartTypeForUpdate();
        }
    }

    private void setBalanceBarChart(double[] balance) {
        BarData data = ChartDataUtils.getDataSetMainBalanceHorizontalBarChart(balance, getActivity());
        chartBalance.setData(data);

        data.setValueFormatter(new ChartLargeValueFormatter(!showOnlyIntegers));

        chartBalance.setDescription("");
        chartBalance.getLegend().setEnabled(false);
        YAxis leftAxis = chartBalance.getAxisLeft();
        YAxis rightAxis = chartBalance.getAxisRight();
        rightAxis.setEnabled(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setLabelCount(3, false);
        leftAxis.setValueFormatter(new ChartLargeValueFormatter(false));

        XAxis xAxis = chartBalance.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);

        leftAxis.setAxisLineColor(ContextCompat.getColor(getActivity(), R.color.custom_light_gray));
        leftAxis.setGridColor(ContextCompat.getColor(getActivity(), R.color.custom_light_gray));
        leftAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.custom_text_gray_dark));

        chartBalance.getXAxis().setTextColor(ContextCompat.getColor(getActivity(), R.color.custom_text_gray_dark));

        chartBalance.setDrawGridBackground(false);
        chartBalance.setBackgroundColor(Color.TRANSPARENT);
        chartBalance.setDrawBorders(true);
        chartBalance.setBorderColor(Color.TRANSPARENT);

        chartBalance.animateXY(2000, 2000);
        chartBalance.setTouchEnabled(false);
        chartBalance.invalidate();
    }

    private void setStatisticBarChart() {
        BarData data = ChartDataUtils.getDataSetMainStatisticHorizontalBarChart(statistic, getActivity());
        chartStatistic.setData(data);

        data.setValueFormatter(new ChartLargeValueFormatter(!showOnlyIntegers));

        chartStatistic.setDescription("");
        chartStatistic.getLegend().setEnabled(false);
        YAxis leftAxis = chartStatistic.getAxisLeft();
        YAxis rightAxis = chartStatistic.getAxisRight();
        rightAxis.setEnabled(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setLabelCount(3, false);
        leftAxis.setValueFormatter(new ChartLargeValueFormatter(false));

        XAxis xAxis = chartStatistic.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);

        leftAxis.setAxisLineColor(ContextCompat.getColor(getActivity(), R.color.custom_light_gray));
        leftAxis.setGridColor(ContextCompat.getColor(getActivity(), R.color.custom_light_gray));
        leftAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.custom_text_gray_dark));

        chartStatistic.getXAxis().setTextColor(ContextCompat.getColor(getActivity(), R.color.custom_text_gray_dark));

        chartStatistic.setDrawGridBackground(false);
        chartStatistic.setBackgroundColor(Color.TRANSPARENT);
        chartStatistic.setDrawBorders(false);

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

        chartStatisticPie.getLegend().setEnabled(false);

        PieData data = ChartDataUtils.getDataSetMainStatisticPieChart(statistic, getActivity());

        data.setValueFormatter(new ChartLargeValueFormatter(!showOnlyIntegers));

        chartStatisticPie.setData(data);
        chartStatisticPie.highlightValues(null);
        chartStatisticPie.invalidate();
    }

    private void setStatisticSumTV() {
        double statSum = statistic[0] + statistic[1];
        tvStatisticSum.setText(String.format("%1$s %2$s",
                DoubleFormatUtils.doubleToStringFormatter(statSum, FORMAT, PRECISE), getCurrencyLang()));
    }

    private void setBalanceTV(double[] balance) {
        double sum = 0;
        for (double d : balance) {
            sum += d;
        }
        tvBalanceSum.setText(String.format("%1$s %2$s",
                DoubleFormatUtils.doubleToStringFormatter(sum, FORMAT, PRECISE), getCurrencyLang()));
    }

    private void setBalance(int posCurrency) {
        double[] balance = getCurrentBalance(posCurrency);
        setBalanceTV(balance);
        setBalanceBarChart(balance);
    }

    private double[] getCurrentBalance(int posCurrency) {
        if (convert) return convertAllCurrencyToOne(posCurrency, balanceMap, 4);
        for (Object o : balanceMap.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (currencyArray[posCurrency].equals(pair.getKey())) return (double[]) pair.getValue();
        }
        return new double[]{0, 0, 0, 0};
    }

    private double[] convertAllCurrencyToOne(int posCurrency, HashMap<String, double[]> map, int arrSize) {
        double[] uahArr = new double[arrSize];
        double[] usdArr = new double[arrSize];
        double[] eurArr = new double[arrSize];
        double[] rubArr = new double[arrSize];
        double[] gbpArr = new double[arrSize];

        final String uahCurName = currencyArray[0];
        final String usdCurName = currencyArray[1];
        final String eurCurName = currencyArray[2];
        final String rubCurName = currencyArray[3];
        final String gbpCurName = currencyArray[4];

        for (Object o : map.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            String key = (String) pair.getKey();
            double[] value = (double[]) pair.getValue();

            if (uahCurName.equals(key))
                System.arraycopy(value, 0, uahArr, 0, uahArr.length);
            else if (usdCurName.equals(key))
                System.arraycopy(value, 0, usdArr, 0, usdArr.length);
            else if (eurCurName.equals(key))
                System.arraycopy(value, 0, eurArr, 0, eurArr.length);
            else if (rubCurName.equals(key))
                System.arraycopy(value, 0, rubArr, 0, rubArr.length);
            else if (gbpCurName.equals(key))
                System.arraycopy(value, 0, gbpArr, 0, gbpArr.length);
        }

        String convertTo = currencyArray[posCurrency];

        double uahExchange = exchangeManager.getExchangeRate(uahCurName, convertTo);
        double usdExchange = exchangeManager.getExchangeRate(usdCurName, convertTo);
        double eurExchange = exchangeManager.getExchangeRate(eurCurName, convertTo);
        double rubExchange = exchangeManager.getExchangeRate(rubCurName, convertTo);
        double gbpExchange = exchangeManager.getExchangeRate(gbpCurName, convertTo);

        uahArr = convertArray(uahArr, uahExchange);
        usdArr = convertArray(usdArr, usdExchange);
        eurArr = convertArray(eurArr, eurExchange);
        rubArr = convertArray(rubArr, rubExchange);
        gbpArr = convertArray(gbpArr, gbpExchange);

        double[] result = new double[arrSize];

        for (int i = 0; i < result.length; i++) {
            result[i] = uahArr[i] + usdArr[i] + eurArr[i] + rubArr[i] + gbpArr[i];
        }

        return result;
    }

    private double[] convertArray(double[] arr, double exc) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] /= exc;
        }
        return arr;
    }

    private void setTransactionStatisticArray(int posCurrency) {
        if (convert) {
            System.arraycopy(convertAllCurrencyToOne(posCurrency, statisticMap, 2), 0, statistic, 0, statistic.length);
        } else {
            for (Object o : statisticMap.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                if (currencyArray[posCurrency].equals(pair.getKey())) {
                    double[] st = (double[]) pair.getValue();
                    System.arraycopy(st, 0, statistic, 0, statistic.length);
                }
            }
        }
    }

    private String getCurrencyLang() {
        return currencyLangArray[spinBalanceCurrency.getSelectedItemPosition()];
    }

    private void checkStatChartTypeForUpdate() {
        switch (spinChartType.getSelectedItemPosition()) {
            case 0:
                setStatisticBarChart();
                break;
            case 1:
                if (statistic[0] == 0 && statistic[1] == 0) {
                    tvNoData.setVisibility(View.VISIBLE);
                    chartStatisticPie.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                    chartStatisticPie.setVisibility(View.VISIBLE);
                    setStatisticPieChart();
                }
                break;
        }
    }
}