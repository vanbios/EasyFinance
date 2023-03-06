package com.androidcollider.easyfin.common.managers.chart.setup

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.utils.ChartLargeValueFormatter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart

/**
 * @author Ihor Bilous
 */
class ChartSetupManager internal constructor(private val context: Context) {

    fun setupMainBarChart(chart: BarChart) {
        val leftAxis = chart.axisLeft
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
        leftAxis.spaceTop = 35f
        leftAxis.setLabelCount(3, false)
        leftAxis.valueFormatter = ChartLargeValueFormatter(false)
        val xAxis = chart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)
        xAxis.setDrawGridLines(false)
        leftAxis.axisLineColor = ContextCompat.getColor(context, R.color.custom_light_gray)
        leftAxis.gridColor = ContextCompat.getColor(context, R.color.custom_light_gray)
        leftAxis.textColor = ContextCompat.getColor(context, R.color.custom_text_gray_dark)
        chart.setDrawGridBackground(false)
        chart.setBackgroundColor(Color.TRANSPARENT)
        chart.setDrawBorders(false)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setFitBars(true)
        chart.setTouchEnabled(false)
    }

    fun setupMainPieChart(chart: PieChart) {
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.TRANSPARENT)
        chart.holeRadius = 45f
        chart.transparentCircleRadius = 48f
        chart.rotationAngle = 0f
        chart.isRotationEnabled = true
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.highlightValues(null)
    }
}