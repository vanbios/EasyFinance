package com.androidcollider.easyfin.common.view_models;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Ihor Bilous
 */

@Getter
@Builder
public class SpinAccountViewModel {

    private int id;
    private String name;
    private double amount;
    private String amountString;
    private int type;
    private String currency;
}