package com.androidcollider.easyfin.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateFormat {

    public static String dateToString(Date date, String dateFormat){
        java.text.DateFormat df = new SimpleDateFormat(dateFormat, Locale.getDefault());

        String reportDate = df.format(date);
        return reportDate;
    }

    public static Date stringToDate(String dateStr, String dateFormat){
        java.text.DateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String intToDateString(int dateInt, String dateFormat){
        Date date = new Date(dateInt);
        java.text.DateFormat df = new SimpleDateFormat(dateFormat, Locale.getDefault());

        String reportDate = df.format(date);
        return reportDate;
    }
}
