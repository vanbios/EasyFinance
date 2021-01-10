package com.androidcollider.easyfin.common.managers.rates.rates_info;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.repository.Repository;

/**
 * @author Ihor Bilous
 */

public class RatesInfoManager {

    private static final long DOUBLE_PRESS_INTERVAL = 1000;
    private long lastPressTime = 0;
    private int count = 0;

    private String info;
    private final Repository repository;
    private final ToastManager toastManager;
    private final ResourcesManager resourcesManager;


    RatesInfoManager(Repository repository, ToastManager toastManager, ResourcesManager resourcesManager) {
        this.repository = repository;
        this.toastManager = toastManager;
        this.resourcesManager = resourcesManager;
        prepareInfo();
    }

    public void setupMultiTapListener(View view, final Context context) {
        view.setOnTouchListener((view1, motionEvent) -> {
            long currentTime = System.currentTimeMillis();
            if (count == 0) lastPressTime = currentTime;

            if (currentTime - lastPressTime < DOUBLE_PRESS_INTERVAL) {
                count++;
                if (count == 7) {
                    toastManager.showClosableToast(context, info, ToastManager.LONG);
                }
            } else count = 0;

            lastPressTime = currentTime;

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view1.performClick();
            }

            return false;
        });
    }

    public void prepareInfo() {
        repository.getRates()
                .subscribe(
                        rates -> {
                            String[] currency = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);

                            StringBuilder sb = new StringBuilder();

                            for (int i = 0; i <= rates.length; i++) {
                                sb.append(currency[i]);
                                sb.append(" - ");
                                sb.append(i == 0 ? 1 : rates[i - 1]);
                                sb.append("; ");
                            }

                            info = sb.toString();
                        },
                        Throwable::printStackTrace
                );
    }
}