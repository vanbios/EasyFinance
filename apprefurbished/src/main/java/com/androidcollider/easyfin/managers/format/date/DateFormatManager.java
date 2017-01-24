package com.androidcollider.easyfin.managers.format.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Ihor Bilous
 */

public class DateFormatManager {

    public static final String DAY_MONTH_YEAR_DOTS = "dd.MM.yyyy";
    public static final String DAY_MONTH_YEAR_SPACED = "dd MMMM yyyy";

    public String dateToString(Date date, String dateFormat) {
        return new SimpleDateFormat(dateFormat, Locale.getDefault()).format(date);
    }

    public Date stringToDate(String dateStr, String dateFormat) {
        Date date = null;
        try {
            date = new SimpleDateFormat(dateFormat, Locale.getDefault()).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String longToDateString(long dateLong, String dateFormat) {
        return new SimpleDateFormat(dateFormat, Locale.getDefault()).format(new Date(dateLong));
    }
}