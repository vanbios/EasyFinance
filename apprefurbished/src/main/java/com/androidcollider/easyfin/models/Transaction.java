package com.androidcollider.easyfin.models;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Transaction implements Serializable {

    @Setter
    private int id;
    private int idAccount;
    private int category;
    private int accountType;
    private long date;
    private double amount;
    private double accountAmount;
    private String currency;
    private String accountName;
}
