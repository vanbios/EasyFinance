package com.androidcollider.easyfin.home;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.util.Map;

import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

class HomePresenter implements HomeMVP.Presenter {

    @Nullable
    private HomeMVP.View view;
    private HomeMVP.Model model;


    HomePresenter(HomeMVP.Model model) {
        this.model = model;
    }

    @Override
    public void setView(@Nullable HomeMVP.View view) {
        this.view = view;
    }

    @Override
    public void loadBalanceAndStatistic(int statisticPosition) {
        model.getBalanceAndStatistic(statisticPosition)
                .subscribe(new Subscriber<Pair<Map<String, double[]>, Map<String, double[]>>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Pair<Map<String, double[]>, Map<String, double[]>> pair) {
                        if (view != null) {
                            view.setBalanceAndStatistic(pair);
                        }
                    }
                });
    }

    @Override
    public void updateBalanceAndStatistic(int statisticPosition) {
        model.getBalanceAndStatistic(statisticPosition)
                .subscribe(new Subscriber<Pair<Map<String, double[]>, Map<String, double[]>>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Pair<Map<String, double[]>, Map<String, double[]>> pair) {
                        if (view != null) {
                            view.updateBalanceAndStatistic(pair);
                        }
                    }
                });
    }

    @Override
    public void updateBalance() {
        model.getBalance()
                .subscribe(new Subscriber<Map<String, double[]>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, double[]> map) {
                        if (view != null) {
                            view.updateBalance(map);
                        }
                    }
                });
    }

    @Override
    public void updateStatistic(int statisticPosition) {
        model.getStatistic(statisticPosition)
                .subscribe(new Subscriber<Map<String, double[]>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, double[]> map) {
                        if (view != null) {
                            view.updateStatistic(map);
                        }
                    }
                });
    }
}