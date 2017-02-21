package com.androidcollider.easyfin.common.managers.rates.rates_loader;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.api.RatesApi;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeNewRates;
import com.androidcollider.easyfin.common.managers.connection.ConnectionManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager;
import com.androidcollider.easyfin.common.models.Currency;
import com.androidcollider.easyfin.common.models.Rates;
import com.androidcollider.easyfin.common.models.RatesRemote;
import com.androidcollider.easyfin.common.repository.Repository;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    private ResourcesManager resourcesManager;

    RatesLoaderManager(Context context, RatesApi ratesApi, Repository repository,
                       ConnectionManager connectionManager, SharedPrefManager sharedPrefManager,
                       ResourcesManager resourcesManager) {
        this.context = context;
        this.ratesApi = ratesApi;
        this.repository = repository;
        this.connectionManager = connectionManager;
        this.sharedPrefManager = sharedPrefManager;
        this.resourcesManager = resourcesManager;
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
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTimeInMillis(sharedPrefManager.getRatesUpdateTime());
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR) == oldCalendar.get(Calendar.DAY_OF_YEAR);
    }

    private void getRates() {
        ratesApi.getRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        ratesRemote -> {
                            int[] idArray = new int[]{3, 7, 11, 15};
                            String[] currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_JSON_RATES);
                            ArrayList<Rates> ratesList = new ArrayList<>();

                            for (int i = 0; i < idArray.length; i++) {
                                int id = idArray[i];
                                String cur = currencyArray[i];
                                ratesList.add(generateNewRates(id, cur, ratesRemote));
                            }
                            Log.d(TAG, "rates " + ratesList);

                            repository.updateRates(ratesList)
                                    .subscribe(
                                            aBoolean -> {
                                                EventBus.getDefault().post(new UpdateFrgHomeNewRates());
                                            },
                                            Throwable::printStackTrace);
                        },
                        Throwable::printStackTrace
                );
    }

    private Rates generateNewRates(int id, String cur, RatesRemote ratesRemote) {
        long date = System.currentTimeMillis();
        Currency currency;
        double bid = 1, ask = 1;
        switch (id) {
            case 3:
                currency = ratesRemote.getUsd();
                break;
            case 7:
                currency = ratesRemote.getEur();
                break;
            case 11:
                currency = ratesRemote.getRub();
                break;
            case 15:
                currency = ratesRemote.getGbp();
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