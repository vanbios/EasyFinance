package com.androidcollider.easyfin.managers.rates.rates_info;

import android.content.Context;
import android.view.View;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.repository.Repository;
import com.androidcollider.easyfin.utils.ToastUtils;

import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

public class RatesInfoManager {

    private static final long DOUBLE_PRESS_INTERVAL = 1000;
    private long lastPressTime = 0;
    private int count = 0;

    private String info;
    private Context context;
    private Repository repository;


    RatesInfoManager(Context context, Repository repository) {
        this.context = context;
        this.repository = repository;
        prepareInfo();
    }

    public void setupMultiTapListener(View view, final Context context) {
        view.setOnTouchListener((view1, motionEvent) -> {
            long currentTime = System.currentTimeMillis();
            if (count == 0) lastPressTime = currentTime;

            if (currentTime - lastPressTime < DOUBLE_PRESS_INTERVAL) {
                count++;
                if (count == 7) {
                    ToastUtils.showClosableToast(context, info, 1);
                }
            } else count = 0;

            lastPressTime = currentTime;
            return false;
        });
    }

    private void prepareInfo() {
        repository.getRates()
                .subscribe(new Subscriber<double[]>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(double[] rates) {
                        String[] currency = context.getResources().getStringArray(R.array.account_currency_array);

                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i <= rates.length; i++) {
                            sb.append(currency[i]);
                            sb.append(" - ");
                            sb.append(i == 0 ? 1 : rates[i - 1]);
                            sb.append("; ");
                        }

                        info = sb.toString();
                    }
                });
    }
}