package com.androidcollider.easyfin.utils;


import android.content.Context;

import com.androidcollider.easyfin.R;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;


public class ChartDataUtils {



    public static ArrayList<BarDataSet> getDataSetMainBalanceHorizontalBarChart(double[] values, Context context) {


        float cash = (float) values[0];
        float card = (float) values[1];
        float deposit = (float) values[2];
        float debt = (float) values[3];

        String[] account_type = context.getResources().getStringArray(R.array.account_type_array);

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(cash, 0);
        valueSet1.add(v1e1);

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(card, 0);
        valueSet2.add(v2e1);

        ArrayList<BarEntry> valueSet3 = new ArrayList<>();
        BarEntry v3e1 = new BarEntry(deposit, 0);
        valueSet3.add(v3e1);

        ArrayList<BarEntry> valueSet4 = new ArrayList<>();
        BarEntry v4e1 = new BarEntry(debt, 0);
        valueSet4.add(v4e1);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, account_type[0]);
        barDataSet1.setColor(context.getResources().getColor(R.color.custom_teal_dark));
        //barDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, account_type[1]);
        barDataSet2.setColor(context.getResources().getColor(R.color.custom_amber_dark));
        //barDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarDataSet barDataSet3 = new BarDataSet(valueSet3, account_type[2]);
        barDataSet3.setColor(context.getResources().getColor(R.color.custom_blue_dark));
        //barDataSet3.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarDataSet barDataSet4 = new BarDataSet(valueSet4, account_type[3]);
        barDataSet4.setColor(context.getResources().getColor(R.color.custom_red_dark));
        //barDataSet4.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet4);
        dataSets.add(barDataSet3);
        dataSets.add(barDataSet2);
        dataSets.add(barDataSet1);

        return dataSets;
    }


    public static ArrayList<BarDataSet> getDataSetMainStatisticHorizontalBarChart(double[] values, Context context) {


        float cost = (float) Math.abs(values[0]);
        float income = (float) values[1];

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(income, 0);
        valueSet1.add(v1e1);

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(cost, 0);
        valueSet2.add(v2e1);


        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "income");
        barDataSet1.setColor(context.getResources().getColor(R.color.custom_green));
        //barDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "cost");
        barDataSet2.setColor(context.getResources().getColor(R.color.custom_red));
        //barDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet2);
        dataSets.add(barDataSet1);


        return dataSets;
    }

    public static ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("");
        return xAxis;
    }


}
