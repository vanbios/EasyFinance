package com.androidcollider.easyfin.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Ihor Bilous
 */

@Getter
@AllArgsConstructor
@ToString
public class Rates {

    private int id;
    private long date;
    private String currency;
    private String rateType;
    private double bid;
    private double ask;
}


