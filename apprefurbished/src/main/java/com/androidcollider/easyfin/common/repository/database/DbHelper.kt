package com.androidcollider.easyfin.common.repository.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * @author Ihor Bilous
 */
class DbHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SqlQueries.create_account_table)
        db.execSQL(SqlQueries.create_transactions_table)
        db.execSQL(SqlQueries.create_debt_table)
        db.execSQL(SqlQueries.create_rates_table)
        db.execSQL(SqlQueries.create_transactions_category_expense_table)
        db.execSQL(SqlQueries.create_transactions_category_income_table)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> {
                db.execSQL(SqlQueries.create_transactions_category_expense_table)
                db.execSQL(SqlQueries.create_transactions_category_income_table)
            }
        }
    }

    companion object {
        const val DATABASE_NAME = "FinU.db"
        private const val DATABASE_VERSION = 2
    }
}