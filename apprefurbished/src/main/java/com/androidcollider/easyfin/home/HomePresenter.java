package com.androidcollider.easyfin.home;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.androidcollider.easyfin.common.managers.chart.data.ChartDataManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.annimon.stream.Stream;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

class HomePresenter implements HomeMVP.Presenter {

    private HomeMVP.View view;
    private HomeMVP.Model model;
    private NumberFormatManager numberFormatManager;
    private ExchangeManager exchangeManager;
    private ChartDataManager chartDataManager;

    private String[] currencyArray, currencyLangArray;
    private double[] statistic;
    private Map<String, double[]> balanceMap, statisticMap;


    HomePresenter(HomeMVP.Model model,
                  ResourcesManager resourcesManager,
                  NumberFormatManager numberFormatManager,
                  ExchangeManager exchangeManager,
                  ChartDataManager chartDataManager) {
        this.model = model;
        this.numberFormatManager = numberFormatManager;
        this.exchangeManager = exchangeManager;
        this.chartDataManager = chartDataManager;

        statistic = new double[2];
        balanceMap = new HashMap<>();
        statisticMap = new HashMap<>();

        currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        currencyLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
    }

    @Override
    public void setView(@Nullable HomeMVP.View view) {
        this.view = view;
    }

    @Override
    public void loadBalanceAndStatistic(int statisticPosition) {
        model.getBalanceAndStatistic(statisticPosition)
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

                        if (view != null) {
                            updateTransactionStatisticArray(view.getBalanceCurrencyPosition());
                            view.setBalanceAndStatistic(pair);
                        }
                    }
                });
    }

    @Override
    public void updateBalanceAndStatistic(int statisticPosition) {
        model.getBalanceAndStatistic(statisticPosition)
                .subscribe(new Subscriber<Pair<Map<String, double[]>, Map<String, double[]>>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Pair<Map<String, double[]>, Map<String, double[]>> pair) {
                        updateRates();

                        balanceMap.clear();
                        balanceMap.putAll(pair.first);
                        statisticMap.clear();
                        statisticMap.putAll(pair.second);

                        if (view != null) {
                            updateTransactionStatisticArray(view.getBalanceCurrencyPosition());
                            view.updateBalanceAndStatistic(pair);
                        }
                    }
                });
    }

    @Override
    public void updateBalanceAndStatisticAfterDBImport(int statisticPosition) {
        model.getBalanceAndStatistic(statisticPosition)
                .subscribe(new Subscriber<Pair<Map<String, double[]>, Map<String, double[]>>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Pair<Map<String, double[]>, Map<String, double[]>> pair) {
                        updateRates();

                        balanceMap.clear();
                        balanceMap.putAll(pair.first);
                        statisticMap.clear();
                        statisticMap.putAll(pair.second);

                        if (view != null) {
                            updateTransactionStatisticArray(view.getBalanceCurrencyPosition());
                            view.updateBalanceAndStatisticAfterDBImport(pair);
                        }
                    }
                });
    }

    @Override
    public void updateBalance() {
        model.getBalance()
                .subscribe(new Subscriber<Map<String, double[]>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, double[]> map) {
                        updateRates();

                        balanceMap.clear();
                        balanceMap.putAll(map);

                        if (view != null) {
                            updateTransactionStatisticArray(view.getBalanceCurrencyPosition());
                            view.updateBalance(map);
                        }
                    }
                });
    }

    @Override
    public void updateStatistic(int statisticPosition) {
        model.getStatistic(statisticPosition)
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

                        if (view != null) {
                            updateTransactionStatisticArray(view.getBalanceCurrencyPosition());
                            view.updateStatistic(map);
                        }
                    }
                });
    }

    @Override
    public boolean isStatisticEmpty() {
        return statistic[0] == 0 && statistic[1] == 0;
    }

    @Override
    public String getFormattedBalance(double[] balance) {
        double sum = 0;
        for (double d : balance) sum += d;
        return String.format("%1$s %2$s",
                numberFormatManager.doubleToStringFormatter(
                        sum,
                        NumberFormatManager.FORMAT_1,
                        NumberFormatManager.PRECISE_1),
                getCurrencyLang());
    }

    @Override
    public String getFormattedStatistic() {
        return String.format("%1$s %2$s",
                numberFormatManager.doubleToStringFormatter(
                        statistic[0] + statistic[1],
                        NumberFormatManager.FORMAT_1,
                        NumberFormatManager.PRECISE_1),
                getCurrencyLang());
    }

    @Override
    public BarData getDataSetMainBalanceHorizontalBarChart(double[] balance) {
        return chartDataManager.getDataSetMainBalanceHorizontalBarChart(balance);
    }

    @Override
    public BarData getDataSetMainStatisticHorizontalBarChart() {
        return chartDataManager.getDataSetMainStatisticHorizontalBarChart(statistic);
    }

    @Override
    public PieData getDataSetMainStatisticPieChart() {
        return chartDataManager.getDataSetMainStatisticPieChart(statistic);
    }

    @Override
    public void updateRates() {
        exchangeManager.updateRates();
    }

    @Override
    public double[] getCurrentBalance(int posCurrency) {
        if (view.isNeedToConvert()) return convertAllCurrencyToOne(posCurrency, balanceMap, 4);
        for (Map.Entry<String, double[]> pair : balanceMap.entrySet()) {
            if (currencyArray[posCurrency].equals(pair.getKey())) return pair.getValue();
        }
        return new double[]{0, 0, 0, 0};
    }

    private double[] convertAllCurrencyToOne(int posCurrency, Map<String, double[]> map, int arrSize) {
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

    @Override
    public void updateTransactionStatisticArray(int posCurrency) {
        if (view.isNeedToConvert()) {
            System.arraycopy(convertAllCurrencyToOne(posCurrency, statisticMap, 2), 0, statistic, 0, statistic.length);
        } else {
            Stream.of(statisticMap.entrySet())
                    .filter(p -> currencyArray[posCurrency].equals(p.getKey()))
                    .forEach(p -> System.arraycopy(p.getValue(), 0, statistic, 0, statistic.length));
        }
    }

    private String getCurrencyLang() {
        return currencyLangArray[view.getBalanceCurrencyPosition()];
    }
}