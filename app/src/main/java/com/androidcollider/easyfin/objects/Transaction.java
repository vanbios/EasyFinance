package com.androidcollider.easyfin.objects;


public class Transaction {

    private long date;
    private int id_account;
    private double amount;
    private String category;
    private String currency;

    private double account_amount;

    private String account_name;

    private String account_type;



    public Transaction (long date, double amount, String category, int id_account, String currency, double account_amount) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.id_account = id_account;
        this.currency = currency;
        this.account_amount = account_amount;
    }


    public Transaction (long date, double amount, String category, String account_name, String currency, String account_type) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.account_name = account_name;
        this.currency = currency;
        this.account_type = account_type;
    }



    public long getDate() {return date;}

    public int getId_account() {return id_account;}

    public double getAmount() {return amount;}

    public String getCategory() {return category;}

    public String getCurrency() {return currency;}

    public String getAccount_name() {return account_name;}

    public String getAccount_type() {return account_type;}

    public double getAccount_amount() {return account_amount;}
}
