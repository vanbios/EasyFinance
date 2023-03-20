package com.androidcollider.easyfin.common.managers.chart.setup

import android.graphics.Color
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.utils.ChartLargeValueFormatter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart

/**
 * @author Ihor Bilous
 */
class ChartSetupManager internal constructor(private val resourcesManager: ResourcesManager) {

    fun setupMainBarChart(chart: BarChart) {
        val leftAxis = chart.axisLeft
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
        leftAxis.spaceTop = 35f
        leftAxis.setLabelCount(3, false)
        leftAxis.valueFormatter = ChartLargeValueFormatter(false)
        leftAxis.axisMinimum = 0f
        leftAxis.axisLineColor =
            resourcesManager.getColorFromAttr(R.attr.colorOutlineVariant, chart)
        leftAxis.gridColor = resourcesManager.getColorFromAttr(R.attr.colorOutlineVariant, chart)
        leftAxis.textColor = resourcesManager.getColorFromAttr(R.attr.colorOnSurfaceVariant, chart)

        val xAxis = chart.xAxis
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(false)
        xAxis.setDrawGridLines(false)

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