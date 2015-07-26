package com.androidcollider.easyfin.objects;


import java.io.Serializable;

public class Debt implements Serializable {

    private int id, type, idAccount;
    private String name, accountName, currency;
    private double amountCurrent, amountAll, accountAmount;
    private long date;


    public Debt (String name, double amountCurrent, int type, int idAccount, long date, double accountAmount) {
        this.name = name;
        this.amountCurrent = amountCurrent;
        this.type = type;
        this.idAccount = idAccount;
        this.date = date;
        this.accountAmount = accountAmount;
    }

    public Debt (String name, double amountCurrent, int type, int idAccount, long date, double accountAmount, int id) {
        this.name = name;
        this.amountCurrent = amountCurrent;
        this.type = type;
        this.idAccount = idAccount;
        this.date = date;
        this.accountAmount = accountAmount;
        this.id = id;
    }

    public Debt (String name, double amountCurrent, double amountAll, int type, long date, String accountName, String currency, int idAccount, int id) {
        this.name = name;
        this.amountCurrent = amountCurrent;
        this.amountAll = amountAll;
        this.type = type;
        this.date = date;
        this.accountName = accountName;
        this.currency = currency;
        this.idAccount = idAccount;
        this.id = id;
    }



    public String getName() {return name;}

    public double getAmountCurrent() {return amountCurrent;}

    public double getAmountAll() {return amountAll;}

    public int getType() {return type;}

    public int getIdAccount() {return idAccount;}

    public long getDate() {return date;}

    public double getAccountAmount() {return accountAmount;}

    public int getId() {return id;}

    public String getAccountName() {return accountName;}

    public String getCurrency() {return currency;}

}
