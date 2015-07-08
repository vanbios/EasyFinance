package com.androidcollider.easyfin.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UpdateRatesUtils {


    public static boolean checkForAvailableNewRates() {

        Date date = new Date();
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone, Locale.UK);

        calendar.setTime(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        switch (dayOfWeek) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            {
                SimpleDateFormat sdfHour = new SimpleDateFormat("HH", Locale.UK);
                sdfHour.setTimeZone(timeZone);

                int hour = Integer.parseInt(sdfHour.format(date));

                if (hour >= 8) {
                    return true;
                }
            }
        }
        return false;
    }
}
