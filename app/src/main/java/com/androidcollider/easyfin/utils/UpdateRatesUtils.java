package com.androidcollider.easyfin.utils;


import com.androidcollider.easyfin.AppController;

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

    public static boolean checkForTodayUpdate() {

        SharedPref sharedPref = new SharedPref(AppController.getContext());

        Calendar currentCalendar = Calendar.getInstance();
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTimeInMillis(sharedPref.getRatesUpdateTime());

        return currentCalendar.get(Calendar.DAY_OF_YEAR) == oldCalendar.get(Calendar.DAY_OF_YEAR);
    }
}
