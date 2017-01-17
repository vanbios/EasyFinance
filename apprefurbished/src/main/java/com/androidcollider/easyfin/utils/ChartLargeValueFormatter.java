package com.androidcollider.easyfin.utils;

import com.github.mikephil.charting.utils.ValueFormatter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * @author Ihor Bilous
 */

public class ChartLargeValueFormatter implements ValueFormatter {

    private boolean showCents;

    public ChartLargeValueFormatter(boolean b) {
        showCents = b;
    }


    private String format(float value, boolean b) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        dfs.setGroupingSeparator('\0');
        DecimalFormat df = new DecimalFormat("0.00", dfs);

        String input = df.format(value);
        int j = input.indexOf(",");
        String natural = input.substring(0, j);
        int length = natural.length();

        if (length > 3 && length <= 6) {
            StringBuilder sb = new StringBuilder(natural);
            sb.insert(sb.length() - 3, " ");
            if (b) {
                sb.insert(sb.length(), input.substring(j));
                return checkForRedundantZeros(sb.toString());
            }
            return sb.toString();
        }
        else if (length > 6 && length <= 9)
            return checkForRedundantZeros(df.format(value/1000000.0)) + "M";
        else if (length > 9)
            return checkForRedundantZeros(df.format(value/1000000000.0)) + "B";

        return b ? checkForRedundantZeros(input) : natural;
    }

    private String checkForRedundantZeros(String s) {
        int length = s.length();
        if (s.substring(length-2, length).equals("00")) return s.substring(0, length-3);
        else if (s.charAt(length-1) == '0') return s.substring(0, length-1);
        return s;
    }

    @Override
    public String getFormattedValue(float value) {
        return format(value, showCents);
    }
}