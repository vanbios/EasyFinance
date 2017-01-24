package com.androidcollider.easyfin.common.models;

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
@EqualsAndHashCode(exclude = {"type", "idAccount", "name", "accountName", "currency", "amountCurrent", "amountAll", "accountAmount", "date"})
public class Debt implements Serializable {

    @Setter
    private int id;
    private int type;
    private int idAccount;
    private String name;
    private String accountName;
    private String currency;
    @Setter
    private double amountCurrent;
    @Setter
    private double amountAll;
    private double accountAmount;
    private long date;
}
