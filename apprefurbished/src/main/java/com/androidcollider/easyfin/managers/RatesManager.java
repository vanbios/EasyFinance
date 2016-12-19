package com.androidcollider.easyfin.managers;

import android.util.Log;

import com.androidcollider.easyfin.AppController;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.modules.RatesApiModule;
import com.androidcollider.easyfin.objects.Currency;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.objects.Rates;
import com.androidcollider.easyfin.objects.RatesNew;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Ihor Bilous
 */
public class RatesManager {

    private static final String TAG = RatesManager.class.getSimpleName();

    public void getRates() {
        getRatesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<RatesNew>() {
                            @Override
                            public void onNext(RatesNew ratesNew) {
                                int[] idArray = new int[]{3, 7, 11, 15};
                                String[] currencyArray = AppController.getContext().getResources().getStringArray(R.array.json_rates_array);
                                ArrayList<Rates> ratesList = new ArrayList<>();

                                for (int i = 0; i < idArray.length; i++) {
                                    int id = idArray[i];
                                    String cur = currencyArray[i];
                                    ratesList.add(generateNewRates(id, cur, ratesNew));
                                }
                                Log.d(TAG, "rates " + ratesList);
                                InfoFromDB.getInstance().getDataSource().insertRates(ratesList);
                            }

                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }
                        }
                );
    }

    private Rates generateNewRates(int id, String cur, RatesNew ratesNew) {
        long date = System.currentTimeMillis();
        Currency currency;
        double bid = 1, ask = 1;
        switch (id) {
            case 3:
                currency = ratesNew.getUsd();
                break;
            case 7:
                currency = ratesNew.getEur();
                break;
            case 11:
                currency = ratesNew.getRub();
                break;
            case 15:
                currency = ratesNew.getGbp();
                break;
            default:
                throw new IllegalArgumentException("id should be 3, 7, 11 or 15!");
        }
        if (currency != null) {
            bid = currency.getBid();
            ask = currency.getAsk();
        }
        return new Rates(id, date, cur, "bank", bid, ask);
    }

    private Observable<RatesNew> getRatesObservable() {
        return new RatesApiModule().getRatesApi(new ApiManager()).getRates();
    }
}