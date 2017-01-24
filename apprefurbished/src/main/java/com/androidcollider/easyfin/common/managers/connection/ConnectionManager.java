package com.androidcollider.easyfin.common.managers.connection;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * @author Ihor Bilous
 */

public class ConnectionManager {

    private Context context;

    public ConnectionManager(Context context) {
        this.context = context;
    }

    public boolean isConnectionEnabled() {
        return (((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null);
    }
}