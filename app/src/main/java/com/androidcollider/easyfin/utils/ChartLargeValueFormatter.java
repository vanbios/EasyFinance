package com.androidcollider.easyfin.utils;


import com.github.mikephil.charting.utils.ValueFormatter;
import java.text.DecimalFormat;


public class ChartLargeValueFormatter implements ValueFormatter {

    private boolean showCents;


    public ChartLargeValueFormatter(boolean b) {
        showCents = b;
    }


    private String format(float value, boolean b) {

        String input = new DecimalFormat("0.00").format(value);
        int j = input.indexOf(",");

        String natural = input.substring(0, j);

        int length = natural.length();


        String resBig;

        if (length > 6 && length <= 9) {
            resBig = String.format("%.2f", value/1000000.0);

            return checkForRedundantZeros(resBig) + "M";
        }

        else if (length > 9) {
            resBig = String.format("%.2f", value/1000000000.0);

            return checkForRedundantZeros(resBig) + "B";
        }


        if (b) {

            return checkForRedundantZeros(input);
        }

        return natural;
    }


    private String checkForRedundantZeros(String s) {

        int length = s.length();

        if (s.substring(length-2, length).equals("00")) {
            return s.substring(0, length-3);
        }

        else if (s.charAt(length-1) == '0') {
            return s.substring(0, length-1);
        }

        return s;
    }


    @Override
    public String getFormattedValue(float value) {
        return format(value, showCents);
    }

}
