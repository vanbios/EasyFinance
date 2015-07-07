package com.androidcollider.easyfin.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DateFormatUtils {


    public static String dateToString(Date date, String dateFormat){
        java.text.DateFormat df = new SimpleDateFormat(dateFormat, Locale.getDefault());

        return df.format(date);

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

    public static String longToDateString(long dateLong, String dateFormat){
        Date date = new Date(dateLong);
        java.text.DateFormat df = new SimpleDateFormat(dateFormat, Locale.getDefault());

        return df.format(date);
    }

}
