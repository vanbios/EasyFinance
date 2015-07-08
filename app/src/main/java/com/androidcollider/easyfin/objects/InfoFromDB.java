package com.androidcollider.easyfin.objects;


import android.content.Intent;

import com.androidcollider.easyfin.AppController;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.fragments.FrgMain;
import com.androidcollider.easyfin.utils.InternetTester;
import com.androidcollider.easyfin.utils.RatesParser;
import com.androidcollider.easyfin.utils.SharedPref;
import com.androidcollider.easyfin.utils.UpdateRatesUtils;

import java.util.ArrayList;



public class InfoFromDB {

    private final DataSource dataSource = new DataSource(AppController.getContext());

    private static volatile InfoFromDB instance;

    private ArrayList<Account> accountList;

    private double[] ratesForExchange;

    private final SharedPref sharedPref = new SharedPref(AppController.getContext());


    private InfoFromDB() {
        accountList = dataSource.getAllAccountsInfo();
        ratesForExchange = dataSource.getRates();
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

    public void updateRatesForExchange() {

        long ratesUpdateTime = sharedPref.getRatesUpdateTime();
        long currentTime = System.currentTimeMillis();

        if (currentTime - ratesUpdateTime > DateConstants.RATES_UPDATE_PERIOD) {

            if (InternetTester.isConnectionEnabled(AppController.getContext()) &&
                    UpdateRatesUtils.checkForAvailableNewRates()) {

                RatesParser.postRequest();
            }
        }
    }

    public void setRatesForExchange() {

        System.arraycopy(dataSource.getRates(), 0, ratesForExchange, 0, ratesForExchange.length);

        Intent intentRates = new Intent(FrgMain.BROADCAST_FRG_MAIN_ACTION);
        intentRates.putExtra(FrgMain.PARAM_STATUS_FRG_MAIN, FrgMain.STATUS_NEW_RATES);
        AppController.getContext().sendBroadcast(intentRates);
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
