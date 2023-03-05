package com.androidcollider.easyfin.common.repository.database

import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager
import com.androidcollider.easyfin.common.models.*
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Single
import java.util.*

/**
 * @author Ihor Bilous
 */
class DatabaseRepository(
    private val dbHelper: DbHelper,
    private val sharedPrefManager: SharedPrefManager,
    private val numberFormatManager: NumberFormatManager,
    private val resourcesManager: ResourcesManager
) : Repository {

    private lateinit var db: SQLiteDatabase

    override fun addNewAccount(account: Account): Single<Account> {
        return Single.fromCallable { insertNewAccount(account) }
    }

    override val allAccounts: Single<List<Account>>
        get() = Single.fromCallable { allAccountsInfo }

    override fun updateAccount(account: Account): Single<Account> {
        return Single.fromCallable { editAccount(account) }
    }

    override fun deleteAccount(id: Int): Single<Boolean> {
        return Single.fromCallable { deleteAccountDB(id) }
    }

    override fun transferBTWAccounts(
        idAccount1: Int,
        accountAmount1: Double,
        idAccount2: Int,
        accountAmount2: Double
    ): Single<Boolean> {
        return Single.fromCallable {
            updateAccountsAmountAfterTransfer(
                idAccount1,
                accountAmount1,
                idAccount2,
                accountAmount2
            )
        }
    }

    override fun addNewTransaction(transaction: Transaction): Single<Transaction> {
        return Single.fromCallable { insertNewTransaction(transaction) }
    }

    override val allTransactions: Single<List<Transaction>>
        get() = Single.fromCallable { allTransactionsInfo }

    override fun updateTransaction(transaction: Transaction): Single<Transaction> {
        return Single.fromCallable { editTransaction(transaction) }
    }

    override fun updateTransactionDifferentAccounts(
        transaction: Transaction,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Single<Boolean> {
        return Single.fromCallable {
            editTransactionDifferentAccounts(
                transaction,
                oldAccountAmount,
                oldAccountId
            )
        }
    }

    override fun deleteTransaction(
        idAccount: Int,
        idTransaction: Int,
        amount: Double
    ): Single<Boolean> {
        return Single.fromCallable { deleteTransactionDB(idAccount, idTransaction, amount) }
    }

    override fun addNewDebt(debt: Debt): Single<Debt> {
        return Single.fromCallable { insertNewDebt(debt) }
    }

    override val allDebts: Single<List<Debt>>
        get() = Single.fromCallable { allDebtInfo }

    override fun updateDebt(debt: Debt): Single<Debt> {
        return Single.fromCallable { editDebt(debt) }
    }

    override fun updateDebtDifferentAccounts(
        debt: Debt,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Single<Boolean> {
        return Single.fromCallable {
            editDebtDifferentAccounts(
                debt,
                oldAccountAmount,
                oldAccountId
            )
        }
    }

    override fun deleteDebt(
        idAccount: Int,
        idDebt: Int,
        amount: Double,
        type: Int
    ): Single<Boolean> {
        return Single.fromCallable { deleteDebtDB(idAccount, idDebt, amount, type) }
    }

    override fun payFullDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int
    ): Single<Boolean> {
        return Single.fromCallable { payAllDebt(idAccount, accountAmount, idDebt) }
    }

    override fun payPartOfDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double
    ): Single<Boolean> {
        return Single.fromCallable { payPartDebt(idAccount, accountAmount, idDebt, debtAmount) }
    }

    override fun takeMoreDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double,
        debtAllAmount: Double
    ): Single<Boolean> {
        return Single.fromCallable {
            takeMoreDebtDB(
                idAccount,
                accountAmount,
                idDebt,
                debtAmount,
                debtAllAmount
            )
        }
    }

    override fun getTransactionsStatistic(position: Int): Single<Map<String, DoubleArray>> {
        return Single.fromCallable { getTransactionsStatisticDB(position) }
    }

    override val accountsAmountSumGroupByTypeAndCurrency: Single<Map<String, DoubleArray>>
        get() = Single.fromCallable { accountsSumGroupByTypeAndCurrency }

    override fun updateRates(ratesList: List<Rates>): Single<Boolean> {
        return Single.fromCallable { insertRates(ratesList) }
    }

    override val rates: Single<DoubleArray>
        get() = Single.fromCallable { ratesDB }

    override fun setAllAccounts(accountList: List<Account>): Single<Boolean> {
        throw IllegalStateException("do not perform this action!")
    }

    override fun setAllTransactions(transactionList: List<Transaction>): Single<Boolean> {
        throw IllegalStateException("do not perform this action!")
    }

    override fun setAllDebts(debtList: List<Debt>): Single<Boolean> {
        throw IllegalStateException("do not perform this action!")
    }

    override fun setRates(rates: DoubleArray): Single<Boolean> {
        throw IllegalStateException("do not perform this action!")
    }

    override fun addNewTransactionIncomeCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory> {
        return Single.fromCallable { insertNewTransactionIncomeCategory(transactionCategory) }
    }

    override val allTransactionIncomeCategories: Single<List<TransactionCategory>>
        get() = Single.fromCallable { transactionIncomeCategories }

    override fun updateTransactionIncomeCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory> {
        return Single.fromCallable { editTransactionIncomeCategory(transactionCategory) }
    }

    override fun deleteTransactionIncomeCategory(id: Int): Single<Boolean> {
        return Single.fromCallable { makeTransactionCategoryIncomeInvisible(id) }
    }

    override fun setAllTransactionIncomeCategories(
        transactionCategoryList: List<TransactionCategory>
    ): Single<Boolean> {
        throw IllegalStateException("do not perform this action!")
    }

    override fun addNewTransactionExpenseCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory> {
        return Single.fromCallable { insertNewTransactionExpenseCategory(transactionCategory) }
    }

    override val allTransactionExpenseCategories: Single<List<TransactionCategory>>
        get() = Single.fromCallable { transactionExpenseCategories }

    override fun updateTransactionExpenseCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory> {
        return Single.fromCallable { editTransactionExpenseCategory(transactionCategory) }
    }

    override fun deleteTransactionExpenseCategory(id: Int): Single<Boolean> {
        return Single.fromCallable { makeTransactionCategoryExpenseInvisible(id) }
    }

    override fun setAllTransactionExpenseCategories(
        transactionCategoryList: List<TransactionCategory>
    ): Single<Boolean> {
        throw IllegalStateException("do not perform this action!")
    }

    //Open database to write
    @Throws(SQLException::class)
    private fun openLocalToWrite() {
        db = dbHelper.writableDatabase
    }

    //Open database to read
    @Throws(SQLException::class)
    private fun openLocalToRead() {
        db = dbHelper.readableDatabase
    }

    //Close database
    private fun closeLocal() {
        db.close()
    }

    private fun insertNewAccount(account: Account): Account {
        val cv = ContentValues()
        cv.put("name", account.name)
        cv.put("amount", account.amount)
        cv.put("type", account.type)
        cv.put("currency", account.currency)
        openLocalToWrite()
        val id = insertAccountQuery(cv).toInt()
        closeLocal()
        if (id > 0) {
            account.id = id
        }
        return account
    }

    private fun insertNewTransaction(transaction: Transaction): Transaction {
        val cv1 = ContentValues()
        val cv2 = ContentValues()
        val idAccount = transaction.idAccount
        cv1.put("id_account", idAccount)
        cv1.put("date", transaction.date)
        cv1.put("amount", transaction.amount)
        cv1.put("category", transaction.category)
        cv2.put("amount", transaction.accountAmount)
        openLocalToWrite()
        val transId = insertTransactionQuery(cv1).toInt()
        updateAccountQuery(cv2, idAccount)
        closeLocal()
        if (transId > 0) {
            transaction.id = transId
        }
        return transaction
    }

    private fun insertNewDebt(debt: Debt): Debt {
        val cv1 = ContentValues()
        val cv2 = ContentValues()
        val idAccount = debt.idAccount
        cv1.put("name", debt.name)
        cv1.put("amount_current", debt.amountCurrent)
        cv1.put("type", debt.type)
        cv1.put("id_account", debt.idAccount)
        cv1.put("deadline", debt.date)
        cv1.put("amount_all", debt.amountCurrent)
        cv2.put("amount", debt.accountAmount)
        openLocalToWrite()
        val debtId = insertDebtQuery(cv1).toInt()
        updateAccountQuery(cv2, idAccount)
        closeLocal()
        if (debtId > 0) {
            debt.id = debtId
        }
        return debt
    }

    private fun updateAccountsAmountAfterTransfer(
        id_account_1: Int, amount_1: Double,
        id_account_2: Int, amount_2: Double
    ): Boolean {
        val cv1 = ContentValues()
        val cv2 = ContentValues()
        cv1.put("amount", amount_1)
        cv2.put("amount", amount_2)
        openLocalToWrite()
        val res1 = updateAccountQuery(cv1, id_account_1)
        val res2 = updateAccountQuery(cv2, id_account_2)
        closeLocal()
        return res1 && res2
    }

    private fun getTransactionsStatisticDB(position: Int): Map<String, DoubleArray> {
        Log.d(TAG, "getTransactionsStatisticDB")
        var period: Long = 0
        when (position) {
            1 -> period = DateConstants.DAY
            2 -> period = DateConstants.WEEK
            3 -> period = DateConstants.MONTH
            4 -> period = DateConstants.YEAR
            5 -> period = Long.MAX_VALUE
        }
        val currencyArray =
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
        val result: MutableMap<String, DoubleArray> = HashMap()
        var cursor: Cursor
        var selectQuery: String
        openLocalToRead()
        for (currency in currencyArray) {
            val arrStat = DoubleArray(2)
            selectQuery = ("SELECT t.date, t.amount FROM Transactions t, Account a "
                    + "WHERE t.id_account = a.id_account "
                    + "AND a.currency = '" + currency + "' ")
            cursor = db.rawQuery(selectQuery, null)
            var cost = 0.0
            var income = 0.0
            if (cursor.moveToFirst()) {
                val amountColIndex = cursor.getColumnIndex("amount")
                val dateColIndex = cursor.getColumnIndex("date")
                val currentTime = Date().time
                for (i in cursor.count - 1 downTo 0) {
                    cursor.moveToPosition(i)
                    val date = cursor.getLong(dateColIndex)
                    val amount = cursor.getDouble(amountColIndex)
                    if (currentTime > date && period >= currentTime - date) {
                        if (numberFormatManager.isDoubleNegative(amount)) {
                            cost += amount
                        } else {
                            income += amount
                        }
                    }
                }
            }
            cursor.close()
            arrStat[0] = cost
            arrStat[1] = income
            result[currency] = arrStat
        }
        closeLocal()
        return result
    }

    private val accountsSumGroupByTypeAndCurrency: Map<String, DoubleArray>
        get() {
            Log.d(TAG, "getAccountsSumGroupByTypeAndCurrency")
            val currencyArray =
                resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
            val results: MutableMap<String, DoubleArray> = HashMap()
            var cursor: Cursor
            var selectQuery: String
            openLocalToRead()
            for (currency in currencyArray) {
                val result = DoubleArray(4)
                for (i in 0..2) {
                    selectQuery = ("SELECT SUM(amount) FROM Account "
                            + "WHERE visibility = 1 AND "
                            + "type = '" + i + "' AND "
                            + "currency = '" + currency + "' ")
                    cursor = db.rawQuery(selectQuery, null)
                    if (cursor.moveToFirst()) {
                        result[i] = cursor.getDouble(0)
                    }
                    cursor.close()
                }
                selectQuery = ("SELECT d.amount_current, d.type FROM Debt d, Account a "
                        + "WHERE d.id_account = a.id_account AND "
                        + "currency = '" + currency + "' ")
                cursor = db.rawQuery(selectQuery, null)
                var debtSum = 0.0
                var debtVal: Double
                var debtType: Int
                if (cursor.moveToFirst()) {
                    val amountColIndex = cursor.getColumnIndex("amount_current")
                    val typeColIndex = cursor.getColumnIndex("type")
                    do {
                        debtVal = cursor.getDouble(amountColIndex)
                        debtType = cursor.getInt(typeColIndex)
                        if (debtType == 1) {
                            debtVal *= -1.0
                        }
                        debtSum += debtVal
                    } while (cursor.moveToNext())
                    cursor.close()
                    result[3] = debtSum
                }
                results[currency] = result
            }
            closeLocal()
            return results
        }

    private val allAccountsInfo: List<Account>
        get() {
            Log.d(TAG, "getAllAccountsInfo")
            val accountArrayList: MutableList<Account> = ArrayList()
            val selectQuery = "SELECT * FROM Account WHERE visibility = 1"
            openLocalToRead()
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                val idColIndex = cursor.getColumnIndex("id_account")
                val nameColIndex = cursor.getColumnIndex("name")
                val amountColIndex = cursor.getColumnIndex("amount")
                val typeColIndex = cursor.getColumnIndex("type")
                val currencyColIndex = cursor.getColumnIndex("currency")
                do {
                    val account = Account()
                    account.id = cursor.getInt(idColIndex)
                    account.name = cursor.getString(nameColIndex)
                    account.amount = cursor.getDouble(amountColIndex)
                    account.type = cursor.getInt(typeColIndex)
                    account.currency = cursor.getString(currencyColIndex)
                    accountArrayList.add(account)
                } while (cursor.moveToNext())
            }
            cursor.close()
            closeLocal()
            return accountArrayList
        }

    private val allTransactionsInfo: List<Transaction>
        get() {
            Log.d(TAG, "getAllTransactionsInfo")
            val transactionArrayList: MutableList<Transaction> = ArrayList()
            val selectQuery =
                ("SELECT t.amount, date, category, name, type, a.currency, t.id_account, id_transaction "
                        + "FROM Transactions t, Account a "
                        + "WHERE t.id_account = a.id_account ")
            openLocalToRead()
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                val amountColIndex = cursor.getColumnIndex("amount")
                val dateColIndex = cursor.getColumnIndex("date")
                val categoryColIndex = cursor.getColumnIndex("category")
                val nameColIndex = cursor.getColumnIndex("name")
                val currencyColIndex = cursor.getColumnIndex("currency")
                val typeColIndex = cursor.getColumnIndex("type")
                val idAccountColIndex = cursor.getColumnIndex("id_account")
                val idTransColIndex = cursor.getColumnIndex("id_transaction")
                val cursorCount = cursor.count
                val limit = 0

                for (i in cursorCount - 1 downTo limit) {
                    cursor.moveToPosition(i)
                    val transaction = Transaction()
                    transaction.date = cursor.getLong(dateColIndex)
                    transaction.amount = cursor.getDouble(amountColIndex)
                    transaction.category = cursor.getInt(categoryColIndex)
                    transaction.accountName = cursor.getString(nameColIndex)
                    transaction.currency = cursor.getString(currencyColIndex)
                    transaction.accountType = cursor.getInt(typeColIndex)
                    transaction.accountType = cursor.getInt(typeColIndex)
                    transaction.idAccount = cursor.getInt(idAccountColIndex)
                    transaction.id = cursor.getInt(idTransColIndex)
                    transactionArrayList.add(transaction)
                }
            }
            cursor.close()
            closeLocal()
            return transactionArrayList
        }

    private val allDebtInfo: List<Debt>
        get() {
            Log.d(TAG, "getAllDebtInfo")
            val debtArrayList: MutableList<Debt> = ArrayList()
            val selectQuery = ("SELECT d.name AS d_name, d.amount_current, d.amount_all, "
                    + "d.type, deadline, a.name AS a_name, currency, d.id_account, id_debt "
                    + "FROM Debt d, Account a "
                    + "WHERE d.id_account = a.id_account ")
            openLocalToRead()
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                val dNameColIndex = cursor.getColumnIndex("d_name")
                val amountColIndex = cursor.getColumnIndex("amount_current")
                val amountAllColIndex = cursor.getColumnIndex("amount_all")
                val typeColIndex = cursor.getColumnIndex("type")
                val dateColIndex = cursor.getColumnIndex("deadline")
                val aNameColIndex = cursor.getColumnIndex("a_name")
                val curColIndex = cursor.getColumnIndex("currency")
                val idAccountColIndex = cursor.getColumnIndex("id_account")
                val idDebtColIndex = cursor.getColumnIndex("id_debt")
                do {
                    val debt = Debt()
                    debt.name = cursor.getString(dNameColIndex)
                    debt.amountCurrent = cursor.getDouble(amountColIndex)
                    debt.amountAll = cursor.getDouble(amountAllColIndex)
                    debt.type = cursor.getInt(typeColIndex)
                    debt.date = cursor.getLong(dateColIndex)
                    debt.accountName = cursor.getString(aNameColIndex)
                    debt.currency = cursor.getString(curColIndex)
                    debt.idAccount = cursor.getInt(idAccountColIndex)
                    debt.id = cursor.getInt(idDebtColIndex)
                    debtArrayList.add(debt)
                } while (cursor.moveToNext())
            }
            cursor.close()
            closeLocal()
            return debtArrayList
        }

    private fun editAccount(account: Account): Account {
        val cv = ContentValues()
        cv.put("name", account.name)
        cv.put("amount", account.amount)
        cv.put("type", account.type)
        cv.put("currency", account.currency)
        val id = account.id
        openLocalToWrite()
        updateAccountQuery(cv, id)
        closeLocal()
        return account
    }

    private fun deleteAccountDB(id: Int): Boolean {
        return if (checkAccountForTransactionOrDebtExist(id)) makeAccountInvisible(id)
        else deleteAccountFromDB(id)
    }

    private fun deleteAccountFromDB(id: Int): Boolean {
        openLocalToWrite()
        val res = deleteAccountQuery(id)
        closeLocal()
        return res
    }

    private fun makeAccountInvisible(id: Int): Boolean {
        val cv = ContentValues()
        cv.put("visibility", 0)
        openLocalToWrite()
        val res = updateAccountQuery(cv, id)
        closeLocal()
        return res
    }

    private fun checkAccountForTransactionOrDebtExist(id: Int): Boolean {
        var selectQuery = ("SELECT COUNT(id_transaction) FROM Transactions "
                + "WHERE id_account = '" + id + "' ")
        openLocalToRead()
        var cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            cursor.close()
            return true
        }
        cursor.close()
        selectQuery = ("SELECT COUNT(id_debt) FROM Debt "
                + "WHERE id_account = '" + id + "' ")
        cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            cursor.close()
            closeLocal()
            return true
        }
        cursor.close()
        closeLocal()
        return false
    }

    private fun deleteTransactionDB(id_account: Int, id_trans: Int, amount: Double): Boolean {
        val selectQuery = ("SELECT amount FROM Account "
                + "WHERE id_account = '" + id_account + "' ")
        openLocalToWrite()
        val cursor = db.rawQuery(selectQuery, null)
        var accountAmount = 0.0
        if (cursor.moveToFirst()) {
            accountAmount = cursor.getDouble(0)
        }
        cursor.close()
        accountAmount -= amount
        val cv = ContentValues()
        cv.put("amount", accountAmount)
        val res1 = updateAccountQuery(cv, id_account)
        val res2 = deleteTransactionQuery(id_trans)
        closeLocal()
        return res1 && res2
    }

    private fun deleteDebtDB(id_account: Int, id_debt: Int, amount: Double, type: Int): Boolean {
        val selectQuery = ("SELECT amount FROM Account "
                + "WHERE id_account = '" + id_account + "' ")
        openLocalToWrite()
        val cursor = db.rawQuery(selectQuery, null)
        var accountAmount = 0.0
        if (cursor.moveToFirst()) {
            accountAmount = cursor.getDouble(0)
        }
        cursor.close()
        if (type == 1) {
            accountAmount -= amount
        } else {
            accountAmount += amount
        }
        val cv = ContentValues()
        cv.put("amount", accountAmount)
        val res1 = updateAccountQuery(cv, id_account)
        val res2 = deleteDebtQuery(id_debt)
        closeLocal()
        return res1 && res2
    }

    private fun payAllDebt(idAccount: Int, accountAmount: Double, idDebt: Int): Boolean {
        val cv = ContentValues()
        cv.put("amount", accountAmount)
        openLocalToWrite()
        val res1 = updateAccountQuery(cv, idAccount)
        val res2 = deleteDebtQuery(idDebt)
        closeLocal()
        return res1 && res2
    }

    private fun payPartDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double
    ): Boolean {
        val cv1 = ContentValues()
        cv1.put("amount", accountAmount)
        val cv2 = ContentValues()
        cv2.put("amount_current", debtAmount)
        openLocalToWrite()
        val res1 = updateAccountQuery(cv1, idAccount)
        val res2 = updateDebtQuery(cv2, idDebt)
        closeLocal()
        return res1 && res2
    }

    private fun takeMoreDebtDB(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double,
        debtAllAmount: Double
    ): Boolean {
        val cv1 = ContentValues()
        cv1.put("amount", accountAmount)
        val cv2 = ContentValues()
        cv2.put("amount_current", debtAmount)
        cv2.put("amount_all", debtAllAmount)
        openLocalToWrite()
        val res1 = updateAccountQuery(cv1, idAccount)
        val res2 = updateDebtQuery(cv2, idDebt)
        closeLocal()
        return res1 && res2
    }

    private fun insertRates(ratesList: List<Rates>): Boolean {
        val cv = ContentValues()
        openLocalToWrite()
        var id: Int
        for ((id1, date, currency, rateType, bid, ask) in ratesList) {
            id = id1
            cv.put("date", date)
            cv.put("currency", currency)
            cv.put("rate_type", rateType)
            cv.put("bid", bid)
            cv.put("ask", ask)
            if (db.update("Rates", cv, "id_rate = '$id' ", null) == 0) {
                cv.put("id_rate", id)
                db.insert("Rates", null, cv)
            }
        }
        closeLocal()
        sharedPrefManager.ratesInsertFirstTimeStatus = true
        sharedPrefManager.setRatesUpdateTime()
        return true
    }

    private val ratesDB: DoubleArray
        get() {
            Log.d(TAG, "getRatesDB")
            val rateNamesArray = resourcesManager.getStringArray(ResourcesManager.STRING_JSON_RATES)
            val rateType = "bank"
            val results = DoubleArray(4)
            var cursor: Cursor
            var selectQuery: String
            openLocalToRead()
            for (i in rateNamesArray.indices) {
                val currency = rateNamesArray[i]
                selectQuery = ("SELECT ask FROM Rates "
                        + "WHERE rate_type = '" + rateType + "' "
                        + "AND currency = '" + currency + "' ")
                cursor = db.rawQuery(selectQuery, null)
                results[i] = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
                cursor.close()
            }
            closeLocal()
            return results
        }

    private fun editTransaction(transaction: Transaction): Transaction {
        val cv1 = ContentValues()
        val cv2 = ContentValues()
        val idAccount = transaction.idAccount
        val idTransaction = transaction.id
        cv1.put("id_account", idAccount)
        cv1.put("date", transaction.date)
        cv1.put("amount", transaction.amount)
        cv1.put("category", transaction.category)
        cv2.put("amount", transaction.accountAmount)
        openLocalToWrite()
        updateTransactionQuery(cv1, idTransaction)
        updateAccountQuery(cv2, idAccount)
        closeLocal()
        return transaction
    }

    private fun editTransactionDifferentAccounts(
        transaction: Transaction,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Boolean {
        val cv1 = ContentValues()
        val cv2 = ContentValues()
        val cv3 = ContentValues()
        val idAccount = transaction.idAccount
        val idTransaction = transaction.id
        cv1.put("id_account", idAccount)
        cv1.put("date", transaction.date)
        cv1.put("amount", transaction.amount)
        cv1.put("category", transaction.category)
        cv2.put("amount", transaction.accountAmount)
        cv3.put("amount", oldAccountAmount)
        openLocalToWrite()
        val res1 = updateTransactionQuery(cv1, idTransaction)
        val res2 = updateAccountQuery(cv2, idAccount)
        val res3 = updateAccountQuery(cv3, oldAccountId)
        closeLocal()
        return res1 && res2 && res3
    }

    private fun editDebt(debt: Debt): Debt {
        val cv1 = ContentValues()
        val cv2 = ContentValues()
        val idAccount = debt.idAccount
        val idDebt = debt.id
        cv1.put("name", debt.name)
        cv1.put("amount_current", debt.amountCurrent)
        cv1.put("type", debt.type)
        cv1.put("id_account", debt.idAccount)
        cv1.put("deadline", debt.date)
        cv1.put("amount_all", debt.amountCurrent)
        cv2.put("amount", debt.accountAmount)
        openLocalToWrite()
        updateDebtQuery(cv1, idDebt)
        updateAccountQuery(cv2, idAccount)
        closeLocal()
        return debt
    }

    private fun editDebtDifferentAccounts(
        debt: Debt,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Boolean {
        val cv1 = ContentValues()
        val cv2 = ContentValues()
        val cv3 = ContentValues()
        val idAccount = debt.idAccount
        val idDebt = debt.id
        cv1.put("name", debt.name)
        cv1.put("amount_current", debt.amountCurrent)
        cv1.put("type", debt.type)
        cv1.put("id_account", debt.idAccount)
        cv1.put("deadline", debt.date)
        cv1.put("amount_all", debt.amountCurrent)
        cv2.put("amount", debt.accountAmount)
        cv3.put("amount", oldAccountAmount)
        openLocalToWrite()
        val res1 = updateDebtQuery(cv1, idDebt)
        val res2 = updateAccountQuery(cv2, idAccount)
        val res3 = updateAccountQuery(cv3, oldAccountId)
        closeLocal()
        return res1 && res2 && res3
    }

    private fun insertAccountQuery(cv: ContentValues): Long {
        return db.insert("Account", null, cv)
    }

    private fun insertTransactionQuery(cv: ContentValues): Long {
        return db.insert("Transactions", null, cv)
    }

    private fun insertDebtQuery(cv: ContentValues): Long {
        return db.insert("Debt", null, cv)
    }

    private fun insertTransactionCategoryQuery(cv: ContentValues, isExpense: Boolean): Long {
        return db.insert(
            if (isExpense) "Transactions_Category_Expense" else "Transactions_Category_Income",
            null, cv
        )
    }

    private fun updateAccountQuery(cv: ContentValues, id: Int): Boolean {
        return db.update("Account", cv, "id_account = $id", null) > 0
    }

    private fun updateTransactionQuery(cv: ContentValues, id: Int): Boolean {
        return db.update("Transactions", cv, "id_transaction = $id", null) > 0
    }

    private fun updateDebtQuery(cv: ContentValues, id: Int): Boolean {
        return db.update("Debt", cv, "id_debt = $id", null) > 0
    }

    private fun updateTransactionCategoryQuery(
        cv: ContentValues,
        id: Int,
        isExpense: Boolean
    ): Boolean {
        return db.update(
            if (isExpense) "Transactions_Category_Expense" else "Transactions_Category_Income",
            cv, "id_category = $id", null
        ) > 0
    }

    private fun deleteAccountQuery(idAccount: Int): Boolean {
        return db.delete("Account", "id_account = '$idAccount' ", null) > 0
    }

    private fun deleteTransactionQuery(idTrans: Int): Boolean {
        return db.delete("Transactions", "id_transaction = $idTrans", null) > 0
    }

    private fun deleteDebtQuery(idDebt: Int): Boolean {
        return db.delete("Debt", "id_debt = $idDebt", null) > 0
    }

    private fun getTransactionCategoriesDB(isExpense: Boolean): List<TransactionCategory> {
        val categoryList: MutableList<TransactionCategory> = ArrayList()
        val initCategoriesArray = resourcesManager.getStringArray(
            if (isExpense) ResourcesManager.STRING_TRANSACTION_CATEGORY_EXPENSE else ResourcesManager.STRING_TRANSACTION_CATEGORY_INCOME
        )
        for (i in initCategoriesArray.indices) {
            categoryList.add(TransactionCategory(i, initCategoriesArray[i], 1))
        }
        val selectQuery =
            if (isExpense) "SELECT * FROM Transactions_Category_Expense " else "SELECT * FROM Transactions_Category_Income "
        openLocalToRead()
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            val idColIndex = cursor.getColumnIndex("id_category")
            val nameColIndex = cursor.getColumnIndex("name")
            val visibilityColIndex = cursor.getColumnIndex("visibility")
            do {
                val id = cursor.getInt(idColIndex)
                val name = cursor.getString(nameColIndex)
                val visibility = cursor.getInt(visibilityColIndex)
                categoryList.add(TransactionCategory(id, name, visibility))
            } while (cursor.moveToNext())
        }
        cursor.close()
        closeLocal()
        return categoryList
    }

    private val transactionIncomeCategories: List<TransactionCategory>
        get() = getTransactionCategoriesDB(false)
    private val transactionExpenseCategories: List<TransactionCategory>
        get() = getTransactionCategoriesDB(true)

    private fun makeTransactionCategoryInvisible(id: Int, isExpense: Boolean): Boolean {
        val cv = ContentValues()
        cv.put("visibility", 0)
        openLocalToWrite()
        val res = updateTransactionCategoryQuery(cv, id, isExpense)
        closeLocal()
        return res
    }

    private fun makeTransactionCategoryIncomeInvisible(id: Int): Boolean {
        return makeTransactionCategoryInvisible(id, false)
    }

    private fun makeTransactionCategoryExpenseInvisible(id: Int): Boolean {
        return makeTransactionCategoryInvisible(id, true)
    }

    private fun editTransactionCategory(
        transactionCategory: TransactionCategory,
        isExpense: Boolean
    ): TransactionCategory {
        val cv = ContentValues()
        cv.put("name", transactionCategory.name)
        val id = transactionCategory.id
        openLocalToWrite()
        updateTransactionCategoryQuery(cv, id, isExpense)
        closeLocal()
        return transactionCategory
    }

    private fun editTransactionIncomeCategory(transactionCategory: TransactionCategory): TransactionCategory {
        return editTransactionCategory(transactionCategory, false)
    }

    private fun editTransactionExpenseCategory(transactionCategory: TransactionCategory): TransactionCategory {
        return editTransactionCategory(transactionCategory, true)
    }

    private fun insertNewTransactionCategory(
        transactionCategory: TransactionCategory,
        isExpense: Boolean
    ): TransactionCategory {
        val cv = ContentValues()
        cv.put("name", transactionCategory.name)
        cv.put("id_category", transactionCategory.id)
        openLocalToWrite()
        insertTransactionCategoryQuery(cv, isExpense)
        closeLocal()
        return transactionCategory
    }

    private fun insertNewTransactionIncomeCategory(transactionCategory: TransactionCategory): TransactionCategory {
        return insertNewTransactionCategory(transactionCategory, false)
    }

    private fun insertNewTransactionExpenseCategory(transactionCategory: TransactionCategory): TransactionCategory {
        return insertNewTransactionCategory(transactionCategory, true)
    }

    companion object {
        private val TAG = DatabaseRepository::class.java.simpleName
    }
}