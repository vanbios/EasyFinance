package com.androidcollider.easyfin.home;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;

import java.util.Map;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

public interface HomeMVP {

    interface Model {

        Flowable<Pair<Map<String, double[]>, Map<String, double[]>>> getBalanceAndStatistic(int statisticPosition);

        Flowable<Map<String, double[]>> getBalance();

        Flowable<Map<String, double[]>> getStatistic(int statisticPosition);
    }

    interface View {

        void setBalanceAndStatistic(Pair<Map<String, double[]>, Map<String, double[]>> mapPair);

        void updateBalanceAndStatistic(Pair<Map<String, double[]>, Map<String, double[]>> mapPair);

        void updateBalanceAndStatisticAfterDBImport(Pair<Map<String, double[]>, Map<String, double[]>> mapPair);

        void updateBalance(Map<String, double[]> map);

        void updateStatistic(Map<String, double[]> map);

        boolean isNeedToConvert();

        int getBalanceCurrencyPosition();
    }

    interface Presenter {

        void setView(@Nullable HomeMVP.View view);

        void loadBalanceAndStatistic(int statisticPosition);

        void updateBalanceAndStatistic(int statisticPosition);

        void updateBalanceAndStatisticAfterDBImport(int statisticPosition);

        void updateBalance();

        void updateStatistic(int statisticPosition);

        boolean isStatisticEmpty();

        String getFormattedBalance(double[] balance);

        String getFormattedStatistic();

        double[] getCurrentBalance(int position);

        void updateTransactionStatisticArray(int posCurrency);

        BarData getDataSetMainBalanceHorizontalBarChart(double[] balance);

        BarData getDataSetMainStatisticHorizontalBarChart();

        PieData getDataSetMainStatisticPieChart();

        void updateRates();
    }
}