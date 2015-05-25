package com.androidcollider.easyfin.objects;

import android.os.Parcel;
import android.os.Parcelable;


public class Account implements Parcelable {

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


    public Account(Parcel accountParcelIn) {
        this.id = accountParcelIn.readInt();
        this.name = accountParcelIn.readString();
        this.amount = accountParcelIn.readDouble();
        this.type = accountParcelIn.readString();
        this.currency = accountParcelIn.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeDouble(this.amount);
        dest.writeString(this.type);
        dest.writeString(this.currency);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };



}
