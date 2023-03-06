package com.androidcollider.easyfin.common.managers.rates.rates_info

import android.content.Context
import android.view.MotionEvent
import android.view.View
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.repository.Repository

/**
 * @author Ihor Bilous
 */
class RatesInfoManager internal constructor(
    private val repository: Repository,
    private val toastManager: ToastManager,
    private val resourcesManager: ResourcesManager
) {
    private var lastPressTime: Long = 0
    private var count = 0
    private lateinit var info: String

    init {
        prepareInfo()
    }

    fun setupMultiTapListener(view: View, context: Context) {
        view.setOnTouchListener { view1: View, motionEvent: MotionEvent ->
            val currentTime = System.currentTimeMillis()
            if (count == 0) lastPressTime = currentTime
            if (currentTime - lastPressTime < DOUBLE_PRESS_INTERVAL) {
                count++
                if (count == 7) {
                    toastManager.showClosableToast(context, info, ToastManager.LONG)
                }
            } else count = 0
            lastPressTime = currentTime
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                view1.performClick()
            }
            false
        }
    }

    fun prepareInfo() {
        repository.rates?.subscribe(
            { rates: DoubleArray ->
                val currency =
                    resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
                val sb = StringBuilder()
                for (i in 0..rates.size) {
                    sb.append(currency[i])
                    sb.append(" - ")
                    sb.append(if (i == 0) 1 else rates[i - 1])
                    sb.append("; ")
                }
                info = sb.toString()
            }, { obj: Throwable -> obj.printStackTrace() })
    }

    companion object {
        private const val DOUBLE_PRESS_INTERVAL: Long = 1000
    }
}