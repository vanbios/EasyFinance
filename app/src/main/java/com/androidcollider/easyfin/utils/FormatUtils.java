package com.androidcollider.easyfin.utils;


import android.support.annotation.NonNull;

import java.text.DecimalFormat;


public class FormatUtils {

    @NonNull
    public static String doubleFormatter (double number, String format, int precise){

        DecimalFormat dfRate = new DecimalFormat(format);
        precise = 10^precise;
        number = number*precise;
        long i = Math.round(number);
        double result = (double) i/precise;
        String s = dfRate.format(result);


        int length = s.length();

        if (s.substring(length-2, length).equals("00")) {
            return s.substring(0, length-3);
        }

        else if (s.substring(length-1, length).equals("0")) {
            return s.substring(0, length-1);
        }

        return s;
    }

    public static boolean isDoubleNegative(double d) {
        return Double.compare(d, 0.0) < 0;
    }

    /*public static double stringToDouble(String s) {
        return Double.parseDouble(s.replace(",", "."));
    }*/

}
