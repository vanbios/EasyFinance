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
@EqualsAndHashCode(exclude = {"name", "amount", "type", "currency"})
public class Account implements Serializable {
    @Setter
    private int id;
    private String name;
    @Setter
    private double amount;
    private int type;
    private String currency;
}