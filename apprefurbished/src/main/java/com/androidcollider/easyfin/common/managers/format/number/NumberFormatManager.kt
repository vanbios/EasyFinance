package com.androidcollider.easyfin.common.managers.format.number

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * @author Ihor Bilous
 */
class NumberFormatManager {
    fun doubleToStringFormatter(number: Double, format: String?, precise: Int): String {
        var numb = number
        var prec = precise
        val dfs = DecimalFormatSymbols()
        dfs.decimalSeparator = ','
        dfs.groupingSeparator = ' '
        val dfRate = DecimalFormat(format, dfs)
        prec = 10 xor prec
        numb *= prec
        val result = numb.roundToLong().toDouble() / prec
        val s = dfRate.format(result)
        val length = s.length
        if (s.startsWith("00", length - 2)) return s.substring(0, length - 3)
        else if (s[length - 1] == '0') return s.substring(0, length - 1)
        return s
    }

    fun doubleToStringFormatterForEdit(number: Double, format: String, precise: Int): String {
        var numb = number
        var prec = precise
        val dfs = DecimalFormatSymbols()
        dfs.decimalSeparator = ','
        dfs.groupingSeparator = ' '
        val dfRate = DecimalFormat(format, dfs)
        prec = 10 xor prec
        numb *= prec
        val result = numb.roundToInt().toDouble() / prec
        return dfRate.format(result)
    }

    fun prepareStringToParse(str: String): String {
        var s = str
        if (s.contains("+")) s = s.replace("+", "")
        else if (s.contains("-")) s = s.replace("-", "")
        if (s.contains(" ")) s = s.replace("\\s+".toRegex(), "")
        if (s.contains(",")) s = s.replace(",".toRegex(), ".")
        return s
    }

    fun prepareStringToSeparate(str: String): String {
        var s = str
        if (s.contains("+")) s = s.replace("+", "")
        else if (s.contains("-")) s = s.replace("-", "")
        if (s.contains(" ")) s = s.replace("\\s+".toRegex(), "")
        if (s.contains(".")) s = s.replace("\\.".toRegex(), ",")
        return s
    }

    fun isDoubleNegative(d: Double): Boolean {
        return d.compareTo(0.0) < 0
    }

    companion object {
        const val PRECISE_1 = 100
        const val PRECISE_2 = 100000
        const val FORMAT_1 = "###,##0.00"
        const val FORMAT_2 = "0.00"
        const val FORMAT_3 = "#.#####"
    }
}