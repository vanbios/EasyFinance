package com.androidcollider.easyfin.objects;


import java.util.Date;

public class Rates {

    private int id;
    private Date date;
    private String currency;
    private String rateType;
    private double bid;
    private double ask;


    public Rates (int id, Date date, String currency, String rateType, double bid, double ask) {
        this.id = id;
        this.date = date;
        this.currency = currency;
        this.rateType = rateType;
        this.bid = bid;
        this.ask = ask;
    }


    public int getId() {return id;}

    public Date getDate() {return date;}

    public String getCurrency() {return currency;}

    public String getRateType() {return rateType;}

    public double getBid() {return bid;}

    public double getAsk() {return ask;}

}


