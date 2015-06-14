package com.androidcollider.easyfin.database;


public class SqlQueries {

        //make a string SQL request for Account table
        public static final String create_account_table = "CREATE TABLE Account (" +
                "id_account       INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name             TEXT NOT NULL," +
                "amount           REAL NOT NULL," +
                "type             TEXT NOT NULL," +
                "currency         TEXT NOT NULL," +
                "visibility       INTEGER DEFAULT 1" +
                ");";

        //make a string SQL request for Transactions table
        public static final String create_transactions_table = "CREATE TABLE Transactions (" +
                "id_transaction   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_account       INTEGER NOT NULL," +
                "amount           REAL NOT NULL," +
                "currency         TEXT NOT NULL," +
                "category         TEXT NOT NULL," +
                "date             INTEGER NOT NULL" +
                ");";
}
