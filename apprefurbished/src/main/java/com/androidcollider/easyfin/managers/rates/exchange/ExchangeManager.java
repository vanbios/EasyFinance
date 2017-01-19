package com.androidcollider.easyfin.managers.rates.exchange;

import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.repository.Repository;

import lombok.Getter;
import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

public class ExchangeManager {

    private Repository repository;
    private ResourcesManager resourcesManager;
    @Getter
    private double[] rates = {1, 27.708, 29.225, 0.463, 34.1};

    ExchangeManager(Repository repository, ResourcesManager resourcesManager) {
        this.repository = repository;
        this.resourcesManager = resourcesManager;
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
        String[] currencyArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);

        int posFrom = 0;
        int posTo = 0;

        for (int i = 0; i < currencyArray.length; i++) {
            if (currencyArray[i].equals(currFrom)) posFrom = i;
            if (currencyArray[i].equals(currTo)) posTo = i;
        }
        return rates[posTo] / rates[posFrom];
    }
}