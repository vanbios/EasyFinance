package com.androidcollider.easyfin.managers.rates.rates_loader;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.api.RatesApi;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeNewRates;
import com.androidcollider.easyfin.managers.connection.ConnectionManager;
import com.androidcollider.easyfin.managers.shared_pref.SharedPrefManager;
import com.androidcollider.easyfin.models.Currency;
import com.androidcollider.easyfin.models.Rates;
import com.androidcollider.easyfin.models.RatesNew;
import com.androidcollider.easyfin.repository.Repository;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Ihor Bilous
 */

public class RatesLoaderManager {

    private static final String TAG = RatesLoaderManager.class.getSimpleName();

    private Context context;
    private RatesApi ratesApi;
    private Repository repository;
    private ConnectionManager connectionManager;
    private SharedPrefManager sharedPrefManager;

    RatesLoaderManager(Context context, RatesApi ratesApi, Repository repository,
                       ConnectionManager connectionManager, SharedPrefManager sharedPrefManager) {
        this.context = context;
        this.ratesApi = ratesApi;
        this.repository = repository;
        this.connectionManager = connectionManager;
        this.sharedPrefManager = sharedPrefManager;
    }

    public void updateRatesForExchange() {
        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.update_rates_automatically), true)
                && connectionManager.isConnectionEnabled()
                && (!sharedPrefManager.getRatesInsertFirstTimeStatus()
                || !checkForTodayUpdate()
                && checkForAvailableNewRates())) {
            getRates();
        }
    }

    private boolean checkForAvailableNewRates() {
        Date date = new Date();
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone, Locale.UK);

        calendar.setTime(date);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        switch (dayOfWeek) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: {
                SimpleDateFormat sdfHour = new SimpleDateFormat("HH", Locale.UK);
                sdfHour.setTimeZone(timeZone);

                if (Integer.parseInt(sdfHour.format(date)) >= 8) return true;
            }
        }
        return false;
    }

    private boolean checkForTodayUpdate() {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTimeInMillis(sharedPrefManager.getRatesUpdateTime());
        return currentCalendar.get(Calendar.DAY_OF_YEAR) == oldCalendar.get(Calendar.DAY_OF_YEAR);
    }

    private void getRates() {
        ratesApi.getRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Subscriber<RatesNew>() {
                            @Override
                            public void onNext(RatesNew ratesNew) {
                                int[] idArray = new int[]{3, 7, 11, 15};
                                String[] currencyArray = App.getContext().getResources().getStringArray(R.array.json_rates_array);
                                ArrayList<Rates> ratesList = new ArrayList<>();

                                for (int i = 0; i < idArray.length; i++) {
                                    int id = idArray[i];
                                    String cur = currencyArray[i];
                                    ratesList.add(generateNewRates(id, cur, ratesNew));
                                }
                                Log.d(TAG, "rates " + ratesList);
                                //InMemoryRepository.getInstance().getDataSource().insertRates(ratesList);
                                repository.updateRates(ratesList)
                                        .subscribe(new Subscriber<Boolean>() {

                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onNext(Boolean aBoolean) {
                                                EventBus.getDefault().post(new UpdateFrgHomeNewRates());
                                            }
                                        });
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
}