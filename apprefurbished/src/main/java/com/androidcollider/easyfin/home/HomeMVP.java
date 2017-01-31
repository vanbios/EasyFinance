package com.androidcollider.easyfin.home;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.util.Map;

import rx.Observable;

/**
 * @author Ihor Bilous
 */

interface HomeMVP {

    interface Model {

        Observable<Pair<Map<String, double[]>, Map<String, double[]>>> getBalanceAndStatistic(int statisticPosition);

        Observable<Map<String, double[]>> getBalance();

        Observable<Map<String, double[]>> getStatistic(int statisticPosition);
    }

    interface View {

        void setBalanceAndStatistic(Pair<Map<String, double[]>, Map<String, double[]>> mapPair);

        void updateBalanceAndStatistic(Pair<Map<String, double[]>, Map<String, double[]>> mapPair);

        void updateBalance(Map<String, double[]> map);

        void updateStatistic(Map<String, double[]> map);
    }

    interface Presenter {

        void setView(@Nullable HomeMVP.View view);

        void loadBalanceAndStatistic(int statisticPosition);

        void updateBalanceAndStatistic(int statisticPosition);

        void updateBalance();

        void updateStatistic(int statisticPosition);
    }
}