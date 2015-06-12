package com.androidcollider.easyfin.database;


public class SqlQueries {

        //make a string SQL request for Account table
        public static final String create_account_table = "CREATE TABLE Account (" +
                "id_account       INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name             TEXT NOT NULL," +
                "amount           REAL NOT NULL," +
                "type             TEXT NOT NULL," +
                "currency         TEXT NOT NULL" +
                ");";

        //make a string SQL request for Transactions table
        public static final String create_transactions_table = "CREATE TABLE Transactions (" +
                "id_transaction   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date             INTEGER NOT NULL," +
                "id_account       INTEGER NOT NULL," +
                "account_name     TEXT NOT NULL," +
                "currency         TEXT NOT NULL," +
                "type             TEXT NOT NULL," +
                "amount           REAL NOT NULL," +
                "category         TEXT NOT NULL" +
                ");";

        //make a string SQL request for TransBTWAccounts table
        public static final String create_trans_btw_accounts_table = "CREATE TABLE TransBTWAccounts (" +
                "id_transaction   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name1            TEXT NOT NULL," +
                "name2            TEXT NOT NULL," +
                "amount           REAL NOT NULL," +
                "currency         TEXT NOT NULL," +
                "date             INTEGER NOT NULL," +
                "type1            TEXT NOT NULL," +
                "type2            TEXT NOT NULL" +
                ");";
}
