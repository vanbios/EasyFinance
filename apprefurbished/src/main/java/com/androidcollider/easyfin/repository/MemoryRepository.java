package com.androidcollider.easyfin.repository;


import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.repository.database.DataSource;
import com.androidcollider.easyfin.events.UpdateFrgHomeNewRates;
import com.androidcollider.easyfin.managers.RatesManager;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.utils.InternetTester;
import com.androidcollider.easyfin.utils.SharedPref;
import com.androidcollider.easyfin.utils.UpdateRatesUtils;
import com.annimon.stream.Stream;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import lombok.Getter;


public class MemoryRepository {

    @Getter
    private final DataSource dataSource;
    private static volatile MemoryRepository instance;
    @Getter
    private ArrayList<Account> accountList;
    @Getter
    private double[] ratesForExchange;
    private SharedPref sharedPref = new SharedPref(App.getContext());


    private MemoryRepository() {
        dataSource = new DataSource(App.getContext());
        accountList = dataSource.getAllAccountsInfo();
        ratesForExchange = dataSource.getRates();
    }

    public void updateAccountList() {
        accountList = MemoryRepository.getInstance().getDataSource().getAllAccountsInfo();
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

    public int getAccountsCount() {
        return accountList.size();
    }

    public void updateRatesForExchange() {
        if (InternetTester.isConnectionEnabled(App.getContext())
                && (!sharedPref.getRatesInsertFirstTimeStatus()
                || !UpdateRatesUtils.checkForTodayUpdate()
                && UpdateRatesUtils.checkForAvailableNewRates())) {
            new RatesManager().getRates();
        }
    }

    public void setRatesForExchange() {
        System.arraycopy(dataSource.getRates(), 0, ratesForExchange, 0, ratesForExchange.length);
        EventBus.getDefault().post(new UpdateFrgHomeNewRates());
    }

    public static MemoryRepository getInstance() {
        MemoryRepository localInstance = instance;
        if (localInstance == null) {
            synchronized (MemoryRepository.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MemoryRepository();
                }
            }
        }
        return localInstance;
    }
}