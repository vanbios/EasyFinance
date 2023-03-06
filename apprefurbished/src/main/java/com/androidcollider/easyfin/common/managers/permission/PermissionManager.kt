package com.androidcollider.easyfin.common.managers.permission;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
                    new String[]{
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
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
        if (requestCode == REQUEST_PERMISSIONS) {
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
        return false;
    }

    private boolean checkWriteExternalStoragePermissionGranted() {
        checkActivityIsNotNull();
        return !isPermissionsNeeded() ||
                checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private boolean checkNetworkStatePermissionGranted() {
        checkActivityIsNotNull();
        return !isPermissionsNeeded() ||
                checkPermission(
                        Manifest.permission.ACCESS_NETWORK_STATE);
    }

    private boolean checkReadExternalStoragePermissionGranted() {
        checkActivityIsNotNull();
        return !isPermissionsNeeded() ||
                checkPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private boolean checkPermission(String permission) {
        try {
            return ContextCompat.checkSelfPermission(activity,
                    permission)
                    == PackageManager.PERMISSION_GRANTED;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void checkActivityIsNotNull() {
        if (activity == null)
            throw new IllegalStateException("Activity must be not null in Permissions Manager. Please set it!");
    }

    private boolean isPermissionsNeeded() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M;
    }
}