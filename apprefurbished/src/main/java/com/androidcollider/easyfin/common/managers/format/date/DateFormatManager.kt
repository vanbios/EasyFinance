package com.androidcollider.easyfin.common.managers.format.date

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Ihor Bilous
 */
class DateFormatManager {

    fun dateToString(date: Date, dateFormat: String): String {
        return SimpleDateFormat(dateFormat, Locale.getDefault()).format(date)
    }

    fun stringToDate(dateStr: String, dateFormat: String): Date? {
        var date: Date? = null
        try {
            date = SimpleDateFormat(dateFormat, Locale.getDefault()).parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    fun longToDateString(dateLong: Long, dateFormat: String): String {
        return SimpleDateFormat(dateFormat, Locale.getDefault()).format(Date(dateLong))
    }

    companion object {
        const val DAY_MONTH_YEAR_DOTS = "dd.MM.yyyy"
        const val DAY_MONTH_YEAR_SPACED = "dd MMMM yyyy"
    }
}