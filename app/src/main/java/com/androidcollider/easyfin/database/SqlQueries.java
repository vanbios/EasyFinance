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
        //make a string SQL request for Transaction table
        public static final String create_transactions_table = "CREATE TABLE Transactions (" +
                "id_transaction   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date             TEXT NOT NULL," +
                "id_account       INTEGER NOT NULL," +
                "amount           REAL NOT NULL," +
                "category         TEXT NOT NULL" +
                ");";
        //make a string SQL request for Category table
    /*public static final String create_category_table = "CREATE TABLE Comment (" +
            "id_category       INTEGER PRIMARY KEY NOT NULL," +
            "name              TEXT NOT NULL" +
            ");";*/
}
