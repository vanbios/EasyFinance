package com.androidcollider.easyfin.common.managers.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Ihor Bilous
 */

public class PermissionManager {

    private final static int REQUEST_PERMISSIONS = 1000;

    private AppCompatActivity activity;


    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public boolean requestRequiredPermissions() {
        checkActivityIsNotNull();
        if (checkWriteExternalStoragePermissionGranted()
                && checkReadExternalStoragePermissionGranted()
                && checkNetworkStatePermissionGranted()) {
            return true;
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    checkIsReadStoragePermissionNeeded() ?
                            new String[]{
                                    Manifest.permission.ACCESS_NETWORK_STATE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            } :
                            new String[]{
                                    Manifest.permission.ACCESS_NETWORK_STATE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            },
                    REQUEST_PERMISSIONS
            );
            return false;
        }
    }

    public boolean onRequestPermissionsResult(int requestCode,
                                              String permissions[],
                                              int[] grantResults) {
        checkActivityIsNotNull();
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            System.out.println("Permissions denied!");
                            activity.finish();
                            return false;
                        }
                    }
                    System.out.println("Permissions granted!");
                    return true;
                } else {
                    System.out.println("Permissions denied!");
                    activity.finish();
                    return false;
                }
            }
        }
        return false;
    }

    private boolean checkWriteExternalStoragePermissionGranted() {
        checkActivityIsNotNull();
        return !checkIsPermissionsNeeded() ||
                ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkNetworkStatePermissionGranted() {
        checkActivityIsNotNull();
        return !checkIsPermissionsNeeded() ||
                ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkReadExternalStoragePermissionGranted() {
        checkActivityIsNotNull();
        return !checkIsPermissionsNeeded() ||
                !checkIsReadStoragePermissionNeeded() ||
                ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    private void checkActivityIsNotNull() {
        if (activity == null)
            throw new IllegalStateException("Activity must be not null in Permissions Manager. Please set it!");
    }

    private boolean checkIsPermissionsNeeded() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M;
    }

    private boolean checkIsReadStoragePermissionNeeded() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
}