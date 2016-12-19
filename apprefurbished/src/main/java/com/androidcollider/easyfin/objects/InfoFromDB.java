package com.androidcollider.easyfin.objects;


import android.content.Intent;

import com.androidcollider.easyfin.AppController;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FrgHome;
import com.androidcollider.easyfin.managers.RatesManager;
import com.androidcollider.easyfin.utils.InternetTester;
import com.androidcollider.easyfin.utils.SharedPref;
import com.androidcollider.easyfin.utils.UpdateRatesUtils;
import com.annimon.stream.Stream;

import java.util.ArrayList;

import lombok.Getter;


public class InfoFromDB {

    @Getter
    private final DataSource dataSource;
    private static volatile InfoFromDB instance;
    private ArrayList<Account> accountList;
    @Getter
    private double[] ratesForExchange;
    private SharedPref sharedPref = new SharedPref(AppController.getContext());


    private InfoFromDB() {
        dataSource = new DataSource(AppController.getContext());
        accountList = dataSource.getAllAccountsInfo();
        ratesForExchange = dataSource.getRates();
    }

    public void updateAccountList() {
        accountList = InfoFromDB.getInstance().getDataSource().getAllAccountsInfo();
    }

    public ArrayList<Account> getAccountList() {
        return accountList;
    }

    private ArrayList<String> getAccountNames() {
        ArrayList<String> accountNames = new ArrayList<>();
        Stream.of(accountList).forEach(account -> accountNames.add(account.getName()));
        return accountNames;
    }

    public boolean checkForAccountNameMatches(String name) {
        ArrayList<String> accountNames = getAccountNames();
        for (String account : accountNames) {
            if (account.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public int getAccountsNumber() {
        return accountList.size();
    }

    public void updateRatesForExchange() {
        if (InternetTester.isConnectionEnabled(AppController.getContext())
                && (!sharedPref.getRatesInsertFirstTimeStatus()
                || !UpdateRatesUtils.checkForTodayUpdate()
                && UpdateRatesUtils.checkForAvailableNewRates())) {
            new RatesManager().getRates();
        }
    }

    public void setRatesForExchange() {
        System.arraycopy(dataSource.getRates(), 0, ratesForExchange, 0, ratesForExchange.length);
        Intent intentRates = new Intent(FrgHome.BROADCAST_FRG_MAIN_ACTION);
        intentRates.putExtra(FrgHome.PARAM_STATUS_FRG_MAIN, FrgHome.STATUS_NEW_RATES);
        AppController.getContext().sendBroadcast(intentRates);
    }

    public static InfoFromDB getInstance() {
        InfoFromDB localInstance = instance;
        if (localInstance == null) {
            synchronized (InfoFromDB.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new InfoFromDB();
                }
            }
        }
        return localInstance;
    }
}
