package com.androidcollider.easyfin.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class InternetTester {

    public static boolean isConnectionEnabled(Context context){
        return (((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null);
    }

}
