package com.androidcollider.easyfin.objects;


public class Transaction {

    private long date;
    private int idAccount;
    private double amount;
    private String category;
    private String currency;

    private double accountAmount;

    private String accountName;

    private String accountType;



    public Transaction (long date, double amount, String category, int idAccount, double accountAmount) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.idAccount = idAccount;
        this.accountAmount = accountAmount;
    }


    public Transaction (long date, double amount, String category, String accountName, String currency, String accountType) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.accountName = accountName;
        this.currency = currency;
        this.accountType = accountType;
    }



    public long getDate() {return date;}

    public int getIdAccount() {return idAccount;}

    public double getAmount() {return amount;}

    public String getCategory() {return category;}

    public String getCurrency() {return currency;}

    public String getAccountName() {return accountName;}

    public String getAccountType() {return accountType;}

    public double getAccountAmount() {return accountAmount;}
}
