package com.androidcollider.easyfin.utils;


import com.androidcollider.easyfin.AppController;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.InfoFromDB;


public class ExchangeUtils {


    public static double getExchangeRate (String currFrom, String currTo) {

        double[] rates = getRates();

        String[] currencyArray = AppController.getContext().getResources().getStringArray(R.array.account_currency_array);


        int posFrom = 0;
        int posTo = 0;

        for (int i = 0; i < currencyArray.length; i++) {

            if (currencyArray[i].equals(currFrom)) {
                posFrom = i;
            }

            if (currencyArray[i].equals(currTo)) {
                posTo = i;
            }
        }

        return rates[posTo] / rates[posFrom];
    }

    public static double[] getRates() {

        double[] rates = new double[5];

        rates[0] = 1;
        rates[1] = 24;
        rates[2] = 26.3;
        rates[3] = 0.42;
        rates[4] = 37.5;


        double[] newRates = InfoFromDB.getInstance().getRatesForExchange();

        for (int i = 1; i < rates.length; i++) {

            if (newRates[i-1] > 0) {
                rates[i] = newRates[i-1];
            }
        }

        return rates;
    }

}
