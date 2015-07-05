package com.androidcollider.easyfin.objects;


import com.androidcollider.easyfin.MainActivity;
import java.util.ArrayList;



public class InfoFromDB {

    private static volatile InfoFromDB instance;

    private ArrayList<Account> accountList;


    private InfoFromDB() {
        accountList = MainActivity.dataSource.getAllAccountsInfo();
    }

    public void updateAccountList() {
        accountList = MainActivity.dataSource.getAllAccountsInfo();
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
