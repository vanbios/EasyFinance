package com.androidcollider.easyfin.utils;


import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.androidcollider.easyfin.R;



public class MultiTapUtils {

    private static final long DOUBLE_PRESS_INTERVAL = 1000;
    private static long lastPressTime = 0;
    private static int count = 0;


    public static void multiTapListener(View view, final Context context) {

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //Log.d("COLLIDER", String.valueOf(count));


                long currentTime = System.currentTimeMillis();

                if (count == 0) {
                    lastPressTime = currentTime;
                }


                if (currentTime - lastPressTime < DOUBLE_PRESS_INTERVAL) {
                    count++;

                    if (count == 7) {

                        String rates = prepareContentForToast(context);
                        //Toast.makeText(context, rates, Toast.LENGTH_LONG).show();
                        ToastUtils.showClosableToast(context, rates, 1);
                    }
                }

                else {
                    count = 0;
                }

                lastPressTime = currentTime;

                return false;
            }
        });
    }


    private static String prepareContentForToast(Context context) {

        double[] rates = ExchangeUtils.getRates();
        String[] currency = context.getResources().getStringArray(R.array.account_currency_array);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < rates.length; i++) {

            sb.append(currency[i]);
            sb.append(" - ");
            sb.append(rates[i]);
            sb.append("; ");
        }

        return sb.toString();
    }
}
