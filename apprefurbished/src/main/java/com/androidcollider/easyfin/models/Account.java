package com.androidcollider.easyfin.models;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Account implements Serializable {
    private int id;
    private String name;
    private double amount;
    private int type;
    private String currency;
}
