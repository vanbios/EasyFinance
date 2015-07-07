package com.androidcollider.easyfin.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class InternetTester {

    public static boolean isConnectionEnabled(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null);
    }

}
