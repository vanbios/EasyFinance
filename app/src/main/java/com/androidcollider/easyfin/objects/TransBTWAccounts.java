package com.androidcollider.easyfin.objects;


public class TransBTWAccounts {

    private String name1;
    private String name2;
    private double amount;
    private String currency;
    private long date;
    private String type1;
    private String type2;


    public TransBTWAccounts (String name1, String name2, double amount,
                             String currency, long date, String type1, String type2) {

        this.name1 = name1;
        this.name2 = name2;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.type1 = type1;
        this.type2 = type2;
    }

    public String getName1() {return name1;}

    public String getName2() {return name2;}

    public double getAmount() {return amount;}

    public String getCurrency() {return currency;}

    public long getDate() {return date;}

    public String getType1() {return type1;}

    public String getType2() {return type2;}
}
