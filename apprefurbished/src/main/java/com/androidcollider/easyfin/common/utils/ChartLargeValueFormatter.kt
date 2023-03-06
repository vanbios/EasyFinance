package com.androidcollider.easyfin.common.utils

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * @author Ihor Bilous
 */
class ChartLargeValueFormatter(private val showCents: Boolean) : ValueFormatter() {
    private fun format(value: Float, b: Boolean): String {
        val dfs = DecimalFormatSymbols()
        dfs.decimalSeparator = ','
        dfs.groupingSeparator = '\u0000'
        val df = DecimalFormat("0.00", dfs)
        val input = df.format(value.toDouble())
        val j = input.indexOf(",")
        val natural = input.substring(0, j)
        val length = natural.length
        if (length in 4..6) {
            val sb = StringBuilder(natural)
            sb.insert(sb.length - 3, " ")
            if (b) {
                sb.insert(sb.length, input.substring(j))
                return checkForRedundantZeros(sb.toString())
            }
            return sb.toString()
        } else if (length in 7..9)
            return checkForRedundantZeros(df.format(value / 1000000.0)) + "M"
        else if (length > 9)
            return checkForRedundantZeros(df.format(value / 1000000000.0)) + "B"
        return if (b) checkForRedundantZeros(input) else natural
    }

    private fun checkForRedundantZeros(s: String): String {
        val length = s.length
        if (s.startsWith("00", length - 2))
            return s.substring(0, length - 3)
        else if (s[length - 1] == '0')
            return s.substring(0, length - 1)
        return s
    }

    override fun getFormattedValue(value: Float): String {
        return format(value, showCents)
    }
}