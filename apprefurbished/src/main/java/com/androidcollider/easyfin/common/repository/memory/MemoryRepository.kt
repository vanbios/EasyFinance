package com.androidcollider.easyfin.common.repository.memory

import android.util.Log
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.models.*
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
class MemoryRepository(
    private val numberFormatManager: NumberFormatManager,
    resourcesManager: ResourcesManager
) : Repository {
    private var accountList: MutableList<Account>? = null
    private var transactionList: MutableList<Transaction>? = null
    private var debtList: MutableList<Debt>? = null
    private var ratesArray: DoubleArray? = null
    private val currencyArray: Array<String>
    private var transactionCategoryIncomeList: MutableList<TransactionCategory>? = null
    private var transactionCategoryExpenseList: MutableList<TransactionCategory>? = null

    init {
        currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
    }

    override fun addNewAccount(account: Account): Single<Account> {
        return Single.fromCallable {
            accountList?.add(account)
            account
        }
    }

    override val allAccounts: Single<List<Account>>?
        get() {
            Log.d(TAG, "getAllAccounts")
            accountList?.let { return Single.just(it) }
            return null
        }

    override fun updateAccount(account: Account): Single<Account> {
        return Single.fromCallable {
            accountList?.let {
                val pos = it.indexOf(account)
                if (pos >= 0) it[pos] = account
            }
            account
        }
    }

    override fun deleteAccount(id: Int): Single<Boolean> {
        return Single.fromCallable {
            val pos = getAccountPosById(id)
            val b = pos != -1
            if (b) accountList?.removeAt(pos)
            b
        }
    }

    override fun transferBTWAccounts(
        idAccount1: Int,
        accountAmount1: Double,
        idAccount2: Int,
        accountAmount2: Double
    ): Single<Boolean> {
        return Single.fromCallable {
            var pos = getAccountPosById(idAccount1)
            val b1 = pos != -1
            if (b1) {
                accountList!![pos].amount = accountAmount1
            }
            pos = getAccountPosById(idAccount2)
            val b2 = pos != -1
            if (b2) {
                accountList!![pos].amount = accountAmount2
            }
            b1 && b2
        }
    }

    override fun addNewTransaction(transaction: Transaction): Single<Transaction> {
        return Single.fromCallable {
            transactionList?.add(0, transaction)
            val pos = getAccountPosById(transaction.idAccount)
            if (pos != -1) {
                accountList!![pos].amount = transaction.accountAmount
            }
            transaction
        }
    }

    override val allTransactions: Single<List<Transaction>>?
        get() {
            Log.d(TAG, "getAllTransactions")
            transactionList?.let { return Single.just(it) }
            return null
        }

    override fun updateTransaction(transaction: Transaction): Single<Transaction> {
        return Single.fromCallable {
            var pos = getAccountPosById(transaction.idAccount)
            if (pos != -1) {
                accountList!![pos].amount = transaction.accountAmount
            }
            pos = transactionList!!.indexOf(transaction)
            if (pos >= 0) transactionList!![pos] = transaction
            transaction
        }
    }

    override fun updateTransactionDifferentAccounts(
        transaction: Transaction,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Single<Boolean> {
        return Single.fromCallable {
            var pos = getAccountPosById(transaction.idAccount)
            val b1 = pos != -1
            if (b1) {
                accountList!![pos].amount = transaction.accountAmount
            }
            pos = getAccountPosById(oldAccountId)
            val b2 = pos != -1
            if (b2) {
                accountList!![pos].amount = oldAccountAmount
            }
            pos = transactionList!!.indexOf(transaction)
            val b3 = pos != -1
            if (b3) transactionList!![pos] = transaction
            b1 && b2 && b3
        }
    }

    override fun deleteTransaction(
        idAccount: Int,
        idTransaction: Int,
        amount: Double
    ): Single<Boolean> {
        return Single.fromCallable {
            var pos = getTransactionPosById(idTransaction)
            var b = pos != -1
            if (b) {
                transactionList!!.removeAt(pos)
                pos = getAccountPosById(idAccount)
                b = pos != -1
                if (b) {
                    accountList!![pos].amount = accountList!![pos].amount - amount
                }
            }
            b
        }
    }

    override fun addNewDebt(debt: Debt): Single<Debt> {
        return Single.fromCallable {
            debtList!!.add(debt)
            val pos = getAccountPosById(debt.idAccount)
            if (pos != -1) {
                accountList!![pos].amount = debt.accountAmount
            }
            debt
        }
    }

    override val allDebts: Single<List<Debt>>?
        get() {
            Log.d(TAG, "getAllDebts")
            debtList?.let { return Single.just(it) }
            return null
        }

    override fun updateDebt(debt: Debt): Single<Debt> {
        return Single.fromCallable {
            var pos = getAccountPosById(debt.idAccount)
            if (pos != -1) {
                accountList!![pos].amount = debt.accountAmount
            }
            pos = debtList!!.indexOf(debt)
            if (pos >= 0) debtList!![pos] = debt
            debt
        }
    }

    override fun updateDebtDifferentAccounts(
        debt: Debt,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Single<Boolean> {
        return Single.fromCallable {
            var pos = getAccountPosById(debt.idAccount)
            val b1 = pos != -1
            if (b1) {
                accountList!![pos].amount = debt.accountAmount
            }
            pos = getAccountPosById(oldAccountId)
            val b2 = pos != -1
            if (b2) {
                accountList!![pos].amount = oldAccountAmount
            }
            pos = debtList!!.indexOf(debt)
            val b3 = pos != -1
            if (b3) debtList!![pos] = debt
            b1 && b2 && b3
        }
    }

    override fun deleteDebt(
        idAccount: Int,
        idDebt: Int,
        amount: Double,
        type: Int
    ): Single<Boolean> {
        return Single.fromCallable {
            var pos = getDebtPosById(idDebt)
            var b = pos != -1
            if (b) {
                debtList!!.removeAt(pos)
                pos = getAccountPosById(idAccount)
                b = pos != -1
                if (b) {
                    accountList!![pos].amount =
                        if (type == 1) accountList!![pos].amount - amount else accountList!![pos].amount + amount
                }
            }
            b
        }
    }

    override fun payFullDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int
    ): Single<Boolean> {
        return Single.fromCallable {
            var pos = getDebtPosById(idDebt)
            var b = pos != -1
            if (b) {
                debtList!!.removeAt(pos)
                pos = getAccountPosById(idAccount)
                b = pos != -1
                if (b) {
                    accountList!![pos].amount = accountAmount
                }
            }
            b
        }
    }

    override fun payPartOfDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double
    ): Single<Boolean> {
        return Single.fromCallable {
            var pos = getDebtPosById(idDebt)
            var b = pos != -1
            if (b) {
                debtList!![pos].amountCurrent = debtAmount
                pos = getAccountPosById(idAccount)
                b = pos != -1
                if (b) {
                    accountList!![pos].amount = accountAmount
                }
            }
            b
        }
    }

    override fun takeMoreDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double,
        debtAllAmount: Double
    ): Single<Boolean> {
        return Single.fromCallable {
            var pos = getDebtPosById(idDebt)
            var b = pos != -1
            if (b) {
                debtList!![pos].amountCurrent = debtAmount
                debtList!![pos].amountAll = debtAllAmount
                pos = getAccountPosById(idAccount)
                b = pos != -1
                if (b) {
                    accountList!![pos].amount = accountAmount
                }
            }
            b
        }
    }

    override fun getTransactionsStatistic(position: Int): Single<Map<String, DoubleArray>>? {
        return if (transactionList == null) null
        else Single.fromCallable { getTransactionsStatisticByPosition(position) }
    }

    override val accountsAmountSumGroupByTypeAndCurrency: Single<Map<String, DoubleArray>>?
        get() = if (accountList == null || debtList == null) null
        else Single.fromCallable { accountsSumGroupByTypeAndCurrency }

    override fun updateRates(ratesList: List<Rates>): Single<Boolean> {
        return Single.fromCallable {
            for (i in ratesArray!!.indices) {
                ratesArray!![i] = ratesList[i].ask
            }
            true
        }
    }

    override val rates: Single<DoubleArray>?
        get() {
            Log.d(TAG, "getRates")
            ratesArray?.let { return Single.just(it) }
            return null
        }

    override fun setAllAccounts(accountList: List<Account>): Single<Boolean> {
        return Single.fromCallable {
            this.accountList = ArrayList()
            this.accountList?.addAll(accountList)
            true
        }
    }

    override fun setAllTransactions(transactionList: List<Transaction>): Single<Boolean> {
        return Single.fromCallable {
            this.transactionList = ArrayList()
            this.transactionList?.addAll(transactionList)
            true
        }
    }

    override fun setAllDebts(debtList: List<Debt>): Single<Boolean> {
        return Single.fromCallable {
            this.debtList = ArrayList()
            this.debtList?.addAll(debtList)
            true
        }
    }

    override fun setRates(rates: DoubleArray): Single<Boolean> {
        return Single.fromCallable {
            ratesArray = DoubleArray(4)
            System.arraycopy(rates, 0, ratesArray!!, 0, rates.size)
            true
        }
    }

    override fun addNewTransactionIncomeCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory> {
        return Single.fromCallable {
            transactionCategoryIncomeList?.add(transactionCategory)
            transactionCategory
        }
    }

    override val allTransactionIncomeCategories: Single<List<TransactionCategory>>?
        get() = if (transactionCategoryIncomeList == null) null
        else Single.just(transactionCategoryIncomeList!!)

    override fun updateTransactionIncomeCategory(
        transactionCategory: TransactionCategory
    ): Single<TransactionCategory> {
        return Single.fromCallable {
            val pos = transactionCategoryIncomeList!!.indexOf(transactionCategory)
            if (pos >= 0) transactionCategoryIncomeList!![pos] = transactionCategory
            transactionCategory
        }
    }

    override fun deleteTransactionIncomeCategory(id: Int): Single<Boolean> {
        return Single.fromCallable {
            val pos = getTransactionCategoryIncomePosById(id)
            val b = pos != -1
            if (b) transactionCategoryIncomeList!!.removeAt(pos)
            b
        }
    }

    override fun setAllTransactionIncomeCategories(
        transactionCategoryList: List<TransactionCategory>
    ): Single<Boolean> {
        return Single.fromCallable {
            transactionCategoryIncomeList = ArrayList()
            transactionCategoryIncomeList!!.addAll(transactionCategoryList)
            true
        }
    }

    override fun addNewTransactionExpenseCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory> {
        return Single.fromCallable {
            transactionCategoryExpenseList?.add(transactionCategory)
            transactionCategory
        }
    }

    override val allTransactionExpenseCategories: Single<List<TransactionCategory>>?
        get() = if (transactionCategoryExpenseList == null) null
        else Single.just(transactionCategoryExpenseList!!)

    override fun updateTransactionExpenseCategory(
        transactionCategory: TransactionCategory
    ): Single<TransactionCategory> {
        return Single.fromCallable {
            val pos = transactionCategoryExpenseList!!.indexOf(transactionCategory)
            if (pos >= 0) transactionCategoryExpenseList!![pos] = transactionCategory
            transactionCategory
        }
    }

    override fun deleteTransactionExpenseCategory(id: Int): Single<Boolean> {
        return Single.fromCallable {
            val pos = getTransactionCategoryExpensePosById(id)
            val b = pos != -1
            if (b) transactionCategoryExpenseList!!.removeAt(pos)
            b
        }
    }

    override fun setAllTransactionExpenseCategories(
        transactionCategoryList: List<TransactionCategory>
    ): Single<Boolean> {
        return Single.fromCallable {
            transactionCategoryExpenseList = ArrayList()
            transactionCategoryExpenseList!!.addAll(transactionCategoryList)
            true
        }
    }

    private val accountsSumGroupByTypeAndCurrency: Map<String, DoubleArray>
        get() {
            Log.d(TAG, "getAccountsSumGroupByTypeAndCurrency")
            val results: MutableMap<String, DoubleArray> = HashMap()
            for (currency in currencyArray) {
                val accountListFilteredByCurrency = accountList!!
                    .filter { a: Account -> a.currency == currency }
                val result = DoubleArray(4)
                var accountSum: Double
                for (i in 0..2) {
                    accountSum = 0.0
                    val accountListFilteredByType = accountListFilteredByCurrency
                        .filter { a: Account -> a.type == i }
                    for (account in accountListFilteredByType) {
                        accountSum += account.amount
                    }
                    result[i] = accountSum
                }
                val debtListFilteredByCurrency = debtList!!
                    .filter { d: Debt -> d.currency == currency }
                var debtSum = 0.0
                var debtVal: Double
                for (debt in debtListFilteredByCurrency) {
                    debtVal = debt.amountCurrent
                    if (debt.type == 1) {
                        debtVal *= -1.0
                    }
                    debtSum += debtVal
                }
                result[3] = debtSum
                results[currency] = result
            }
            return results
        }

    private fun getTransactionsStatisticByPosition(position: Int): Map<String, DoubleArray> {
        Log.d(TAG, "getTransactionsStatisticByPosition")
        var period: Long = 0
        when (position) {
            1 -> period = DateConstants.DAY
            2 -> period = DateConstants.WEEK
            3 -> period = DateConstants.MONTH
            4 -> period = DateConstants.YEAR
            5 -> period = Long.MAX_VALUE
        }
        val result: MutableMap<String, DoubleArray> = HashMap()
        val currentTime = System.currentTimeMillis()
        var cost: Double
        var income: Double
        for (currency in currencyArray) {
            val transactionListFilteredByCurrency = transactionList!!
                .filter { t: Transaction -> t.currency == currency }
            cost = 0.0
            income = 0.0
            for (transaction in transactionListFilteredByCurrency) {
                val date = transaction.date
                val amount = transaction.amount
                if (currentTime > date && period >= currentTime - date) {
                    if (numberFormatManager.isDoubleNegative(amount)) {
                        cost += amount
                    } else {
                        income += amount
                    }
                }
            }
            result[currency] = doubleArrayOf(cost, income)
        }
        return result
    }

    private fun getAccountPosById(id: Int): Int {
        for (i in accountList!!.indices) {
            if (accountList!![i].id == id) return i
        }
        return -1
    }

    private fun getTransactionPosById(id: Int): Int {
        for (i in transactionList!!.indices) {
            if (transactionList!![i].id == id) return i
        }
        return -1
    }

    private fun getDebtPosById(id: Int): Int {
        for (i in debtList!!.indices) {
            if (debtList!![i].id == id) return i
        }
        return -1
    }

    private fun getTransactionCategoryIncomePosById(id: Int): Int {
        for (i in transactionCategoryIncomeList!!.indices) {
            if (transactionCategoryIncomeList!![i].id == id) return i
        }
        return -1
    }

    private fun getTransactionCategoryExpensePosById(id: Int): Int {
        for (i in transactionCategoryExpenseList!!.indices) {
            if (transactionCategoryExpenseList!![i].id == id) return i
        }
        return -1
    }

    companion object {
        private val TAG = MemoryRepository::class.java.simpleName
    }
}