package com.androidcollider.easyfin.objects;


import java.io.Serializable;

public class Account implements Serializable{

    private int id;
    private String name;
    private double amount;
    private String type;
    private String currency;


    public Account(int id, String name, double amount, String type, String currency) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.currency = currency;
    }

    public Account(String name, double amount, String type, String currency) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.currency = currency;
    }



    public int getId() {return id;}

    public String getName() {return name;}

    public double getAmount() {return amount;}

    public String getType() {return type;}

    public String getCurrency() {return currency;}

}
