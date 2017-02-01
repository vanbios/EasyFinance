package com.androidcollider.easyfin.debts.list;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Ihor Bilous
 */

@Getter
@Builder
class DebtViewModel {

    private int id;
    private String name;
    private String accountName;
    private String date;
    private String amount;
    private int progress;
    private String progressPercents;
    private int colorRes;
}