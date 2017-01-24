package com.androidcollider.easyfin.common.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Ihor Bilous
 */

@Getter
@AllArgsConstructor
public class Data {

    private List<Account> accountList;
    private List<Transaction> transactionList;
    private List<Debt> debtList;
    private double[] ratesArray;
}
