package com.androidcollider.easyfin.utils;


import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.fragments.FrgMain;

public class FloatingActionButtonBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    public FloatingActionButtonBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        float offset = 0;
        switch (child.getId()) {
            case R.id.btnFloatAddTransExpense:
                offset = FrgMain.offset1;
                break;
            case R.id.btnFloatAddTransIncome:
                offset = FrgMain.offset2;
                break;
            case R.id.btnFloatAddTransBTW:
                offset = FrgMain.offset3;
                break;
        }
        child.setTranslationY(offset + dependency.getTranslationY() - dependency.getHeight());
        return true;
    }

}