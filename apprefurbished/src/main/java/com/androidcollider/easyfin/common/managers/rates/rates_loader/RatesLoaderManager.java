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
import com.androidcollider.easyfin.common.models.Rates;
import com.androidcollider.easyfin.common.models.RatesRemote;
import com.androidcollider.easyfin.common.repository.Repository;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author Ihor Bilous
 */

public class RatesLoaderManager {

    private static final String TAG = RatesLoaderManager.class.getSimpleName();

    private final Context context;
    private final RatesApi ratesApi;
    private final Repository repository;
    private final ConnectionManager connectionManager;
    private final SharedPrefManager sharedPrefManager;
    private final ResourcesManager resourcesManager;

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
                //&& checkForAvailableNewRates()
        )) {
            getRates();
        }
    }

    /*private boolean checkForAvailableNewRates() {
        Date date = new Date();
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone, Locale.UK);

        calendar.setTime(date);

        SimpleDateFormat sdfHour = new SimpleDateFormat("HH", Locale.UK);
        sdfHour.setTimeZone(timeZone);

        return Integer.parseInt(sdfHour.format(date)) >= 8;
    }*/

    private boolean checkForTodayUpdate() {
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTimeInMillis(sharedPrefManager.getRatesUpdateTime());
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR) == oldCalendar.get(Calendar.DAY_OF_YEAR);
    }

    private void getRates() {
        ratesApi.getRates("json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        ratesRemoteList -> {
                            int[] idArray = new int[]{3, 7, 11, 15};
                            String[] currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_JSON_RATES);
                            ArrayList<Rates> ratesList = new ArrayList<>();

                            for (int i = 0; i < idArray.length; i++) {
                                int id = idArray[i];
                                String cur = currencyArray[i];
                                ratesList.add(generateNewRates(id, cur, ratesRemoteList));
                            }
                            Log.d(TAG, "rates " + ratesList);

                            repository.updateRates(ratesList)
                                    .subscribe(
                                            aBoolean -> EventBus.getDefault().post(new UpdateFrgHomeNewRates()),
                                            Throwable::printStackTrace);
                        },
                        Throwable::printStackTrace
                );
    }

    private Rates generateNewRates(int id, String cur, List<RatesRemote> ratesRemoteList) {
        long date = System.currentTimeMillis();
        double rate = 1;
        for (RatesRemote ratesRemote : ratesRemoteList) {
            if (cur.equals(ratesRemote.getCc().toLowerCase())) {
                rate = ratesRemote.getRate();
                break;
            }
        }
        return new Rates(id, date, cur, "bank", rate, rate);
    }
}