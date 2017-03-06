package com.androidcollider.easyfin.common.repository.database;

/**
 * @author Ihor Bilous
 */

class SqlQueries {

        //make a string SQL request for Account table
        static final String create_account_table = "CREATE TABLE Account (" +
                "id_account       INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name             TEXT NOT NULL," +
                "amount           REAL NOT NULL," +
                "type             INTEGER NOT NULL," +
                "currency         TEXT NOT NULL," +
                "visibility       INTEGER DEFAULT 1" +
                ");";

        //make a string SQL request for Transactions table
        static final String create_transactions_table = "CREATE TABLE Transactions (" +
                "id_transaction   INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_account       INTEGER NOT NULL," +
                "amount           REAL NOT NULL," +
                "category         INTEGER NOT NULL," +
                "date             INTEGER NOT NULL" +
                ");";

        //make a string SQL request for Debt table
        static final String create_debt_table = "CREATE TABLE Debt (" +
                "id_debt          INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name             TEXT NOT NULL," +
                "amount_current   REAL NOT NULL," +
                "amount_all       REAL NOT NULL," +
                "type             INTEGER NOT NULL," +
                "id_account       INTEGER NOT NULL," +
                "deadline         INTEGER NOT NULL" +
                ");";

        //make a string SQL request for Rates table
        static final String create_rates_table = "CREATE TABLE Rates (" +
                "id_rate          INTEGER PRIMARY KEY," +
                "date             INTEGER NOT NULL," +
                "currency         TEXT NOT NULL," +
                "rate_type        TEXT NOT NULL," +
                "bid              REAL NOT NULL," +
                "ask              REAL NOT NULL" +
                ");";

        //make a string SQL request for Transactions Category Expense table
        static final String create_transactions_category_expense_table = "CREATE TABLE Transactions_Category_Expense (" +
                "id_category      INTEGER PRIMARY KEY," +
                "name             TEXT NOT NULL," +
                "visibility       INTEGER DEFAULT 1" +
                ");";

        //make a string SQL request for Transactions Category Income table
        static final String create_transactions_category_income_table = "CREATE TABLE Transactions_Category_Income (" +
                "id_category      INTEGER PRIMARY KEY," +
                "name             TEXT NOT NULL," +
                "visibility       INTEGER DEFAULT 1" +
                ");";
}