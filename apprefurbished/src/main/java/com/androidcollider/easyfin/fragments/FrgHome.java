package com.androidcollider.easyfin.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.View;
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
import com.androidcollider.easyfin.common.events.UpdateFrgHome;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeNewRates;
import com.androidcollider.easyfin.fragments.common.CommonFragmentWithEvents;
import com.androidcollider.easyfin.managers.chart.data.ChartDataManager;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.managers.rates.exchange.ExchangeManager;
import com.androidcollider.easyfin.managers.rates.rates_info.RatesInfoManager;
import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.managers.shared_pref.SharedPrefManager;
import com.androidcollider.easyfin.repository.Repository;
import com.androidcollider.easyfin.utils.ChartLargeValueFormatter;
import com.annimon.stream.Stream;
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

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

public class FrgHome extends CommonFragmentWithEvents {

    private String[] currencyArray, currencyLangArray;
    private double[] statistic = new double[2];
    private HashMap<String, double[]> balanceMap, statisticMap;

    @BindView(R.id.spinMainPeriod)
    Spinner spinPeriod;
    @BindView(R.id.spinMainCurrency)
    Spinner spinBalanceCurrency;
    @BindView(R.id.spinMainChart)
    Spinner spinChartType;
    @BindView(R.id.tvMainStatisticSum)
    TextView tvStatisticSum;
    @BindView(R.id.tvMainSumValue)
    TextView tvBalanceSum;
    @BindView(R.id.tvMainNoData)
    TextView tvNoData;
    @BindView(R.id.tvMainCurrentBalance)
    TextView tvBalance;
    @BindView(R.id.ivMainBalanceSettings)
    ImageView ivBalanceSettings;
    @BindView(R.id.chartHBarMainStatistic)
    HorizontalBarChart chartStatistic;
    @BindView(R.id.chartMainBalance)
    HorizontalBarChart chartBalance;
    @BindView(R.id.chartPieMainStatistic)
    PieChart chartStatisticPie;
    CheckBox chkBoxConvert;
    CheckBox chkBoxShowOnlyIntegers;

    private MaterialDialog balanceSettingsDialog;

    private boolean convert, showOnlyIntegers,

    // we can prevent init spinner issue with subscribing
    // to listeners in onRestoreInstanceState
    spinPeriodNotInitSelectedItemCall,
            spinBalanceCurrencyNotInitSelectedItemCall,
            spinChartTypeNotInitSelectedItemCall;

    @Inject
    ExchangeManager exchangeManager;

    @Inject
    RatesInfoManager ratesInfoManager;

    @Inject
    Repository repository;

    @Inject
    SharedPrefManager sharedPrefManager;

    @Inject
    ChartDataManager chartDataManager;

    @Inject
    NumberFormatManager numberFormatManager;

    @Inject
    ResourcesManager resourcesManager;


    @Override
    public int getContentView() {
        return R.layout.frg_home;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    }

