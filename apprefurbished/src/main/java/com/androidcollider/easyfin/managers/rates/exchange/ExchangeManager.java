package com.androidcollider.easyfin.managers.rates.exchange;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.repository.Repository;

import lombok.Getter;
import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

public class ExchangeManager {

    private Repository repository;
    @Getter
    private double[] rates = {1, 27.708, 29.225, 0.463, 34.1};

    ExchangeManager(Repository repository) {
        this.repository = repository;
        updateRates();
    }

    private void updateRates() {
        repository.getRates()
                .subscribe(new Subscriber<double[]>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(double[] newRates) {
                        for (int i = 1; i < rates.length; i++) {
                            if (newRates[i - 1] > 0) rates[i] = newRates[i - 1];
                        }
                    }
                });
    }

    public double getExchangeRate(String currFrom, String currTo) {
        String[] currencyArray = App.getContext().getResources().getStringArray(R.array.account_currency_array);

        int posFrom = 0;
        int posTo = 0;

        for (int i = 0; i < currencyArray.length; i++) {
            if (currencyArray[i].equals(currFrom)) posFrom = i;
            if (currencyArray[i].equals(currTo)) posTo = i;
        }
        return rates[posTo] / rates[posFrom];
    }
}