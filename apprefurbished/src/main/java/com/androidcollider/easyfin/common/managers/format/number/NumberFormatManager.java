package com.androidcollider.easyfin.common.managers.format.number;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * @author Ihor Bilous
 */

public class NumberFormatManager {

    public static final int PRECISE_1 = 100;
    public static final int PRECISE_2 = 100_000;
    public static final String FORMAT_1 = "###,##0.00";
    public static final String FORMAT_2 = "0.00";
    public static final String FORMAT_3 = "#.#####";


    public String doubleToStringFormatter(double number, String format, int precise) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        dfs.setGroupingSeparator(' ');

        DecimalFormat dfRate = new DecimalFormat(format, dfs);
        precise = 10 ^ precise;
        number = number * precise;
        double result = (double) Math.round(number) / precise;
        String s = dfRate.format(result);

        int length = s.length();
        if (s.startsWith("00", length - 2))
            return s.substring(0, length - 3);
        else if (s.charAt(length - 1) == '0')
            return s.substring(0, length - 1);

        return s;
    }

    public String doubleToStringFormatterForEdit(double number, String format, int precise) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        dfs.setGroupingSeparator(' ');

        DecimalFormat dfRate = new DecimalFormat(format, dfs);
        precise = 10 ^ precise;
        number = number * precise;
        double result = (double) Math.round(number) / precise;

        return dfRate.format(result);
    }

    public String prepareStringToParse(String s) {
        if (s.contains("+"))
            s = s.replace("+", "");
        else if (s.contains("-"))
            s = s.replace("-", "");

        if (s.contains(" "))
            s = s.replaceAll("\\s+", "");
        if (s.contains(","))
            s = s.replaceAll(",", ".");

        return s;
    }

    public String prepareStringToSeparate(String s) {
        if (s.contains("+"))
            s = s.replace("+", "");
        else if (s.contains("-"))
            s = s.replace("-", "");

        if (s.contains(" "))
            s = s.replaceAll("\\s+", "");
        if (s.contains("."))
            s = s.replaceAll("\\.", ",");

        return s;
    }


    public boolean isDoubleNegative(double d) {
        return Double.compare(d, 0.0) < 0;
    }
}