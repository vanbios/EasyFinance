package com.androidcollider.easyfin.objects;


import java.io.Serializable;

public class Debt implements Serializable {

    private int id;
    private String name;
    private double amount;
    private int type;
    private int idAccount;
    private long date;

    private double accountAmount;
    private String accountName;
    private String currency;


    public Debt (String name, double amount, int type, int idAccount, long date, double accountAmount) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.idAccount = idAccount;
        this.date = date;
        this.accountAmount = accountAmount;
    }

    public Debt (String name, double amount, int type, long date, String accountName, String currency, int idAccount, int id) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.accountName = accountName;
        this.currency = currency;
        this.idAccount = idAccount;
        this.id = id;
    }




    public String getName() {return name;}

    public double getAmount() {return amount;}

    public int getType() {return type;}

    public int getIdAccount() {return idAccount;}

    public long getDate() {return date;}

    public double getAccountAmount() {return accountAmount;}

    public int getId() {return id;}

    public String getAccountName() {return accountName;}

    public String getCurrency() {return currency;}
}
