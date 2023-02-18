package com.androidcollider.easyfin.common.managers.chart.data;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Ihor Bilous
 */

public class ChartDataManager {

    private final NumberFormatManager numberFormatManager;
    private final ResourcesManager resourcesManager;
    private final Context context;

    private final int textColor;

    ChartDataManager(NumberFormatManager numberFormatManager, ResourcesManager resourcesManager, Context context) {
        this.numberFormatManager = numberFormatManager;
        this.resourcesManager = resourcesManager;
        this.context = context;
        textColor = ContextCompat.getColor(context, R.color.custom_text_gray_dark);
    }


    public BarData getDataSetMainBalanceHorizontalBarChart(double[] values) {
        float cash = (float) values[0];
        float card = (float) values[1];
        float deposit = (float) values[2];
        float debt = (float) Math.abs(values[3]);

        String[] accountType = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_TYPE);

        List<BarEntry> valueSet1 = Collections.singletonList(new BarEntry(4, cash));
        List<BarEntry> valueSet2 = Collections.singletonList(new BarEntry(3, card));
        List<BarEntry> valueSet3 = Collections.singletonList(new BarEntry(2, deposit));
        List<BarEntry> valueSet4 = Collections.singletonList(new BarEntry(1, debt));

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, accountType[0]);
        barDataSet1.setColor(ContextCompat.getColor(context, R.color.custom_teal_dark));

        BarDataSet barDataSet2 = new BarDataSet(valueSet2, accountType[1]);
        barDataSet2.setColor(ContextCompat.getColor(context, R.color.custom_amber_dark));

        BarDataSet barDataSet3 = new BarDataSet(valueSet3, accountType[2]);
        barDataSet3.setColor(ContextCompat.getColor(context, R.color.custom_blue_dark));

        BarDataSet barDataSet4 = new BarDataSet(valueSet4, context.getResources().getString(R.string.debts));

        barDataSet4.setColor(
                numberFormatManager.isDoubleNegative(values[3]) ?
                        ContextCompat.getColor(context, R.color.custom_red) :
                        ContextCompat.getColor(context, R.color.custom_green)
        );

        return getBarDataFromDataSets(Arrays.asList(barDataSet4, barDataSet3, barDataSet2, barDataSet1));
    }


    public BarData getDataSetMainStatisticHorizontalBarChart(double[] values) {
        float cost = (float) Math.abs(values[0]);
        float income = (float) values[1];

        List<BarEntry> valueSet1 = Collections.singletonList(new BarEntry(2, income));
        List<BarEntry> valueSet2 = Collections.singletonList(new BarEntry(1, cost));

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "income");
        barDataSet1.setColor(ContextCompat.getColor(context, R.color.custom_green));

        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "cost");
        barDataSet2.setColor(ContextCompat.getColor(context, R.color.custom_red));

        return getBarDataFromDataSets(Arrays.asList(barDataSet2, barDataSet1));
    }

    private BarData getBarDataFromDataSets(List<IBarDataSet> dataSets) {
        BarData data = new BarData(dataSets);
        data.setValueTextSize(12f);
        data.setValueTextColor(textColor);

        return data;
    }

    public PieData getDataSetMainStatisticPieChart(double[] values) {
        float cost = (float) Math.abs(values[0]);
        float income = (float) values[1];

        List<PieEntry> valueSet = Arrays.asList(
                new PieEntry(income),
                new PieEntry(cost)
        );

        PieDataSet pieDataSet = new PieDataSet(valueSet, "statistic");

        int[] colors = {
                ContextCompat.getColor(context, R.color.custom_green),
                ContextCompat.getColor(context, R.color.custom_red)
        };

        pieDataSet.setColors(colors);

        pieDataSet.setSliceSpace(3);
        pieDataSet.setSelectionShift(5);

        PieData data = new PieData(pieDataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(textColor);

        return data;
    }
}