package com.androidcollider.easyfin.common.managers.ui.hide_touch_outside

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * @author Ihor Bilous
 */
class HideTouchOutsideManager {

    fun hideKeyboardByTouchOutsideEditText(view: View, activity: Activity) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v: View, event: MotionEvent ->
                val inputMethodManager =
                    activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                activity.currentFocus?.let {
                    inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    v.performClick()
                }
                false
            }
        }
        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                hideKeyboardByTouchOutsideEditText(innerView, activity)
            }
        }
    }
}