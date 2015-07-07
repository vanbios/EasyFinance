package com.androidcollider.easyfin.utils;


import android.util.Log;

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
        double[] rates = new double[4];

        rates[0] = 1;
        rates[1] = 21.93;
        rates[2] = 24.58;
        rates[3] = 0.4;

        /*System.arraycopy(InfoFromDB.getInstance().getRatesForExchange(), 0, rates, 1, rates.length-1);


        for (double d : rates) {

            Log.d("COLLIDER", String.valueOf(d));
        }*/

        return rates;
    }

}
