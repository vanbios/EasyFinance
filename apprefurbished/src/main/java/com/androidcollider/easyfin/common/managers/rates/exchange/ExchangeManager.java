package com.androidcollider.easyfin.common.managers.rates.exchange;

import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.repository.Repository;

import lombok.Getter;

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

    public void updateRates() {
        repository.getRates()
                .subscribe(
                        newRates -> {
                            for (int i = 1; i < rates.length; i++) {
                                if (newRates[i - 1] > 0) rates[i] = newRates[i - 1];
                            }
                        },
                        Throwable::printStackTrace
                );
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