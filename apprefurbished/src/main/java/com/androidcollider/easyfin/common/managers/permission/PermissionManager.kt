package com.androidcollider.easyfin.common.managers.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * @author Ihor Bilous
 */
class PermissionManager {
    private var activity: AppCompatActivity? = null
    fun setActivity(activity: AppCompatActivity?) {
        this.activity = activity
    }

    fun requestRequiredPermissions(): Boolean {
        checkActivityIsNotNull()
        return if (checkWriteExternalStoragePermissionGranted()
            && checkReadExternalStoragePermissionGranted()
            && checkNetworkStatePermissionGranted()
        ) {
            true
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, arrayOf(
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQUEST_PERMISSIONS
                )
            }
            false
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ): Boolean {
        checkActivityIsNotNull()
        return if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty()) {
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        println("Permissions denied!")
                        activity?.finish()
                        return false
                    }
                }
                println("Permissions granted!")
                true
            } else {
                println("Permissions denied!")
                activity?.finish()
                false
            }
        } else false
    }

    private fun checkWriteExternalStoragePermissionGranted(): Boolean {
        checkActivityIsNotNull()
        return !isPermissionsNeeded ||
                checkPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
    }

    private fun checkNetworkStatePermissionGranted(): Boolean {
        checkActivityIsNotNull()
        return !isPermissionsNeeded ||
                checkPermission(
                    Manifest.permission.ACCESS_NETWORK_STATE
                )
    }

    private fun checkReadExternalStoragePermissionGranted(): Boolean {
        checkActivityIsNotNull()
        return !isPermissionsNeeded ||
                checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
    }

    private fun checkPermission(permission: String): Boolean {
        return try {
            (ContextCompat.checkSelfPermission(
                activity!!,
                permission
            )
                    == PackageManager.PERMISSION_GRANTED)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            false
        }
    }

    private fun checkActivityIsNotNull() {
        checkNotNull(activity) { "Activity must be not null in Permissions Manager. Please set it!" }
    }

    private val isPermissionsNeeded: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    companion object {
        private const val REQUEST_PERMISSIONS = 1000
    }
}