package com.androidcollider.easyfin.utils;


import android.support.annotation.NonNull;

import java.text.DecimalFormat;

public class FormatUtils {

    @NonNull
    public static String doubleFormatter (double number, String format, int precise){
        DecimalFormat dfRate = new DecimalFormat(format);
        precise = 10^precise;
        number = number*precise;
        int i = (int) Math.round(number);
        double result = (double) i/precise;
        return dfRate.format(result);
    }

    public static boolean isDoubleNegative(double d) {
        return Double.compare(d, 0.0) < 0;
    }


}
