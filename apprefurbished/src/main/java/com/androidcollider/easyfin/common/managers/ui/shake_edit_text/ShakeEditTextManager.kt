package com.androidcollider.easyfin.common.managers.ui.shake_edit_text

import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.EditText

/**
 * @author Ihor Bilous
 */
class ShakeEditTextManager {

    fun highlightEditText(editText: EditText) {
        editText.requestFocus()
        editText.setSelection(0, editText.length())
        shakeView(editText)
    }

    private var shake: Animation? = null

    private fun shakeView(view: View) {
        if (shake == null) {
            shake = RotateAnimation(
                -1f,
                +1f,
                RotateAnimation.RELATIVE_TO_SELF,
                0.5f,
                RotateAnimation.RELATIVE_TO_SELF,
                0.5f
            )
            shake!!.repeatCount = 5
            shake!!.repeatMode = Animation.REVERSE
            shake!!.duration = 80
            shake!!.interpolator = LinearInterpolator()
        }
        view.startAnimation(shake)
    }
}