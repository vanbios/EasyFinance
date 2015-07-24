package com.androidcollider.easyfin.objects;



public class Rates {

    private int id;
    private long date;
    private String currency, rateType;
    private double bid, ask;


    public Rates (int id, long date, String currency, String rateType, double bid, double ask) {
        this.id = id;
        this.date = date;
        this.currency = currency;
        this.rateType = rateType;
        this.bid = bid;
        this.ask = ask;
    }


    public int getId() {return id;}

    public long getDate() {return date;}

    public String getCurrency() {return currency;}

    public String getRateType() {return rateType;}

    public double getBid() {return bid;}

    public double getAsk() {return ask;}

}


