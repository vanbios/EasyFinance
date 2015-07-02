package com.androidcollider.easyfin.utils;


import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ChartLargeValueFormatter implements ValueFormatter {

    private static final NavigableMap<Double, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000d, "k");
        suffixes.put(1_000_000d, "M");
        suffixes.put(1_000_000_000d, "G");
        suffixes.put(1_000_000_000_000d, "T");
        suffixes.put(1_000_000_000_000_000d, "P");
        suffixes.put(1_000_000_000_000_000_000d, "E");
    }

    public String format(float value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here

        double d = ((long) value / 100) / 10.0;
        if (d == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        //if (d < 0) return "-" + format(-d);
        if (d < 1000) return Double.toString(d); //deal with easy case

        Map.Entry<Double, String> e = suffixes.floorEntry(d);
        Double divideBy = e.getKey();
        String suffix = e.getValue();

        double truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    @Override
    public String getFormattedValue(float value) {
        return format(value);
    }




    /*private static final String[] SUFFIX = new String[] {
            "", "k", "m", "b", "t"
    };
    private static final int MAX_LENGTH = 4;

    private DecimalFormat mFormat;
    private String mText = "";

    public ChartLargeValueFormatter() {
        mFormat = new DecimalFormat("###E0");
    }


    public ChartLargeValueFormatter(String appendix) {
        this();
        mText = appendix;
    }

    @Override
    public String getFormattedValue(float value) {
        return makePretty(value) + mText;
    }


    private String makePretty(double number) {

        String r = mFormat.format(number);

        r = r.replaceAll("E[0-9]", SUFFIX[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);

        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }

        return r;
    }*/



    /*public ChartLargeValueFormatter() {

    }


    @Override
    public String getFormattedValue(float value) {
        return coolFormat(value, 0);
    }


    private static char[] c = new char[]{'k', 'm', 'b', 't'};

    /**
     * Recursive implementation, invokes itself for each factor of a thousand, increasing the class on each invokation.
     * @param n the number to format
     * @param iteration in fact this is the class from the array c
     * @return a String representing the number n formatted in a cool looking way.
     */
    /*private static String coolFormat(double n, int iteration) {
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration + 1));
    }*/

}
