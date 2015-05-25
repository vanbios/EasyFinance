package com.androidcollider.easyfin.utils;


import java.text.DecimalFormat;

public class FormatUtils {

    public static String doubleFormatter (double number, String format, int precise){
        DecimalFormat dfRate = new DecimalFormat(format);
        precise = 10^precise;
        number = number*precise;
        int i = (int) Math.round(number);
        double result = (double) i/precise;
        return dfRate.format(result);
    }


}
