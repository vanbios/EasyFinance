package com.androidcollider.easyfin.common.managers.ui.hide_touch_outside;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @author Ihor Bilous
 */

public class HideTouchOutsideManager {

    public void hideKeyboardByTouchOutsideEditText(View view, final Activity activity) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (activity.getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }
                return false;
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboardByTouchOutsideEditText(innerView, activity);
            }
        }
    }
}