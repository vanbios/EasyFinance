package com.androidcollider.easyfin.managers.chart;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.managers.format.DoubleFormatManager;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Ihor Bilous
 */

public class ChartDataManager {

    private DoubleFormatManager doubleFormatManager;

    public ChartDataManager(DoubleFormatManager doubleFormatManager) {
        this.doubleFormatManager = doubleFormatManager;
    }


    public BarData getDataSetMainBalanceHorizontalBarChart(double[] values, Context context) {
        float cash = (float) values[0];
        float card = (float) values[1];
        float deposit = (float) values[2];
        float debt = (float) Math.abs(values[3]);

        String[] accountType = context.getResources().getStringArray(R.array.account_type_array);

        List<BarEntry> valueSet1 = Collections.singletonList(new BarEntry(cash, 0));
        List<BarEntry> valueSet2 = Collections.singletonList(new BarEntry(card, 0));
        List<BarEntry> valueSet3 = Collections.singletonList(new BarEntry(deposit, 0));
        List<BarEntry> valueSet4 = Collections.singletonList(new BarEntry(debt, 0));

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, accountType[0]);
        barDataSet1.setColor(ContextCompat.getColor(context, R.color.custom_teal_dark));

        BarDataSet barDataSet2 = new BarDataSet(valueSet2, accountType[1]);
        barDataSet2.setColor(ContextCompat.getColor(context, R.color.custom_amber_dark));

        BarDataSet barDataSet3 = new BarDataSet(valueSet3, accountType[2]);
        barDataSet3.setColor(ContextCompat.getColor(context, R.color.custom_blue_dark));

        BarDataSet barDataSet4 = new BarDataSet(valueSet4, context.getResources().getString(R.string.debts));

        barDataSet4.setColor(
                doubleFormatManager.isDoubleNegative(values[3]) ?
                        ContextCompat.getColor(context, R.color.custom_red) :
                        ContextCompat.getColor(context, R.color.custom_green)
        );

        List<BarDataSet> dataSets = Arrays.asList(barDataSet4, barDataSet3, barDataSet2, barDataSet1);

        BarData data = new BarData(getXAxisValues(), dataSets);
        data.setValueTextSize(12f);
        data.setValueTextColor(context.getResources().getColor(R.color.custom_text_gray_dark));

        return data;
    }


    public BarData getDataSetMainStatisticHorizontalBarChart(double[] values, Context context) {
        float cost = (float) Math.abs(values[0]);
        float income = (float) values[1];

        List<BarEntry> valueSet1 = Collections.singletonList(new BarEntry(income, 0));
        List<BarEntry> valueSet2 = Collections.singletonList(new BarEntry(cost, 0));

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "income");
        barDataSet1.setColor(ContextCompat.getColor(context, R.color.custom_green));

        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "cost");
        barDataSet2.setColor(ContextCompat.getColor(context, R.color.custom_red));

        List<BarDataSet> dataSets = Arrays.asList(barDataSet2, barDataSet1);

        BarData data = new BarData(getXAxisValues(), dataSets);
        data.setValueTextSize(12f);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.custom_text_gray_dark));

        return data;
    }

    private List<String> getXAxisValues() {
        return Collections.singletonList("");
    }

    private List<String> getXAxisValuesPie() {
        return Arrays.asList("", "");
    }

    public PieData getDataSetMainStatisticPieChart(double[] values, Context context) {
        float cost = (float) Math.abs(values[0]);
        float income = (float) values[1];

        List<Entry> valueSet = Arrays.asList(
                new Entry(income, 0),
                new Entry(cost, 0)
        );

        PieDataSet pieDataSet = new PieDataSet(valueSet, "statistic");

        int[] colors = {
                ContextCompat.getColor(context, R.color.custom_green),
                ContextCompat.getColor(context, R.color.custom_red)
        };

        pieDataSet.setColors(colors);

        pieDataSet.setSliceSpace(3);
        pieDataSet.setSelectionShift(5);

        PieData data = new PieData(getXAxisValuesPie(), pieDataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.custom_text_light));

        return data;
    }
}
