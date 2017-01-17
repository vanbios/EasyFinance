package com.androidcollider.easyfin.models;

import java.io.Serializable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ihor Bilous
 */

@Getter
@Builder
@ToString
@EqualsAndHashCode(exclude = {"idAccount", "category", "accountType", "date", "amount", "accountAmount", "currency", "accountName"})
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
