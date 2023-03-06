package com.androidcollider.easyfin.common.managers.ui.toast

import android.content.Context
import android.widget.Toast

/**
 * @author Ihor Bilous
 */
class ToastManager {
    fun showClosableToast(context: Context, text: String, duration: Int) {
        var toastDuration = duration
        staticToast?.cancel()
        staticToast = Toast(context)
        when (duration) {
            SHORT -> toastDuration = Toast.LENGTH_SHORT
            LONG -> toastDuration = Toast.LENGTH_LONG
        }
        staticToast = Toast.makeText(context, text, toastDuration)
        staticToast?.show()
    }

    companion object {
        const val SHORT = 1
        const val LONG = 2
        private var staticToast: Toast? = null
    }
}