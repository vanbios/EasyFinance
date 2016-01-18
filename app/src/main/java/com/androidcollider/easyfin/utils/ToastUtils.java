package com.androidcollider.easyfin.utils;


import android.content.Context;
import android.widget.Toast;


public class ToastUtils {

    private static Toast staticToast;

    public static void showClosableToast(Context context, String text, int duration) {
        if (staticToast != null) {
            staticToast.cancel();
        }
        staticToast = new Toast(context);
        switch (duration) {
            case 1: {duration = Toast.LENGTH_SHORT; break;}
            case 2: {duration = Toast.LENGTH_LONG; break;}
        }
        staticToast = Toast.makeText(context, text, duration);
        staticToast.show();
    }

}
