package com.androidcollider.easyfin.home;

import androidx.core.util.Pair;

import com.androidcollider.easyfin.common.repository.Repository;

import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;

/**
 * @author Ihor Bilous
 */

class HomeModel implements HomeMVP.Model {

    private Repository repository;

    HomeModel(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Flowable<Pair<Map<String, double[]>, Map<String, double[]>>> getBalanceAndStatistic(int statisticPosition) {
        return Flowable.combineLatest(
                repository.getAccountsAmountSumGroupByTypeAndCurrency(),
                repository.getTransactionsStatistic(statisticPosition),
                Pair::new);
    }

    @Override
    public Flowable<Map<String, double[]>> getBalance() {
        return repository.getAccountsAmountSumGroupByTypeAndCurrency();
    }

    @Override
    public Flowable<Map<String, double[]>> getStatistic(int statisticPosition) {
        return repository.getTransactionsStatistic(statisticPosition);
    }
}