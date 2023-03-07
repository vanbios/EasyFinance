package com.androidcollider.easyfin.common.utils

import android.view.View
import android.view.animation.Animation

fun animateViewWithChangeVisibilityAndClickable(
    view: View,
    animation: Animation,
    visible: Boolean
) {
    animation.setAnimationListener(object : Animation.AnimationListener {

        override fun onAnimationStart(animation: Animation?) {
        }

        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
            view.isClickable = visible
        }
    })
    view.startAnimation(animation)
}