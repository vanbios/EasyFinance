package com.androidcollider.easyfin.transactions.list;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Ihor Bilous
 */

@Getter
@Builder
public class TransactionViewModel {

    private int id;
    private String accountName;
    private String date;
    private String amount;
    private int accountType;
    private int colorRes;
    private int category;
    private boolean isExpense;
}