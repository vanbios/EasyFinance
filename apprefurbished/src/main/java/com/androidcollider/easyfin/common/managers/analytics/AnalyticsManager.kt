package com.androidcollider.easyfin.common.managers.analytics

import android.content.Context

/**
 * @author Ihor Bilous
 */
class AnalyticsManager internal constructor(context: Context?) {
    fun sendScreeName(screenName: String?) {}
    fun sendAction(s: String?, s1: String?, label: String?) {}
}