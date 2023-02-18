package com.androidcollider.easyfin.common.managers.chart.setup;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.utils.ChartLargeValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

/**
 * @author Ihor Bilous
 */

public class ChartSetupManager {

    private final Context context;

    ChartSetupManager(Context context) {
        this.context = context;
    }

    public void setupMainBarChart(BarChart chart) {
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setLabelCount(3, false);
        leftAxis.setValueFormatter(new ChartLargeValueFormatter(false));

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);

        leftAxis.setAxisLineColor(ContextCompat.getColor(context, R.color.custom_light_gray));
        leftAxis.setGridColor(ContextCompat.getColor(context, R.color.custom_light_gray));
        leftAxis.setTextColor(ContextCompat.getColor(context, R.color.custom_text_gray_dark));

        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setDrawBorders(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.setFitBars(true);

        chart.setTouchEnabled(false);
    }

    public void setupMainPieChart(PieChart chart) {
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setHoleRadius(45);
        chart.setTransparentCircleRadius(48);

        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.highlightValues(null);
    }
}