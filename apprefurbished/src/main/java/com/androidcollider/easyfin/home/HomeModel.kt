package com.androidcollider.easyfin.home

import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class HomeModel(private val repository: Repository) : HomeMVP.Model {

    override fun getBalanceAndStatistic(statisticPosition: Int):
            Single<Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>> {
        return Single.zip(
            repository.accountsAmountSumGroupByTypeAndCurrency!!,
            repository.getTransactionsStatistic(statisticPosition)!!
        ) { first: Map<String, DoubleArray>, second: Map<String, DoubleArray> ->
            Pair(first, second)
        }
    }

    override val balance: Single<Map<String, DoubleArray>>
        get() = repository.accountsAmountSumGroupByTypeAndCurrency!!

    override fun getStatistic(statisticPosition: Int): Single<Map<String, DoubleArray>> {
        return repository.getTransactionsStatistic(statisticPosition)!!
    }
}