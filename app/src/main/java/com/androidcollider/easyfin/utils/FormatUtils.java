package com.androidcollider.easyfin.utils;


import android.support.annotation.NonNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtils {

    @NonNull
    public static String doubleFormatter (double number, String format, int precise){
        //NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
        DecimalFormat dfRate = new DecimalFormat(format);
        precise = 10^precise;
        number = number*precise;
        int i = (int) Math.round(number);
        double result = (double) i/precise;
        String s = dfRate.format(result);

        return s.replace(",", ".");
    }

    public static boolean isDoubleNegative(double d) {
        return Double.compare(d, 0.0) < 0;
    }


}
