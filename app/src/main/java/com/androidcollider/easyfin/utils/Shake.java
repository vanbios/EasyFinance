package com.androidcollider.easyfin.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;


public class Shake {

    public static void highlightEditText(final EditText editText){
        editText.requestFocus();
        editText.setSelection(0, editText.length());
        shakeView(editText);
    }


    private static Animation shake;
    public static void shakeView(final View view){
        if(shake == null) {
            shake = new RotateAnimation(-1f, +1f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            shake.setRepeatCount(5);
            shake.setRepeatMode(Animation.REVERSE);
            shake.setDuration(80);
            shake.setInterpolator(new LinearInterpolator());
        }
        view.startAnimation(shake);
    }
}
