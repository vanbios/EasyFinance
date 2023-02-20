package com.androidcollider.easyfin.home

import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Flowable

/**
 * @author Ihor Bilous
 */
internal class HomeModel(private val repository: Repository) : HomeMVP.Model {

    override fun getBalanceAndStatistic(statisticPosition: Int):
            Flowable<Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>> {
        return Flowable.combineLatest<
                Map<String, DoubleArray>,
                Map<String, DoubleArray>,
                Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>>(
            repository.accountsAmountSumGroupByTypeAndCurrency,
            repository.getTransactionsStatistic(statisticPosition)
        ) { first: Map<String, DoubleArray>, second: Map<String, DoubleArray> ->
            Pair(first, second)
        }
    }

    override val balance: Flowable<Map<String, DoubleArray>>
        get() = repository.accountsAmountSumGroupByTypeAndCurrency

    override fun getStatistic(statisticPosition: Int): Flowable<Map<String, DoubleArray>> {
        return repository.getTransactionsStatistic(statisticPosition)
    }
}