    private void initializeViewsAndRes() {
        buildBalanceSettingsDialog();

        View balanceSettings = balanceSettingsDialog.getCustomView();

        if (balanceSettings != null) {
            chkBoxConvert = findById(balanceSettings, R.id.checkBoxMainBalanceSettingsConvert);
            chkBoxShowOnlyIntegers = findById(balanceSettings, R.id.checkBoxMainBalanceSettingsShowCents);
        }

        convert = sharedPrefManager.getMainBalanceSettingsConvertCheck();
        showOnlyIntegers = sharedPrefManager.getMainBalanceSettingsShowOnlyIntegersCheck();

        chkBoxConvert.setChecked(convert);
        chkBoxShowOnlyIntegers.setChecked(showOnlyIntegers);

        chkBoxConvert.setOnCheckedChangeListener((compoundButton, b) -> {
            sharedPrefManager.setMainBalanceSettingsConvertCheck(b);
            convert = b;
            setBalance(spinBalanceCurrency.getSelectedItemPosition());

            setTransactionStatisticArray(spinBalanceCurrency.getSelectedItemPosition());
            setStatisticSumTV();
            checkStatChartTypeForUpdate();
        });

        chkBoxShowOnlyIntegers.setOnCheckedChangeListener((compoundButton, b) -> {
            sharedPrefManager.setMainBalanceSettingsShowOnlyIntegersCheck(b);
            showOnlyIntegers = b;
            setBalance(spinBalanceCurrency.getSelectedItemPosition());
            checkStatChartTypeForUpdate();
        });


        currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        currencyLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);

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
        spinBalanceCurrency.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text_main,
                R.id.tvSpinHeadIconTextMain,
                R.id.ivSpinHeadIconTextMain,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY),
                resourcesManager.getIconArray(ResourcesManager.ICON_FLAGS)));

        spinBalanceCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinBalanceCurrencyNotInitSelectedItemCall) {
                    setBalance(i);
                    setTransactionStatisticArray(i);
                    setStatisticSumTV();
                    checkStatChartTypeForUpdate();
                    sharedPrefManager.setHomeBalanceCurrencyPos(i);
                } else {
                    spinBalanceCurrencyNotInitSelectedItemCall = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinBalanceCurrency.setSelection(sharedPrefManager.getHomeBalanceCurrencyPos());
    }

    private void setStatisticPeriodSpinner() {
        ArrayAdapter<?> adapterStatPeriod = ArrayAdapter.createFromResource(
                getActivity(),
                ResourcesManager.STRING_MAIN_STATISTIC_PERIOD,
                R.layout.spin_head_text_medium
        );

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
                                    setTransactionStatisticArray(spinBalanceCurrency.getSelectedItemPosition());

                                    setStatisticSumTV();
                                    checkStatChartTypeForUpdate();
                                    sharedPrefManager.setHomePeriodPos(i);
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

        spinPeriod.setSelection(sharedPrefManager.getHomePeriodPos());
    }

    private void setChartTypeSpinner() {
        spinChartType.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text_main_chart,
                R.id.tvSpinHeadIconTextMainChart,
                R.id.ivSpinHeadIconTextMainChart,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                resourcesManager.getStringArray(ResourcesManager.STRING_CHART_TYPE),
                resourcesManager.getIconArray(ResourcesManager.ICON_CHART_TYPE)));

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
                    sharedPrefManager.setHomeChartTypePos(i);
                } else {
                    spinChartTypeNotInitSelectedItemCall = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinChartType.setSelection(sharedPrefManager.getHomeChartTypePos());
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
        BarData data = chartDataManager.getDataSetMainBalanceHorizontalBarChart(balance, getActivity());
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
        BarData data = chartDataManager.getDataSetMainStatisticHorizontalBarChart(statistic, getActivity());
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

        PieData data = chartDataManager.getDataSetMainStatisticPieChart(statistic, getActivity());

        data.setValueFormatter(new ChartLargeValueFormatter(!showOnlyIntegers));

        chartStatisticPie.setData(data);
        chartStatisticPie.highlightValues(null);
        chartStatisticPie.invalidate();
    }

    private void setStatisticSumTV() {
        tvStatisticSum.setText(String.format("%1$s %2$s",
                numberFormatManager.doubleToStringFormatter(
                        statistic[0] + statistic[1],
                        NumberFormatManager.FORMAT_1,
                        NumberFormatManager.PRECISE_1),
                getCurrencyLang()));
    }

    private void setBalanceTV(double[] balance) {
        double sum = 0;
        for (double d : balance) sum += d;
        tvBalanceSum.setText(String.format("%1$s %2$s",
                numberFormatManager.doubleToStringFormatter(
                        sum,
                        NumberFormatManager.FORMAT_1,
                        NumberFormatManager.PRECISE_1),
                getCurrencyLang()));
    }

    private void setBalance(int posCurrency) {
        double[] balance = getCurrentBalance(posCurrency);
        setBalanceTV(balance);
        setBalanceBarChart(balance);
    }

    private double[] getCurrentBalance(int posCurrency) {
        if (convert) return convertAllCurrencyToOne(posCurrency, balanceMap, 4);
        for (Map.Entry<String, double[]> pair : balanceMap.entrySet()) {
            if (currencyArray[posCurrency].equals(pair.getKey())) return pair.getValue();
        }
        return new double[]{0, 0, 0, 0};
    }

    private double[] convertAllCurrencyToOne(int posCurrency, HashMap<String, double[]> map, int arrSize) {
        double[][] arr = new double[currencyArray.length][arrSize];

        for (int i = 0; i < arr.length; i++) {
            double[] value = map.get(currencyArray[i]);
            if (value != null) {
                System.arraycopy(value, 0, arr[i], 0, arr[i].length);
                arr[i] = convertArray(arr[i], exchangeManager.getExchangeRate(currencyArray[i], currencyArray[posCurrency]));
            }
        }

        double[] result = new double[arrSize];

        for (int i = 0; i < result.length; i++) {
            for (double[] a : arr) {
                result[i] += a[i];
            }
        }

        return result;
    }

    private double[] convertArray(double[] arr, double exc) {
        for (int i = 0; i < arr.length; i++) arr[i] /= exc;
        return arr;
    }

    private void setTransactionStatisticArray(int posCurrency) {
        if (convert) {
            System.arraycopy(convertAllCurrencyToOne(posCurrency, statisticMap, 2), 0, statistic, 0, statistic.length);
        } else {
            Stream.of(statisticMap.entrySet())
                    .filter(p -> currencyArray[posCurrency].equals(p.getKey()))
                    .forEach(p -> System.arraycopy(p.getValue(), 0, statistic, 0, statistic.length));
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

    @OnClick({R.id.ivMainBalanceSettings})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMainBalanceSettings:
                balanceSettingsDialog.show();
                break;
        }
    }
}