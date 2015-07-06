package com.androidcollider.easyfin.objects;


import android.util.Log;

import com.androidcollider.easyfin.AppController;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.utils.RatesParser;
import com.androidcollider.easyfin.utils.SharedPref;

import java.util.ArrayList;
import java.util.Date;


public class InfoFromDB {

    private final DataSource dataSource = new DataSource(AppController.getContext());

    private static volatile InfoFromDB instance;

    private ArrayList<Account> accountList;

    private double[] ratesForExchange;

    private final SharedPref sharedPref = new SharedPref(AppController.getContext());


    private InfoFromDB() {
        accountList = dataSource.getAllAccountsInfo();
        ratesForExchange = new double[4];
        setRatesForExchange();
    }

    public void updateAccountList() {
        accountList = InfoFromDB.getInstance().getDataSource().getAllAccountsInfo();
    }

    public ArrayList<Account> getAccountList() {
        return accountList;
    }

    public ArrayList<String> getAccountNames() {
        ArrayList<String> accountNames = new ArrayList<>();

        for (Account account : accountList) {
            accountNames.add(account.getName());
        }

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

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setRatesForExchange() {

        boolean ratesInDBStatus = sharedPref.getRatesInDBExistStatus();

        if (ratesInDBStatus) {
            long ratesUpdateTime = sharedPref.getRatesUpdateTime();
            long currentTime = new Date().getTime();

            if (currentTime - ratesUpdateTime > DateConstants.RATES_UPDATE_PERIOD) {

                RatesParser.postRequest();
            }

            System.arraycopy(dataSource.getRates(), 0, ratesForExchange, 0 , ratesForExchange.length);

            for (double d : ratesForExchange) {

                Log.d("RATES", String.valueOf(d));
            }
        }
    }

    public double[] getRatesForExchange() {
        return ratesForExchange;
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
