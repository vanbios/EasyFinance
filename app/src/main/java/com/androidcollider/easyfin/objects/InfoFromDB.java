package com.androidcollider.easyfin.objects;


import com.androidcollider.easyfin.AppController;
import com.androidcollider.easyfin.database.DataSource;

import java.util.ArrayList;



public class InfoFromDB {

    private final DataSource dataSource = new DataSource(AppController.getContext());

    private static volatile InfoFromDB instance;

    private ArrayList<Account> accountList;


    private InfoFromDB() {
        accountList = dataSource.getAllAccountsInfo();
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
