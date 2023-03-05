package com.androidcollider.easyfin.common.repository

import android.util.Log
import com.androidcollider.easyfin.common.managers.import_export_db.ImportExportDbManager
import com.androidcollider.easyfin.common.models.*
import com.androidcollider.easyfin.common.repository.database.Database
import com.androidcollider.easyfin.common.repository.memory.Memory
import com.androidcollider.easyfin.common.utils.BackgroundExecutor.safeBackgroundExecutor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * @author Ihor Bilous
 */
internal class DataRepository(
    @param:Memory private val memoryRepository: Repository,
    @param:Database private val databaseRepository: Repository,
    private val importExportDbManager: ImportExportDbManager
) : Repository {

    private val subscribeSc = Schedulers.from(
        safeBackgroundExecutor
    )
    private val observeSc = AndroidSchedulers.mainThread()

    override fun addNewAccount(account: Account): Single<Account> {
        return databaseRepository.addNewAccount(account)
            .flatMap { accountWithId: Account -> memoryRepository.addNewAccount(accountWithId) }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override val allAccounts: Single<List<Account>>
        get() {
            val memoryObservable = memoryRepository.allAccounts
            Log.d(TAG, "getAllAccounts " + (memoryObservable != null))
            return if (memoryObservable != null && !isDataExpired) memoryObservable else loadAllDataFromDB()
                .flatMap { aBoolean: Boolean ->
                    if (aBoolean) importExportDbManager.isDBExpired = false
                    (if (aBoolean) memoryRepository.allAccounts else databaseRepository.allAccounts)!!
                }
                .subscribeOn(subscribeSc)
                .observeOn(observeSc)
        }

    override fun updateAccount(account: Account): Single<Account> {
        return databaseRepository.updateAccount(account)
            .flatMap { account1: Account -> memoryRepository.updateAccount(account1) }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun deleteAccount(id: Int): Single<Boolean> {
        return Single.zip(
            databaseRepository.deleteAccount(id),
            memoryRepository.deleteAccount(id)
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun transferBTWAccounts(
        idAccount1: Int,
        accountAmount1: Double,
        idAccount2: Int,
        accountAmount2: Double
    ): Single<Boolean> {
        return Single.zip(
            databaseRepository.transferBTWAccounts(
                idAccount1,
                accountAmount1,
                idAccount2,
                accountAmount2
            ),
            memoryRepository.transferBTWAccounts(
                idAccount1,
                accountAmount1,
                idAccount2,
                accountAmount2
            )
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun addNewTransaction(transaction: Transaction): Single<Transaction> {
        return databaseRepository.addNewTransaction(transaction)
            .flatMap { transactionWithId: Transaction ->
                memoryRepository.addNewTransaction(transactionWithId)
            }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override val allTransactions: Single<List<Transaction>>
        get() {
            val memoryObservable = memoryRepository.allTransactions
            Log.d(TAG, "getAllTransactions " + (memoryObservable != null))
            return if (memoryObservable != null && !isDataExpired) memoryObservable else loadAllDataFromDB()
                .flatMap { aBoolean: Boolean ->
                    if (aBoolean) importExportDbManager.isDBExpired = false
                    (if (aBoolean) memoryRepository.allTransactions else databaseRepository.allTransactions)!!
                }
                .subscribeOn(subscribeSc)
                .observeOn(observeSc)
        }

    override fun updateTransaction(transaction: Transaction): Single<Transaction> {
        return databaseRepository.updateTransaction(transaction)
            .flatMap { transaction1: Transaction ->
                memoryRepository.updateTransaction(transaction1)
            }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun updateTransactionDifferentAccounts(
        transaction: Transaction,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Single<Boolean> {
        return Single.zip(
            databaseRepository.updateTransactionDifferentAccounts(
                transaction,
                oldAccountAmount,
                oldAccountId
            ),
            memoryRepository.updateTransactionDifferentAccounts(
                transaction,
                oldAccountAmount,
                oldAccountId
            )
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun deleteTransaction(
        idAccount: Int,
        idTransaction: Int,
        amount: Double
    ): Single<Boolean> {
        return Single.zip(
            databaseRepository.deleteTransaction(idAccount, idTransaction, amount),
            memoryRepository.deleteTransaction(idAccount, idTransaction, amount)
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun addNewDebt(debt: Debt): Single<Debt> {
        return databaseRepository.addNewDebt(debt)
            .flatMap { debt1: Debt -> memoryRepository.addNewDebt(debt1) }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override val allDebts: Single<List<Debt>>
        get() {
            val memoryObservable = memoryRepository.allDebts
            Log.d(TAG, "getAllDebts " + (memoryObservable != null))
            return if (memoryObservable != null && !isDataExpired) memoryObservable else loadAllDataFromDB()
                .flatMap { aBoolean: Boolean ->
                    if (aBoolean) importExportDbManager.isDBExpired = false
                    (if (aBoolean) memoryRepository.allDebts else databaseRepository.allDebts)!!
                }
                .subscribeOn(subscribeSc)
                .observeOn(observeSc)
        }

    override fun updateDebt(debt: Debt): Single<Debt> {
        return databaseRepository.updateDebt(debt)
            .flatMap { debt1: Debt -> memoryRepository.updateDebt(debt1) }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun updateDebtDifferentAccounts(
        debt: Debt,
        oldAccountAmount: Double,
        oldAccountId: Int
    ): Single<Boolean> {
        return Single.zip(
            databaseRepository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId),
            memoryRepository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId)
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun deleteDebt(
        idAccount: Int,
        idDebt: Int,
        amount: Double,
        type: Int
    ): Single<Boolean> {
        return Single.zip(
            databaseRepository.deleteDebt(idAccount, idDebt, amount, type),
            memoryRepository.deleteDebt(idAccount, idDebt, amount, type)
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun payFullDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int
    ): Single<Boolean> {
        return Single.zip(
            databaseRepository.payFullDebt(idAccount, accountAmount, idDebt),
            memoryRepository.payFullDebt(idAccount, accountAmount, idDebt)
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun payPartOfDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double
    ): Single<Boolean> {
        return Single.zip(
            databaseRepository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount),
            memoryRepository.payPartOfDebt(idAccount, accountAmount, idDebt, debtAmount)
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun takeMoreDebt(
        idAccount: Int,
        accountAmount: Double,
        idDebt: Int,
        debtAmount: Double,
        debtAllAmount: Double
    ): Single<Boolean> {
        return Single.zip(
            databaseRepository.takeMoreDebt(
                idAccount,
                accountAmount,
                idDebt,
                debtAmount,
                debtAllAmount
            ),
            memoryRepository.takeMoreDebt(
                idAccount,
                accountAmount,
                idDebt,
                debtAmount,
                debtAllAmount
            )
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun getTransactionsStatistic(position: Int): Single<Map<String, DoubleArray>> {
        val memoryObservable = memoryRepository.getTransactionsStatistic(position)
        Log.d(TAG, "getTransactionsStatistic " + (memoryObservable != null))
        return if (memoryObservable != null && !isDataExpired)
            memoryObservable else loadAllDataFromDB()
            .flatMap { aBoolean: Boolean ->
                if (aBoolean) importExportDbManager.isDBExpired = false
                databaseRepository.getTransactionsStatistic(position)!!
            }
    }

    override val accountsAmountSumGroupByTypeAndCurrency: Single<Map<String, DoubleArray>>
        get() {
            val memoryObservable = memoryRepository.accountsAmountSumGroupByTypeAndCurrency
            Log.d(
                TAG,
                "getAccountsAmountSumGroupByTypeAndCurrency " + (memoryObservable != null)
            )
            return if (memoryObservable != null && !isDataExpired)
                memoryObservable else loadAllDataFromDB()
                .flatMap { aBoolean: Boolean ->
                    if (aBoolean) importExportDbManager.isDBExpired = false
                    (if (aBoolean) memoryRepository.accountsAmountSumGroupByTypeAndCurrency
                    else databaseRepository.accountsAmountSumGroupByTypeAndCurrency)!!
                }
        }

    override fun updateRates(ratesList: List<Rates>): Single<Boolean> {
        return Single.zip(
            databaseRepository.updateRates(ratesList),
            memoryRepository.updateRates(ratesList)
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override val rates: Single<DoubleArray>
        get() {
            val memoryObservable = memoryRepository.rates
            Log.d(TAG, "getRates " + (memoryObservable != null))
            return if (memoryObservable != null && !isDataExpired) memoryObservable else
                Single.unsafeCreate { s: SingleObserver<in DoubleArray> ->
                    databaseRepository.rates!!.subscribe { rates: DoubleArray ->
                        memoryRepository.setRates(rates)
                            .subscribe({
                                s.onSuccess(rates)
                            }) { obj: Throwable -> obj.printStackTrace() }
                    }
                }
                    .subscribeOn(subscribeSc)
                    .observeOn(observeSc)
        }

    override fun setAllAccounts(accountList: List<Account>): Single<Boolean> {
        return memoryRepository.setAllAccounts(accountList)
    }

    override fun setAllTransactions(transactionList: List<Transaction>): Single<Boolean> {
        return memoryRepository.setAllTransactions(transactionList)
    }

    override fun setAllDebts(debtList: List<Debt>): Single<Boolean> {
        return memoryRepository.setAllDebts(debtList)
    }

    override fun setRates(rates: DoubleArray): Single<Boolean> {
        return memoryRepository.setRates(rates)
    }

    override fun addNewTransactionIncomeCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory> {
        return databaseRepository.addNewTransactionIncomeCategory(transactionCategory)
            .flatMap { transactionCategory1: TransactionCategory ->
                memoryRepository.addNewTransactionIncomeCategory(
                    transactionCategory1
                )
            }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override val allTransactionIncomeCategories: Single<List<TransactionCategory>>
        get() {
            val memoryObservable = memoryRepository.allTransactionIncomeCategories
            Log.d(TAG, "getAllTransactionIncomeCategories " + (memoryObservable != null))
            return if (memoryObservable != null && !isDataExpired)
                memoryObservable else loadAllDataFromDB()
                .flatMap { aBoolean: Boolean ->
                    if (aBoolean) importExportDbManager.isDBExpired = false
                    (if (aBoolean) memoryRepository.allTransactionIncomeCategories
                    else databaseRepository.allTransactionIncomeCategories)!!
                }
                .subscribeOn(subscribeSc)
                .observeOn(observeSc)
        }

    override fun updateTransactionIncomeCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory> {
        return databaseRepository.updateTransactionIncomeCategory(transactionCategory)
            .flatMap { transactionCategory1: TransactionCategory ->
                memoryRepository.updateTransactionIncomeCategory(
                    transactionCategory1
                )
            }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun deleteTransactionIncomeCategory(id: Int): Single<Boolean> {
        return Single.zip(
            databaseRepository.deleteTransactionIncomeCategory(id),
            memoryRepository.deleteTransactionIncomeCategory(id)
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun setAllTransactionIncomeCategories(
        transactionCategoryList: List<TransactionCategory>
    ): Single<Boolean> {
        return memoryRepository.setAllTransactionIncomeCategories(transactionCategoryList)
    }

    override fun addNewTransactionExpenseCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory> {
        return databaseRepository.addNewTransactionExpenseCategory(transactionCategory)
            .flatMap { transactionCategory1: TransactionCategory ->
                memoryRepository.addNewTransactionExpenseCategory(
                    transactionCategory1
                )
            }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override val allTransactionExpenseCategories: Single<List<TransactionCategory>>
        get() {
            val memoryObservable = memoryRepository.allTransactionExpenseCategories
            Log.d(TAG, "getAllTransactionExpenseCategories " + (memoryObservable != null))
            return if (memoryObservable != null && !isDataExpired)
                memoryObservable else loadAllDataFromDB()
                .flatMap { aBoolean: Boolean ->
                    if (aBoolean) importExportDbManager.isDBExpired = false
                    (if (aBoolean) memoryRepository.allTransactionExpenseCategories
                    else databaseRepository.allTransactionExpenseCategories)!!
                }
                .subscribeOn(subscribeSc)
                .observeOn(observeSc)
        }

    override fun updateTransactionExpenseCategory(transactionCategory: TransactionCategory):
            Single<TransactionCategory> {
        return databaseRepository.updateTransactionExpenseCategory(transactionCategory)
            .flatMap { transactionCategory1: TransactionCategory ->
                memoryRepository.updateTransactionExpenseCategory(transactionCategory1)
            }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun deleteTransactionExpenseCategory(id: Int): Single<Boolean> {
        return Single.zip(
            databaseRepository.deleteTransactionExpenseCategory(id),
            memoryRepository.deleteTransactionExpenseCategory(id)
        ) { aBoolean: Boolean, aBoolean2: Boolean -> aBoolean && aBoolean2 }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    override fun setAllTransactionExpenseCategories(
        transactionCategoryList: List<TransactionCategory>
    ): Single<Boolean> {
        return memoryRepository.setAllTransactionExpenseCategories(transactionCategoryList)
    }

    private val isDataExpired: Boolean
        get() = importExportDbManager.isDBExpired

    private fun loadAllDataFromDB(): Single<Boolean> {
        return Single.zip(
            databaseRepository.allAccounts!!,
            databaseRepository.allTransactions!!,
            databaseRepository.allDebts!!,
            databaseRepository.rates!!,
            databaseRepository.allTransactionIncomeCategories!!,
            databaseRepository.allTransactionExpenseCategories!!
        ) { accountList: List<Account>,
            transactionList: List<Transaction>,
            debtList: List<Debt>,
            ratesArray: DoubleArray,
            transactionCategoryIncomeList: List<TransactionCategory>,
            transactionCategoryExpenseList: List<TransactionCategory> ->
            Data(
                accountList,
                transactionList,
                debtList,
                ratesArray,
                transactionCategoryIncomeList,
                transactionCategoryExpenseList
            )
        }
            .flatMap { data: Data ->
                Single.zip(
                    memoryRepository.setAllAccounts(data.accountList!!),
                    memoryRepository.setAllTransactions(data.transactionList!!),
                    memoryRepository.setAllDebts(data.debtList!!),
                    memoryRepository.setRates(data.ratesArray),
                    memoryRepository.setAllTransactionIncomeCategories(
                        data.transactionCategoryIncomeList!!
                    ),
                    memoryRepository.setAllTransactionExpenseCategories(
                        data.transactionCategoryExpenseList!!
                    )
                ) { aBoolean: Boolean,
                    aBoolean2: Boolean,
                    aBoolean3: Boolean,
                    aBoolean4: Boolean,
                    aBoolean5: Boolean,
                    aBoolean6: Boolean ->
                    aBoolean && aBoolean2 && aBoolean3 && aBoolean4 && aBoolean5 && aBoolean6
                }
            }
            .subscribeOn(subscribeSc)
            .observeOn(observeSc)
    }

    companion object {
        private val TAG = DataRepository::class.java.simpleName
    }
}