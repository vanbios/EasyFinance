package com.androidcollider.easyfin.objects;


public class DateConstants {

    private long day;
    private long week;
    private long month;
    private long year;

    public DateConstants() {
        this.day = 86400000;
        this.week = 604800000;
        this.month = 2592000000L;
        this.year = 31536000000L;
    }

    public long getDay() {return day;}

    public long getWeek() {return week;}

    public long getMonth() {return month;}

    public long getYear() {return year;}
}
