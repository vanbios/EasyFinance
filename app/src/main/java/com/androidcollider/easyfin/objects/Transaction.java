package com.androidcollider.easyfin.objects;


import java.io.Serializable;

public class Transaction implements Serializable {

    private int id, idAccount, category, accountType;
    private long date;
    private double amount, accountAmount;
    private String currency, accountName;



    public Transaction (long date, double amount, int category, int idAccount, double accountAmount) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.idAccount = idAccount;
        this.accountAmount = accountAmount;
    }


    public Transaction (long date, double amount, int category, int idAccount, double accountAmount, int id) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.idAccount = idAccount;
        this.accountAmount = accountAmount;
        this.id = id;
    }


    public Transaction (long date, double amount, int category, String accountName,
                        String currency, int accountType, int idAccount, int id) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.accountName = accountName;
        this.currency = currency;
        this.accountType = accountType;
        this.idAccount = idAccount;
        this.id = id;
    }



    public long getDate() {return date;}

    public int getIdAccount() {return idAccount;}

    public double getAmount() {return amount;}

    public int getCategory() {return category;}

    public String getCurrency() {return currency;}

    public String getAccountName() {return accountName;}

    public int getAccountType() {return accountType;}

    public double getAccountAmount() {return accountAmount;}

    public int getId() {return id;}

}
