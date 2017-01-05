package com.androidcollider.easyfin.repository.memory;


import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.repository.database.DataSource;
import com.annimon.stream.Stream;

import java.util.ArrayList;

import lombok.Getter;


public class InMemoryRepository {

    @Getter
    private final DataSource dataSource;
    private static volatile InMemoryRepository instance;
    private ArrayList<Account> accountList;
    /*@Getter
    private double[] ratesForExchange;*/
    //private SharedPref sharedPref = new SharedPref(App.getContext());


    private InMemoryRepository() {
        dataSource = new DataSource(App.getContext());
        accountList = dataSource.getAllAccountsInfo();
        //ratesForExchange = dataSource.getRates();
    }

    public void updateAccountList() {
        accountList = InMemoryRepository.getInstance().getDataSource().getAllAccountsInfo();
    }

    /*public ArrayList<Account> getAccountList() {
        return accountList;
    }*/

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

    /*public void updateRatesForExchange() {
        if (InternetTester.isConnectionEnabled(App.getContext())
                && (!sharedPref.getRatesInsertFirstTimeStatus()
                || !UpdateRatesUtils.checkForTodayUpdate()
                && UpdateRatesUtils.checkForAvailableNewRates())) {
            new RatesLoaderManager().getRates();
        }
    }*/

    /*public void setRatesForExchange() {
        System.arraycopy(dataSource.getRates(), 0, ratesForExchange, 0, ratesForExchange.length);
        EventBus.getDefault().post(new UpdateFrgHomeNewRates());
    }*/

    public static InMemoryRepository getInstance() {
        InMemoryRepository localInstance = instance;
        if (localInstance == null) {
            synchronized (InMemoryRepository.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new InMemoryRepository();
                }
            }
        }
        return localInstance;
    }
}