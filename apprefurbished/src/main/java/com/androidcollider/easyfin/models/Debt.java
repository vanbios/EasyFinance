package com.androidcollider.easyfin.models;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Debt implements Serializable {
    private int id;
    private int type;
    private int idAccount;
    private String name;
    private String accountName;
    private String currency;
    private double amountCurrent;
    private double amountAll;
    private double accountAmount;
    private long date;
}
