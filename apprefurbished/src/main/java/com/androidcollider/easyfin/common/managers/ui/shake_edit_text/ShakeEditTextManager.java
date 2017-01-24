package com.androidcollider.easyfin.common.managers.ui.shake_edit_text;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;

/**
 * @author Ihor Bilous
 */

public class ShakeEditTextManager {

    public void highlightEditText(final EditText editText) {
        editText.requestFocus();
        editText.setSelection(0, editText.length());
        shakeView(editText);
    }

    private Animation shake;

    private void shakeView(final View view) {
        if (shake == null) {
            shake = new RotateAnimation(-1f, +1f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            shake.setRepeatCount(5);
            shake.setRepeatMode(Animation.REVERSE);
            shake.setDuration(80);
            shake.setInterpolator(new LinearInterpolator());
        }
        view.startAnimation(shake);
    }
}